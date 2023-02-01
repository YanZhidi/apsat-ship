package com.zkthinke.modules.apsat.sync.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.Mail;
import cn.hutool.extra.mail.MailAccount;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.zkthinke.modules.apsat.ship.domain.*;
import com.zkthinke.modules.apsat.ship.mapper.*;
import com.zkthinke.modules.apsat.ship.repository.ShipLogErrorRepository;
import com.zkthinke.modules.apsat.ship.repository.SysConfigRepository;
import com.zkthinke.modules.apsat.ship.service.ShipRoutePlanEnclosureService;
import com.zkthinke.modules.apsat.ship.utils.FieldUnitUtil;
import com.zkthinke.modules.apsat.sync.service.ShipLogReadService;
import com.zkthinke.modules.common.utils.BinaryStringConverteUtil;
import com.zkthinke.modules.common.utils.GetFtp;
import com.zkthinke.utils.DateUtils;
import dk.tbsalling.aismessages.AISInputStreamReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ShipLogReadServiceImpl implements ShipLogReadService {
    @Value("${remote.log.host}")
    private String host;
    @Value("${remote.log.port}")
    private Integer port;
    @Value("${remote.log.username}")
    private String username;
    @Value("${remote.log.password}")
    private String password;
    @Value("${remote.log.timeout}")
    private Integer timeout;
    @Value("${remote.log.path}")
    private String remoteLogPath;
    @Value("${remote.log.folders}")
    private String folders;
    @Value("${local.log.path}")
    private String localLogPath;

    @Autowired
    DeviceInformationMapper deviceInformationMapper;

    /**
     * 航行最新记录表 仅文件解析内部使用。
     */
    @Autowired
    NavigationInformationMapper navigationInformationMapper;

    /**
     * 这个也是航行历史表，但是要使用的数据表更改因此更改。
     */
    @Autowired
    NavigationInformationHistoryMapper navigationInformationHistoryMapper;

    /**
     * 航行历史表
     */
    @Autowired
    ShipDetailMapper shipDetailMapper;

    @Autowired
    ShipDeviceMapper shipDeviceMapper;

    @Autowired
    ShipDeviceModMapper shipDeviceModMapper;

    @Autowired
    ShipRoutePlanEnclosureService shipRoutePlanEnclosureService;

    @Autowired
    ShipMapper shipMapper;

    @Autowired
    ShipVdmMapper shipVdmMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ErrorLogMapper errorLogMapper;

    @Autowired
    private ShipLogErrorRepository shipLogErrorRepository;

    @Autowired
    private SysConfigRepository sysConfigRepository;

    @Override
    public Integer readLogService() {
        try {
            log.info("解析日志开始");
            List<String> pathList = GetFtp.createChannelSftp(host, port, username, password, timeout, remoteLogPath, localLogPath,folders);
            // IS-00003 关于智慧航运202203的需求-5.1.	Log文件缺失监控告警
            Map<String, String> emailConfigMap = new HashMap<>();
            Map<String, String> alarmConfigMap = new HashMap<>();
            Map<String, String> shipfolderMap = new HashMap<>();
            List<String> lackHeaderList = new ArrayList<>();
            Set<String> logFolders = new HashSet<>(Arrays.asList(folders.split(",")));
            try {
                List<SysConfig> sysConfigs = new ArrayList<>();
                Object sysConfigObj = redisTemplate.opsForValue().get("ship_log_alarm_config");
                if (sysConfigObj != null) {
                    sysConfigs = JSONObject.parseObject((String) sysConfigObj, new TypeReference<List<SysConfig>>() {
                    }.getType());
                } else {
                    sysConfigs = sysConfigRepository.findAll((root, query, cb) -> cb.in(root.get("businessCode")).value(Arrays.asList(new String[]{"ship_log_alarm", "email_config"})));
                    redisTemplate.opsForValue().set("ship_log_alarm_config", JSONObject.toJSONString(sysConfigs), 30, TimeUnit.MINUTES);
                }
                // log.info("sysConfigs:" + JSONObject.toJSONString(sysConfigs));
                if (!CollectionUtils.isEmpty(sysConfigs)) {
                    emailConfigMap = sysConfigs.stream().filter(x -> "email_config".equals(x.getBusinessCode())).collect(Collectors.toMap(SysConfig::getCode, SysConfig::getValue));
                    alarmConfigMap = sysConfigs.stream().filter(x -> "ship_log_alarm".equals(x.getBusinessCode())).collect(Collectors.toMap(SysConfig::getCode, SysConfig::getValue));
                    // 监控船舶（json:{日志文件夹名:船名]）
                    if (alarmConfigMap.containsKey("monitor_ship")) {
                        shipfolderMap = JSONObject.parseObject(alarmConfigMap.get("monitor_ship"), Map.class);
                    }
                    // 监控船舶日志字头（,号分割）
                    if (alarmConfigMap.containsKey("monitor_log_title")) {
                        String monitorLogTitle = alarmConfigMap.get("monitor_log_title");
                        lackHeaderList = new ArrayList<>(Arrays.asList(monitorLogTitle.split(",")));
                    }
                }
            } catch (Exception e) {
                log.error("获取船舶日志监控配置异常!" + e.getMessage(), e);
            }
            // IS-00003 关于智慧航运202203的需求-5.1.	Log文件缺失监控告警
            try {
                for (String logFolder : logFolders) {
                    if (shipfolderMap.containsKey(logFolder)) {
                        String loseCycleKey = "ship_log_" + logFolder + "_loseAlarmTime";
                        String key = "ship_log_" + logFolder + "_getLastTime";
                        Object loseCycleVal = redisTemplate.opsForValue().get(loseCycleKey);
                        Object logFolderVal = redisTemplate.opsForValue().get(key);
                        if (logFolderVal != null) {
                            long loseCycleTime = 0L;
                            if (loseCycleVal != null) {
                                loseCycleTime = (long) loseCycleVal;
                            }
                            long lastTime = (long) logFolderVal;
                            long monitorLogCycle = Long.parseLong(alarmConfigMap.get("monitor_log_cycle"));
                            long logLoseCycle = Long.parseLong(alarmConfigMap.get("log_lose_cycle"));
                            if (System.currentTimeMillis() - lastTime >= logLoseCycle) {
                                // 告警...
                                ShipLogError logError = new ShipLogError();
                                logError.setShipName(shipfolderMap.get(logFolder));
                                logError.setErrorType("1");
                                logError.setErrorTypeName("Log文件获取异常");
                                logError.setRemark(logFolder);
                                logError.setCreateTime(new Date());
                                // 监控日志缺失开关（1:开，0:关）
                                if ("1".equals(alarmConfigMap.get("log_lose_switch"))) {
                                    if (System.currentTimeMillis() - loseCycleTime >= monitorLogCycle) {
                                        // 发送邮件
                                        Map<String, String> contentMap = new HashMap<>();
                                        contentMap.put("shipName", logError.getShipName());
                                        contentMap.put("logFolder", logFolder);
                                        contentMap.put("errorType", logError.getErrorTypeName());
                                        sendAlarmEmail(emailConfigMap, contentMap, alarmConfigMap);
                                        logError.setSendFlag(1);
                                        redisTemplate.opsForValue().set(loseCycleKey, System.currentTimeMillis());
                                    }
                                }
                                // 保存错误日志
                                shipLogErrorRepository.save(logError);

                                // 重置时间
                                redisTemplate.opsForValue().set(key, System.currentTimeMillis());
                            }
                        } else {
                            // 设置时间
                            redisTemplate.opsForValue().set(key, System.currentTimeMillis());
                        }
                        // if (path.indexOf(logFolder) > -1) {
                        //     // 获取到日志文件，更新时间
                        //     redisTemplate.opsForValue().set(key, System.currentTimeMillis());
                        // }
                    }
                }
            } catch (Exception e) {
                log.error("日志缺失监控检测异常!" + e.getMessage(), e);
            }
            // ==========================================================
            for (String path : pathList) {

                log.info("日志path" + path);
                Map myLog = getLog(path, lackHeaderList);
                try {
                    redisTemplate.opsForValue().set("ship_log_" + myLog.get("imonumber") + "_getLastTime", System.currentTimeMillis());
                    if (shipfolderMap.containsKey(myLog.get("imonumber"))) {
                        String alarmLogkey = "ship_log_" + myLog.get("imonumber") + "_alarmTime";
                        if (!CollectionUtils.isEmpty(lackHeaderList)) {
                            long monitorLogCycle = Long.parseLong(alarmConfigMap.get("monitor_log_cycle"));
                            // 告警...
                            ShipLogError logError = new ShipLogError();
                            logError.setShipName(shipfolderMap.get(myLog.get("imonumber")));
                            logError.setErrorType("2");
                            logError.setErrorTypeName("Log文件解析异常");
                            logError.setRemark("[" + myLog.get("fileName") + "]" + "；log文件缺失字头：" + org.apache.commons.lang3.StringUtils.join(lackHeaderList, ","));
                            logError.setCreateTime(new Date());
                            // 监控日志字头开关（1:开，0:关）
                            if ("1".equals(alarmConfigMap.get("monitor_log_title_switch"))) {
                                Object logAlarmTimeObj = redisTemplate.opsForValue().get(alarmLogkey);
                                boolean isCycleWarn = false;
                                if (null != logAlarmTimeObj) {
                                    long logAlarmTime = (long) logAlarmTimeObj;
                                    isCycleWarn = (System.currentTimeMillis() - logAlarmTime) >= monitorLogCycle;
                                }
                                if (isCycleWarn || logAlarmTimeObj == null) {
                                    // 时间大于周期时间或上次告警邮件发送时间为空时，发送邮件
                                    Map<String, String> contentMap = new HashMap<>();
                                    contentMap.put("shipName", logError.getShipName());
                                    contentMap.put("logPath", path);
                                    contentMap.put("errorType", logError.getErrorTypeName());
                                    sendAlarmEmail(emailConfigMap, contentMap, alarmConfigMap);
                                    logError.setSendFlag(1);
                                    redisTemplate.opsForValue().set(alarmLogkey, System.currentTimeMillis());
                                }
                            }
                            // 保存错误日志
                            shipLogErrorRepository.save(logError);
                        } else {
                            // 缺少字头list为空时，视为已修复或无告警，置空上次告警邮件发送时间
                            // redisTemplate.delete(alarmLogkey);
                        }
                    }
                } catch (Exception e) {
                    log.error("监控日志文件缺少字头异常!" + e.getMessage(), e);
                }
                if (myLog.containsKey("isAlarm")) {
                    String modbusAlarmKey = "ship_log_" + myLog.get("imonumber") + "_modbusAlarmTime";
                    try {
                        if ("1".equals((String) myLog.get("isAlarm"))) {
                            long monitorLogCycle = Long.parseLong(alarmConfigMap.get("monitor_log_cycle"));
                            // 告警...
                            ShipLogError logError = new ShipLogError();
                            logError.setShipName(shipfolderMap.get(myLog.get("imonumber")));
                            logError.setErrorType("3");
                            logError.setErrorTypeName("MODBUD或XDR日志解析异常");
                            logError.setRemark("[" + myLog.get("fileName") + "]；" + (String) myLog.get("alarmMsg"));
                            logError.setCreateTime(new Date());
                            // 监控日志字头开关（1:开，0:关）
                            if ("1".equals(alarmConfigMap.get("monitor_log_mx_switch"))) {
                                Object logAlarmTimeObj = redisTemplate.opsForValue().get(modbusAlarmKey);
                                boolean isCycleWarn = false;
                                if (null != logAlarmTimeObj) {
                                    long logAlarmTime = (long) logAlarmTimeObj;
                                    isCycleWarn = (System.currentTimeMillis() - logAlarmTime) >= monitorLogCycle;
                                }
                                if (isCycleWarn || logAlarmTimeObj == null) {
                                    // 时间大于周期时间或上次告警邮件发送时间为空时，发送邮件
                                    Map<String, String> contentMap = new HashMap<>();
                                    contentMap.put("shipName", logError.getShipName());
                                    contentMap.put("logPath", path);
                                    contentMap.put("errorType", logError.getErrorTypeName());
                                    sendAlarmEmail(emailConfigMap, contentMap, alarmConfigMap);
                                    logError.setSendFlag(1);
                                    redisTemplate.opsForValue().set(modbusAlarmKey, System.currentTimeMillis());
                                }
                            }
                            // 保存错误日志
                            shipLogErrorRepository.save(logError);
                        } else {
                            // isAlarm=false，视为已修复或无告警，置空上次告警邮件发送时间
                            // redisTemplate.delete(modbusAlarmKey);
                        }
                    } catch (Exception e) {
                        log.error("监控MODBUD或XDR日志解析异常告警异常!" + e.getMessage(), e);
                    }
                }
                log.info("获取到的map=={}",myLog);
                ShipDetailPO navigationInformationParPO = readData(myLog);
                if("invalidmmsi".equals(navigationInformationParPO.getName())){
                    continue;
                }
                ShipDevicePO deviceInformationParPO = readCode(myLog);
                Map<String,ShipVdmPO> shipVdmPOMap = readVDM(myLog);
                ShipDetailPO navigationInfo = null;

                /**
                 * 如果解析VLW时候 VLW语句有问题，则该条语句不落库
                 * 那么给navigationInformationParPO对象的imoNumber属性设置一个invalid
                 * 但是deviceInformation没有给imoNumber对象复制，所以在这里给imoNumber属性赋值
                 */
                deviceInformationParPO.setImoNumber(navigationInformationParPO.getImoNumber());

                /*如果远程服务器没有文件 不写入数据库*/
                if (!navigationInformationParPO.getImoNumber().equals("invalid") && !deviceInformationParPO.getImoNumber().equals("invalid")) {
                    if (navigationInformationParPO.getImoNumber().equals("invalid") || deviceInformationParPO.getImoNumber().equals("invalid")) {
                        log.info("文件内容有误不予落库");
                    } else {
                        /** Ship造值*/
                        /*插入缺少的值*/
                        deviceInformationParPO.setDataSyncTime(navigationInformationParPO.getDataSyncTime());
                        deviceInformationParPO.setCollectTime(DateUtils.Date2Long(navigationInformationParPO.getDataSyncTime()));
                        deviceInformationParPO.setName(navigationInformationParPO.getName() == null ? "NONAME" : navigationInformationParPO.getName());
                        navigationInformationParPO.setName(navigationInformationParPO.getName() == null ? "NONAME" : navigationInformationParPO.getName());
                        navigationInformationParPO.setCollectTime(DateUtils.Date2Long(navigationInformationParPO.getDataSyncTime()));
                        /**本次文件解析出来的目的地 先设置一个错误信息以防止信息为空,防止出现空指针异常的情况*/
                        String thisTimeDestination = "dataErr";

                        //上次目的地
                        String lastTimedestination = null;
                        //上次状态
                        String lastStatus = null;
//                        String destinationFlag = redisTemplate.opsForValue().get("destinationFlag").toString();
//                        if("1".equals(destinationFlag)){
//                            navigationInformationParPO.setDestination("SANDONGHAO");
////                            shipRoutePlanEnclosureService.insertAlarmForChangeTarget(navigationInformationParPO.getShipId().toString());
//                        }
                        if (navigationInformationParPO.getDestination() != null) {
                            //如果文件解析出来的目的地不为空，则赋值给 thisTimeDestination
                            thisTimeDestination = navigationInformationParPO.getDestination();
                        }
                        //上次解析出来的目的地 去数据库的t_navagation_infor表中查出来

                        //因为需要落一张航行最新表和一张航行历史表，因此这里使用了两个PO，两个PO的属性一样
                        ShipDetailHistoryPO navigationInformationHistoryPO = new ShipDetailHistoryPO();

                        //查询ship表 获取船的id
                        Long shipId = shipMapper.selectShipByMmsi(navigationInformationParPO.getMmsiNumber());
                        ShipPO shipPO = new ShipPO();
                        shipPO.setName(navigationInformationParPO.getName());
                        /**
                         * 如果shipId为空，代表该船第一次解析
                         * 则插入一条新的数据到ship表
                         */
                        if (shipId == null) {
                            shipPO.setMmsiNumber(navigationInformationParPO.getMmsiNumber());
                            shipPO.setImoNumber(navigationInformationParPO.getImoNumber());
                            shipPO.setCallSign(navigationInformationParPO.getCallSign());
                            shipPO.setUpdateTime(System.currentTimeMillis());
                            shipMapper.insert(shipPO);
                            shipId = shipPO.getId();
                        }
                        //设置ship_id
                        deviceInformationParPO.setShipId(shipId);
                        navigationInformationParPO.setShipId(shipId);

                        //去数据库中判断当前的数据在数据库中是否存在
                        //设备历史表利用船名 imo编号 数据同步时间来判断数据库中有无重复的数据 X
                        //利用船名 shipId 数据同步时间来判断数据库中有误重复的数据 √
                        int deviceCount = deviceInformationMapper.getCount(deviceInformationParPO.getName(), deviceInformationParPO.getShipId(), deviceInformationParPO.getDataSyncTime());
                        //航行最新表使用mmsi编号来判断库中有无重复数据
                        //航行最新表只有一条船的一条数据，所以该船只会有一条数据，mmsi在表中也是唯一的
                        int navigationCount = navigationInformationMapper.getCount(navigationInformationParPO.getMmsiNumber());
                        //航行历史表 使用mmsi和数据同步时间来判断有无重复数据
                        Integer navigationHistoryMapperCount = shipDetailMapper.getCount(navigationInformationParPO);

                        /**航线预警**/
                        String routeFlag = redisTemplate.opsForValue().get("routeFlag").toString();
                        String speedFlag = redisTemplate.opsForValue().get("speedFlag").toString();
                        String waterDeepFlag = redisTemplate.opsForValue().get("waterDeepFlag").toString();

                        /**预警信息需要文件解析时间**/
                        SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String timeDateString = timeFormatter.format(navigationInformationParPO.getDataSyncTime());
                        Long transTime = DateUtils.str2Long(timeDateString);
                        //如果flag为1 则改变经纬度
                        String latitude = navigationInformationParPO.getLatitude();
                        String longitude = navigationInformationParPO.getLongitude();
                        Double latitudeDouble =  Double.valueOf(latitude);
                        Double longitudeDouble = Double.valueOf(longitude);
                        if("1".equals(routeFlag)){
                            log.info("改变轨迹"+routeFlag);
                            latitudeDouble = Double.valueOf(latitude)-0.1;
                            longitudeDouble = Double.valueOf(longitude)-0.1;
                            navigationInformationParPO.setLongitude(longitudeDouble.toString());
                            navigationInformationParPO.setLatitude(latitudeDouble.toString());
                        }
                        shipRoutePlanEnclosureService.isInEnclosure(navigationInformationParPO.getShipId().toString(),longitudeDouble,latitudeDouble,transTime);
                        String maxStaticDraftValue = StringUtils.isEmpty(navigationInformationParPO.getMaxStaticDraft())?navigationInformationParPO.getMaxStaticDraft():navigationInformationParPO.getMaxStaticDraft().substring(0,navigationInformationParPO.getMaxStaticDraft().length()-1);

                        if("1".equals(waterDeepFlag)){
                            log.info("改变船舶最大吃水深度");
                            maxStaticDraftValue="80";
                        }
                        if (!StringUtils.isEmpty(navigationInformationParPO.getMaxStaticDraft())){
                            shipRoutePlanEnclosureService.isHitTheRock(navigationInformationParPO.getShipId().toString(),longitudeDouble,latitudeDouble,Double.parseDouble(maxStaticDraftValue),transTime);
                        }
                        if("1".equals(speedFlag)) {
                            deviceInformationParPO.setRevolutionSpeed("0RPM");
                            navigationInformationParPO.setGroundSpeed("1knots");
                        }
                        try {
                            Double revolutionSpeed = Double.valueOf(FieldUnitUtil.removeUnit(deviceInformationParPO.getRevolutionSpeed()));
                            Double groundSpeed = Double.valueOf(FieldUnitUtil.removeUnit(navigationInformationParPO.getGroundSpeed()));
                            shipRoutePlanEnclosureService.insertAlarmForNoPower(shipId.toString(), revolutionSpeed, groundSpeed, transTime, longitudeDouble, latitudeDouble);
                        }catch (Exception e){
                            e.printStackTrace();
                            log.error("文件缺少revolutionSpeed和groundSpeed数据");
                        }
                        //靠近暗礁岛屿告警
                        try {
                            shipRoutePlanEnclosureService.isCloseToReef(longitude,latitude,shipId.toString(),navigationInformationParPO.getCollectTime());
                        } catch (Exception e) {
                            log.error("靠近暗礁岛屿告警 异常：",e);
                        }
                        //靠近沉船告警
                        try {
                            shipRoutePlanEnclosureService.isCloseToShipwrecks(longitude,latitude,shipId.toString(),navigationInformationParPO.getCollectTime());
                        } catch (Exception e) {
                            log.error("靠近沉船告警 异常：",e);
                        }
                        //靠近台风告警
                        try {
                            shipRoutePlanEnclosureService.isCloseToTyphoon(longitude,latitude,shipId.toString(),navigationInformationParPO.getCollectTime());
                        } catch (Exception e) {
                            log.error("靠近台风告警 异常：",e);
                        }
                        //走锚告警
                        try {
                            String sailingStatus = navigationInformationParPO.getSailingStatus();
                            Long collectTime = navigationInformationParPO.getCollectTime();
                            shipRoutePlanEnclosureService.anchorWalkingWarning(shipId.toString(),sailingStatus,collectTime,longitude,latitude);
                        }catch (Exception e){
                            log.error("走锚预警异常：",e);
                        }
                        //搁浅风险预警
                        try {
                            String sensorDepth = navigationInformationParPO.getSensorDepth();
                            Long collectTime = navigationInformationParPO.getCollectTime();
                            shipRoutePlanEnclosureService.isStranding(shipId.toString(), sensorDepth, collectTime);
                        } catch (Exception e) {
                            log.error("搁浅风险预警异常：", e);
                        }
                        //人工围栏预警
                        try {
                            shipRoutePlanEnclosureService.isInArtificialFence(longitude,latitude,shipId.toString(),navigationInformationParPO.getCollectTime());
                        }catch (Exception e) {
                            log.error("人工围栏预警异常：", e);
                        }


//                        navigationInformationParPO.setName("散货1号");


                        /**
                         * 插入实时表 imo编号查询
                         * 如果上面的navigationCount 为0 则代表数据库中无此数据，做插入数据操作
                         * 如果上面的navigationCount 不为0 则代表数据库中有此数据，做更新数据操作
                         */
                        if (navigationCount == 0) {
                            navigationInformationParPO.setDeparture("startPire");//将船舶出发地设置为startPire
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String dateString = formatter.format(navigationInformationParPO.getDataSyncTime());
                            navigationInformationParPO.setCollectTime(DateUtils.str2Long(dateString));
                            navigationInformationParPO.setDepartureTime(DateUtils.Date2Long(navigationInformationParPO.getDataSyncTime()));
//                            navigationInformationParPO.setDestination(thisTimeDestination);
                            navigationInformationMapper.insert(navigationInformationParPO);
                        } else {
                            //如果该船在航行最新表中有数据，则根据mmsi获取之前的数据，更新
                            navigationInfo = navigationInformationMapper.getNavigation(navigationInformationParPO.getMmsiNumber());
                            log.info("navigationInfo:"+navigationInfo.toString());
                            lastTimedestination = navigationInfo.getDestination();
                            lastStatus = navigationInfo.getSailingStatus();
                            Long navigationInfoDepartureTime = navigationInfo.getDepartureTime();
                            navigationInformationParPO.setId(navigationInfo.getId());
                            //如果文件中没有解析出来本次目的
                            if ("dataErr".equals(thisTimeDestination)) {
//                            lastTimedestination = null;
                                navigationInformationParPO.setDeparture(navigationInfo.getDeparture());//将本次出发地设置成上一次的出发地
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String dateString = formatter.format(navigationInformationParPO.getDataSyncTime());
                                navigationInformationParPO.setDepartureTime(DateUtils.str2Long(dateString));
                            } else {//文件中解析出本次目的地
                                //如果这次的目的地和上次的目的地不同，则代表该船已经到达了上次的目的地，并从上次的目的地出发前往本次的目的地
                                if (!thisTimeDestination.equals(lastTimedestination) && lastTimedestination != null) {
                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String dateString = formatter.format(navigationInformationParPO.getDataSyncTime());
                                    navigationInformationParPO.setCollectTime(DateUtils.str2Long(dateString));
                                    //如果船舶处于 1 或5的状态，本次出发地为上次的目的地
                                    if ("系泊".equals(navigationInformationParPO.getSailingStatus())) {
                                        navigationInformationParPO.setDeparture(lastTimedestination);
                                        navigationInformationParPO.setDepartureTime(DateUtils.Date2Long(navigationInformationParPO.getDataSyncTime()));
                                    }else if("发动机使用中".equals(navigationInformationParPO.getSailingStatus()) && "系泊".equals(lastStatus)){
                                      //TODO 看上一次的状态是不是系泊
                                        navigationInformationParPO.setDeparture(lastTimedestination);
                                        navigationInformationParPO.setDepartureTime(DateUtils.Date2Long(navigationInformationParPO.getDataSyncTime()));
                                    } else {
                                        //如果船舶不为1或5的状态，本次出发地不变，出发时间不变，目的地改变
                                        navigationInformationParPO.setDeparture(navigationInfo.getDeparture());
                                        navigationInformationParPO.setDepartureTime(navigationInfo.getDepartureTime());
                                    }
                                } else {
                                    navigationInformationParPO.setDeparture(navigationInfo.getDeparture());
                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String dateString = formatter.format(navigationInformationParPO.getDataSyncTime());
                                    navigationInformationParPO.setCollectTime(DateUtils.str2Long(dateString));
                                    navigationInformationParPO.setDepartureTime(navigationInfoDepartureTime);
                                }
                            }
                            navigationInformationMapper.updateByPrimaryKey(navigationInformationParPO);
                        }
                        /** 航行历史表和航行最新表 COPY属性 */
                        BeanUtils.copyProperties(navigationInformationParPO, navigationInformationHistoryPO);
                        //设备历史表
                        if (deviceCount == 0) {
                            Integer insert = shipDeviceMapper.insert(deviceInformationParPO);
                            Long id = deviceInformationParPO.getId();
                            /**
                             *设备信息过多，另外新建了一张表用来存放MODBUS类型数据 t_ship_device_mod
                             * 用设备信息的id与t_ship_device_mod表中的device_id关联
                             */
                            List<ShipDeviceModPO> shipDeviceModPOList = deviceInformationParPO.getShipDeviceModPOList();
                            if (shipDeviceModPOList != null) {
                                for (int i = 0; i < shipDeviceModPOList.size(); i++) {
                                    ShipDeviceModPO shipDeviceModPO = shipDeviceModPOList.get(i);
                                    shipDeviceModPO.setDeviceId(id);
                                    shipDeviceModPO.setShipId(Integer.valueOf(deviceInformationParPO.getShipId().toString()));
                                    shipDeviceModPO.setDataSyncTime(String.valueOf(deviceInformationParPO.getCollectTime() + (i * 5000)));
                                }
                                shipDeviceModMapper.insert(shipDeviceModPOList);
                            }else {

                            }
                        } else {
                            ShipDevicePO deviceInfo = shipDeviceMapper.getDeviceInfoByImoNumber(deviceInformationParPO.getName(), deviceInformationParPO.getImoNumber(), deviceInformationParPO.getDataSyncTime());
                            deviceInformationParPO.setId(deviceInfo.getId());
                            shipDeviceMapper.updateByPrimaryKey(deviceInformationParPO);
                        }

                        //插入航行历史表 imo编号和同步时间查询
                        if (navigationHistoryMapperCount == 0) {
//                        navigationInformationHistoryPO.setDeparture(navigationInformationParPO.getDeparture());
                            ShipDetailPO navigation = new ShipDetailPO();
                            BeanUtils.copyProperties(navigationInformationParPO,navigation);
                            Long departureTime = navigation.getDepartureTime();
                            String departure = navigation.getDeparture();
                            navigationInformationHistoryPO.setDepartureTime(departureTime);
                            navigationInformationHistoryPO.setDeparture(departure);
                            navigationInformationHistoryPO.setDestination(navigation.getDestination());
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String dateString = formatter.format(navigationInformationParPO.getDataSyncTime());
                            navigationInformationHistoryPO.setCollectTime(DateUtils.str2Long(dateString));
                            shipDetailMapper.insert(navigationInformationHistoryPO);
                            shipPO.setLastDetailId(navigationInformationHistoryPO.getId());
                            int insert = shipMapper.updateLastDetailId(navigationInformationHistoryPO.getShipId(), shipPO.getLastDetailId(), shipPO.getName());
                            log.info("插入t_ship航行更新时间" + insert);
                        } else {
                            ShipDetailHistoryPO navigationHistory = shipDetailMapper.getNavigationHistory(navigationInformationHistoryPO.getMmsiNumber(), navigationInformationHistoryPO.getCollectTime());
//                        BeanUtils.copyProperties(navigationHistory,navigationInformationHistoryPO);
                            navigationInformationHistoryPO.setId(navigationHistory.getId());
                            shipDetailMapper.updateByPrimaryKey(navigationInformationHistoryPO);
                        }

                        //shipVdm数据写库
                        if (!shipVdmPOMap.isEmpty()){
                            List<ShipVdmPO> shipVdmList = new ArrayList<>(shipVdmPOMap.values());

                            List<ShipVdmPO> shipVdm1List = new ArrayList<>();
                            List<ShipVdmPO> shipVdm2List = new ArrayList<>();
                            List<ShipVdmPO> shipVdm3List = new ArrayList<>();
                            List<ShipVdmPO> shipVdm5List = new ArrayList<>();
                            List<ShipVdmPO> shipVdm18List = new ArrayList<>();
                            List<ShipVdmPO> shipVdm19List = new ArrayList<>();
                            List<ShipVdmPO> shipVdm24AList = new ArrayList<>();
                            List<ShipVdmPO> shipVdm24BList = new ArrayList<>();
                            List<ShipVdmPO> shipVdm27List = new ArrayList<>();
                            for (ShipVdmPO po : shipVdmList) {
                                po.setShipId(shipId);
                                String messageId = po.getMessageId();
                                if ("1".equals(messageId) ){
                                    shipVdm1List.add(po);
                                }else if ("2".equals(messageId)){
                                    shipVdm2List.add(po);
                                }else if ("3".equals(messageId)){
                                    shipVdm3List.add(po);
                                }else if ("5".equals(messageId)){
                                    shipVdm5List.add(po);
                                }else if ("18".equals(messageId)){
                                    shipVdm18List.add(po);
                                }else if ("19".equals(messageId)){
                                    shipVdm19List.add(po);
                                }else if ("24A".equals(messageId)){
                                    shipVdm24AList.add(po);
                                }else if ("24B".equals(messageId)){
                                    shipVdm24BList.add(po);
                                }else if ("27".equals(messageId)){
                                    shipVdm27List.add(po);
                                }
                            }

                            if (!CollectionUtils.isEmpty(shipVdm1List)){
                                shipVdmMapper.upsertVdm1List(shipVdm1List);
                            }
                            if (!CollectionUtils.isEmpty(shipVdm2List)){
                                shipVdmMapper.upsertVdm2List(shipVdm2List);
                            }
                            if (!CollectionUtils.isEmpty(shipVdm3List)){
                                shipVdmMapper.upsertVdm3List(shipVdm3List);
                            }
                            if (!CollectionUtils.isEmpty(shipVdm5List)){
                                shipVdmMapper.upsertVdm5List(shipVdm5List);
                            }
                            if (!CollectionUtils.isEmpty(shipVdm18List)){
                                shipVdmMapper.upsertVdm18List(shipVdm18List);
                            }
                            if (!CollectionUtils.isEmpty(shipVdm19List)){
                                shipVdmMapper.upsertVdm19List(shipVdm19List);
                            }
                            if (!CollectionUtils.isEmpty(shipVdm24AList)){
                                shipVdmMapper.upsertVdm24AList(shipVdm24AList);
                            }
                            if (!CollectionUtils.isEmpty(shipVdm24BList)){
                                shipVdmMapper.upsertVdm24BList(shipVdm24BList);
                            }
                            if (!CollectionUtils.isEmpty(shipVdm27List)){
                                shipVdmMapper.upsertVdm27List(shipVdm27List);
                            }
                        }
                    }
                } else {
                    log.info("文件服务器无日志，无法写入数据");
                }

                log.info("navigationInformationParPO"+navigationInformationParPO);
                log.info("deviceInformationParPO"+deviceInformationParPO);
            }
        } catch (Exception e) {
            log.error("日志",e);
            throw new RuntimeException(e.getMessage());
        }
        return 1;
    }

    //读取日志并放入到MAP中
    public static Map getLog(String path, List<String> lackHeaderList) {
        if (!path.equals("")) {
            try {
                log.info("result" + path);
                Map<String, Object> fileRead = new HashMap<>();
                File file = new File(path);
                log.info(file.getAbsolutePath());
                log.info("文件名:" + file.getName());
                String[] imonumber = file.getName().split("_");
                log.info(imonumber[0]);
                fileRead.put("fileName", file.getName());
                fileRead.put("path", file.getAbsolutePath());
                fileRead.put("imonumber", imonumber[0]);
                String[] dateSplit = imonumber[2].split("\\.");
                fileRead.put("dataSyncTime", dateSplit[0]);
                BufferedReader reader = null;
                try {
                    //log.info("以行为单位读取文件内容，一次读一整行：");
                    reader = new BufferedReader(new FileReader(file));
                    String tempString = null;
                    StringBuilder vdo1Sb = new StringBuilder();

                    StringBuilder vdm1Sb = new StringBuilder();
                    StringBuilder vdm5Sb = new StringBuilder();

                    List<Map<String, String>> listForXdr = new ArrayList<>();
                    List<Map<String, String>> modbusList1 = new ArrayList<>();
                    List<Map<String, String>> modbusList2 = new ArrayList<>();
                    List<Map<String, String>> modbusList3 = new ArrayList<>();
                    List<Map<String, String>> modbusList4 = new ArrayList<>();
                    List<Map<String, String>> modbusList5 = new ArrayList<>();
                    List<Map<String, String>> modbusList6 = new ArrayList<>();
                    List<Map<String, String>> modbusList7 = new ArrayList<>();
                    List<String> modbusList = new ArrayList<>();
                    boolean isFirstXDR = true;
                    boolean hasXdrInfo = false;
                    String lastXdrStr = "";
                    Map<String, Object> alarmMsgMap = new LinkedHashMap<>();
                    //是否读取到了对应的x/s雷达
                    boolean x_flag = false;
                    boolean s_flag = false;

                    while ((tempString = reader.readLine()) != null) {
                        lackHeaderList.remove(tempString);
                        // 显示行号
                        if (tempString.equals("")) {
                            // log.info("空行");
                        }else if ("[X-RADAR]".equals(tempString)) {
                            x_flag = true;
                            s_flag = false;
                        }else if ("[S-RADAR]".equals(tempString)) {
                            s_flag = true;
                            x_flag = false;
                        } else {
                            if (tempString.startsWith("!") || tempString.startsWith("$")) {
                                String[] split = tempString.split(",");
                                String index = split[0];
                                lackHeaderList.remove(index);
                                if (!index.equals("")) {
                                    String hasNext = split[1];
                                    if (index.startsWith("$")) {
                                        if (index.endsWith("VLW") && index.startsWith("$")) {
                                            fileRead.put("$VLW", tempString);
                                        } else if (index.endsWith("MWV") && index.startsWith("$")) {
                                            fileRead.put("$MWV", tempString);
                                        } else if (index.endsWith("DPT") && index.startsWith("$")) {
                                            fileRead.put("$DPT", tempString);
                                        } else if (index.endsWith("ZDA") && index.startsWith("$")) {
                                            fileRead.put("$ZDA", tempString);
                                        } else if (index.endsWith("TTM")) {
                                            if (x_flag){
                                                fileRead.put("$TTM_X", tempString);
                                            }
                                            if (s_flag){
                                                fileRead.put("$TTM_S", tempString);
                                            }
                                        }else if (index.endsWith("OSD")) {
                                            if (x_flag){
                                                fileRead.put("$OSD_X", tempString);
                                            }
                                            if (s_flag){
                                                fileRead.put("$OSD_S", tempString);
                                            }
                                        }else if (index.endsWith("RSA")) {
                                            fileRead.put("$RSA", tempString);
                                        }else if (index.endsWith("HTD")) {
                                            fileRead.put("$HTD", tempString);
                                        }else if (index.endsWith("VBW")) {
                                            fileRead.put("$VBW", tempString);
                                        } else if (index.endsWith("HDT")) {
                                            fileRead.put("$HDT", tempString);
                                        } else if (index.endsWith("ROT")) {
                                            fileRead.put("$ROT", tempString);
                                        } else if (index.endsWith("RMC")) {
                                            fileRead.put("$RMC", tempString);
                                        } else if (index.endsWith("VTG")) {
                                            fileRead.put("$VTG", tempString);
                                        } else if (index.endsWith("KTSX5")) {
                                            fileRead.put("$KTSX5", tempString);
                                        } else if (index.endsWith("XDR")) {
                                            hasXdrInfo = true;
                                            lastXdrStr = tempString;
                                            if (isFirstXDR){
                                                if (!tempString.contains("0102")){
                                                    fileRead.put("isAlarm", "1");
                                                    alarmMsgMap.put("XDR-F","xdr第一行没有0102");
                                                    fileRead.put("alarmMsg", JSONObject.toJSONString(alarmMsgMap));
                                                }
                                                isFirstXDR = false;
                                            }
                                            int length = split.length;
                                            for (int i = 4; i < length; i += 4) {
                                                Map<String, String> xdrMap = new HashMap<>();
                                                String s = split[i];
                                                if (i == length - 1) {
                                                    s = s.split("\\*")[0];
                                                }
                                                String key = null;
                                                //如果是515船，使用515自己的规则
                                                if("RG9H210716157B4761".equals(imonumber[0])){
                                                    key= numberForTypeTo515(s);
                                                }else{
                                                    //使用536解析规则
                                                    key = numberForType(s);
                                                }
                                                // String key = numberForType(s);
                                                String value = null;
                                                if ("S".equals(split[i - 3])) {
                                                    if ("0".equals(split[i - 2])) {
                                                        value = "OPEN";
                                                    } else if ("1".equals(split[i - 2])) {
                                                        value = "CLOSE";
                                                    }
                                                } else {
                                                    value = split[i - 2] + unit(split[i - 1]);
                                                }
                                                if (i == length - 1) {
                                                    key = key.split("\\*")[0];
                                                }
                                                xdrMap.put(key, value);
                                                listForXdr.add(xdrMap);
                                            }
                                        } else if (index.endsWith("FEC")) {
                                            fileRead.put("$FEC", tempString);
                                        }
                                    } else if (index.startsWith("!")) {
                                        if (index.endsWith("VDO")) {
                                            vdo1Sb.append(tempString).append("\n");
                                            fileRead.put("VDO", vdo1Sb.toString());
                                        }else if (index.endsWith("VDM") && hasNext.equals("1")){
                                            vdm1Sb.append(tempString).append("\n");
                                            fileRead.put("VDM1", vdm1Sb.toString());
                                        }else if (index.endsWith("VDM") && hasNext.equals("2")){
                                            vdm5Sb.append(tempString).append("\n");
                                            fileRead.put("VDM5", vdm5Sb.toString());
                                        }
                                    }
                                }
                            } else if (tempString.startsWith("(") && (tempString.trim().length() == 82 || tempString.trim().length() == 88) || tempString.trim().length() == 46) {
                                //读到这里说明上面带$的已经读取完毕这里往map终放入
                                if (hasXdrInfo && !lastXdrStr.contains("0964")) {
                                    fileRead.put("isAlarm", "1");
                                    alarmMsgMap.put("XDR-L","xdr最后一行没有0964");
                                    fileRead.put("alarmMsg", JSONObject.toJSONString(alarmMsgMap));
                                } else {
                                    fileRead.put("$XDR", listForXdr);
                                }

                                String ss2 = tempString.trim().substring(19);
                                String date = tempString.trim().substring(1, 18);
                                String userFulString = ss2.replaceAll(" ", "");
                                if (userFulString.startsWith("0A")) {
                                    Map<String, String> map = new HashMap<>();
                                    map.put(date, ss2);
                                    modbusList7.add(map);
                                } else{
                                    modbusList.add(tempString);
                                }
                                // if (userFulString.startsWith("01")) {
                                //     Map<String, String> map = new HashMap<>();
                                //     map.put(date, ss2);
                                //     modbusList1.add(map);
                                // } else if (userFulString.startsWith("02")) {
                                //     Map<String, String> map = new HashMap<>();
                                //     map.put(date, ss2);
                                //     modbusList2.add(map);
                                // } else if (userFulString.startsWith("03")) {
                                //     Map<String, String> map = new HashMap<>();
                                //     map.put(date, ss2);
                                //     modbusList3.add(map);
                                // } else if (userFulString.startsWith("04")) {
                                //     Map<String, String> map = new HashMap<>();
                                //     map.put(date, ss2);
                                //     modbusList4.add(map);
                                // } else if (userFulString.startsWith("05")) {
                                //     Map<String, String> map = new HashMap<>();
                                //     map.put(date, ss2);
                                //     modbusList5.add(map);
                                // }else if (userFulString.startsWith("06")) {
                                //     Map<String, String> map = new HashMap<>();
                                //     map.put(date, ss2);
                                //     modbusList6.add(map);
                                // }else if (userFulString.startsWith("0A")) {
                                //     Map<String, String> map = new HashMap<>();
                                //     map.put(date, ss2);
                                //     modbusList7.add(map);
                                // }
                                // fileRead.put("01", modbusList1);
                                // fileRead.put("02", modbusList2);
                                // fileRead.put("03", modbusList3);
                                // fileRead.put("04", modbusList4);
                                // fileRead.put("05", modbusList5);
                                // fileRead.put("06", modbusList6);
                                fileRead.put("07", modbusList7);
                            }
                        }
                    }

                    // IS-00002 关于智慧航运首页、船舶详情、辅助驾驶、后台管理优化功能的需求-5.5.船舶日志解析规则优化
                    Map<String, List<String>> modbusMap = new LinkedHashMap<>();
                    List<String> sectionModbusList = new ArrayList<>();
                    try {
                        // 文件时间
                        Date fileDate = DateUtil.parse(dateSplit[0], DatePattern.PURE_DATETIME_PATTERN);
                        Calendar c = Calendar.getInstance();
                        c.setTime(fileDate);
                        c.add(Calendar.SECOND, -30);
                        // 时间区间，5s为一个区间
                        Map<String, String> sectionMap = new LinkedHashMap<>();
                        for (int i = 0; i < 30; i += 5) {
                            int second = c.get(Calendar.SECOND) + i;
                            sectionMap.put(second + "", second + "-" + (second + 5));
                        }
                        // 按时间区间聚合数据
                        if (!CollectionUtils.isEmpty(modbusList)) {
                            for (String section : sectionMap.keySet()) {
                                String[] du = sectionMap.get(section).split("-");
                                List<String> modbuss = new ArrayList<>();
                                for (String modbus : modbusList) {
                                    String modbusSecond = modbus.trim().substring(16, 18);
                                    if (Integer.parseInt(du[0]) <= Integer.parseInt(modbusSecond) && Integer.parseInt(du[1]) > Integer.parseInt(modbusSecond)) {
                                        modbuss.add(modbus);
                                    }
                                }
                                modbusMap.put(section, modbuss);
                            }
                        }
                        if (!CollectionUtils.isEmpty(modbusMap)) {
                            boolean isAlarm = false;
                            List<String> alarmList = new ArrayList<>();
                            int modbusNullCount = 0;
                            for (String key : modbusMap.keySet()) {
                                List<String> modbusArr = modbusMap.get(key);
                                if (modbusArr.size() == 0) {
                                    modbusNullCount++;
                                }
                                if ("RG9H210716157B47A3".equals(imonumber[0]) || "RG9H201026157B44A0".equals(imonumber[0])) {
                                    //神华805 RG9H210716157B47A3；神华812 RG9H201026157B44A0 有modbus6
                                    if (modbusArr.size() == 6) {
                                        sectionModbusList.addAll(modbusArr);
                                    } else if (modbusArr.size() > 0){
                                        isAlarm = true;
                                        alarmList.addAll(modbusArr);
                                    } else {
                                        log.info(imonumber[0] + ",时间区间" + key + ",为空...");
                                    }
                                } else {
                                    if (modbusArr.size() == 5) {
                                        sectionModbusList.addAll(modbusArr);
                                    } else if (modbusArr.size() > 0){
                                        isAlarm = true;
                                        alarmList.addAll(modbusArr);
                                    } else {
                                        log.info(imonumber[0] + ",时间区间" + key + ",为空...");
                                    }
                                }
                            }
                            if (modbusNullCount == modbusMap.keySet().size()) {
                                fileRead.put("isAlarm", "1");
                                alarmMsgMap.put("MODBUS", "MODBUS为空");
                                fileRead.put("alarmMsg", JSONObject.toJSONString(alarmMsgMap));
                            }
                            if (isAlarm) {
                                fileRead.put("isAlarm", "1");
                                alarmMsgMap.put("MODBUS", alarmList);
                                fileRead.put("alarmMsg", JSONObject.toJSONString(alarmMsgMap));
                            }
                        }
                    } catch (Exception e) {
                        log.error("按时间区间聚合modbus数据异常!" + e.getMessage(), e);
                    }
                    if (CollectionUtils.isEmpty(sectionModbusList)){
                        sectionModbusList = modbusList;
                    }
                    if (!CollectionUtils.isEmpty(sectionModbusList)) {
                        for (String modbus : sectionModbusList) {
                            String ss2 = modbus.trim().substring(19);
                            String date = modbus.trim().substring(1, 18);
                            String userFulString = ss2.replaceAll(" ", "");
                            if (userFulString.startsWith("01")) {
                                Map<String, String> map = new HashMap<>();
                                map.put(date, ss2);
                                modbusList1.add(map);
                            } else if (userFulString.startsWith("02")) {
                                Map<String, String> map = new HashMap<>();
                                map.put(date, ss2);
                                modbusList2.add(map);
                            } else if (userFulString.startsWith("03")) {
                                Map<String, String> map = new HashMap<>();
                                map.put(date, ss2);
                                modbusList3.add(map);
                            } else if (userFulString.startsWith("04")) {
                                Map<String, String> map = new HashMap<>();
                                map.put(date, ss2);
                                modbusList4.add(map);
                            } else if (userFulString.startsWith("05")) {
                                Map<String, String> map = new HashMap<>();
                                map.put(date, ss2);
                                modbusList5.add(map);
                            } else if (userFulString.startsWith("06")) {
                                Map<String, String> map = new HashMap<>();
                                map.put(date, ss2);
                                modbusList6.add(map);
                            }
                        }
                        fileRead.put("01", modbusList1);
                        fileRead.put("02", modbusList2);
                        fileRead.put("03", modbusList3);
                        fileRead.put("04", modbusList4);
                        fileRead.put("05", modbusList5);
                        fileRead.put("06", modbusList6);
                    }
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e1) {
                        }
                    }
                }
                return fileRead;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            log.info("读取日志服务出错，/usr/log/sensor没有本地文件");
        }
        return null;
    }

    public ShipDetailPO readData(Map<String, Object> map) {
        ShipDetailPO navigationInformationPO = new ShipDetailPO();
        if (map != null) {
            try {
                String imonumber = (String) map.get("imonumber");
                navigationInformationPO.setImoNumber(imonumber);

                String syncDate = (String) map.get("dataSyncTime");
                navigationInformationPO.setDataSyncTime(new SimpleDateFormat("yyyyMMddHHmmss").parse(syncDate));

                String mmsi;
                if (map.containsKey("$MWV")) {
                    try {
                        String MWV = (String) map.get("$MWV");
                        String[] mnvSplit = MWV.split(",");
                        String type = mnvSplit[0];
                        String windAngle;
                        if (StringUtils.isEmpty(mnvSplit[1])) {
                            windAngle = 0 + "°";
                        } else {
                            windAngle = mnvSplit[1] + "°";
                        }
                        String reference = mnvSplit[2];
                        String weedSpeed;
                        if (StringUtils.isEmpty(mnvSplit[3])) {
                            weedSpeed = "0";
                        } else {
                            weedSpeed = mnvSplit[3];
                        }
                        String speedUnit;
                        if (StringUtils.isEmpty(mnvSplit[4])) {
                            speedUnit = "0";
                        } else {
                            speedUnit = mnvSplit[4];
                        }
                        String mnvS = mnvSplit[5].split("\\*")[0];

                        if (speedUnit.equals("N")) {
                            weedSpeed = weedSpeed + "knots";
                        } else if (speedUnit.equals("M")) {
                            weedSpeed = weedSpeed + "m/s";
                        } else if (speedUnit.equals("K")) {
                            weedSpeed = weedSpeed + "km/h";
                        }
                        navigationInformationPO.setRelativeWind(windAngle);
                        navigationInformationPO.setWindSpeed(weedSpeed);
                    } catch (Exception e) {
                        log.info("文件内容$MWV内容有误"+e);
                        navigationInformationPO.setImoNumber("invalid");
                        sendErrorMessage(new ErrorLog("文件内容$MWV内容有误",(String)map.get("fileName")));
                    }
                } else {
                    log.info("文件缺少$MWV数据");
                    sendErrorMessage(new ErrorLog("文件缺少$MWV数据",(String)map.get("fileName")));

                }

                if (map.containsKey("$DPT")) {
                    try {
                        String DPT = (String) map.get("$DPT");
                        String[] dptSplit = DPT.split(",");
                        String waterDepth;
                        String offset;
                        String maxinumRange;
                        if (StringUtils.isEmpty(dptSplit[1])) {
                            waterDepth = 0 + "m";
                        } else {
                            waterDepth = dptSplit[1].replaceFirst("^0*", "") + "m";
                        }
                        //如果传为000.0 则会出现.0m的情况
                        if (".0m".equals(waterDepth)) {
                            waterDepth = "0m";
                        }
                        if (StringUtils.isEmpty(dptSplit[2])) {
                            offset = 0 + "m";
                        } else {
                            offset = dptSplit[2] + "m";
                        }

                        if (StringUtils.isEmpty(dptSplit[3])) {
                            maxinumRange = "0";
                        } else {
                            maxinumRange = dptSplit[3].split("\\*")[0];
                        }
                        navigationInformationPO.setSensorDepth(waterDepth);
                        navigationInformationPO.setSwsd(offset);
                    } catch (Exception e) {
                        log.info("文件内容$DPT内容有误"+e);
                        navigationInformationPO.setImoNumber("invalid");
                        sendErrorMessage(new ErrorLog("文件内容$DPT内容有误",(String)map.get("fileName")));

                    }
                } else {
                    log.info("文件缺少$DPT数据");
                    sendErrorMessage(new ErrorLog("文件缺少$DPT数据",(String)map.get("fileName")));
                }

                if (map.containsKey("$VLW")) {
                    try {
                        String VLW = (String) map.get("$VLW");
                        String[] vlwSplit = VLW.split(",");
                        String totalCumulativeWaterDistance;
                        String waterDistanceSinceReset;
                        String totalCumulativeGroundDistance;
                        String groundDistanceSinceReset;
                        if (!StringUtils.isEmpty(vlwSplit[1])) {
                            totalCumulativeWaterDistance = vlwSplit[1] + "nm";
                        } else {
                            totalCumulativeWaterDistance = 0 + "nm";
                        }
                        if (StringUtils.isEmpty(vlwSplit[3])) {
                            waterDistanceSinceReset = 0 + "nm";
                        } else {
                            waterDistanceSinceReset = vlwSplit[3] + "nm";
                        }
                        //SH的日志文件不需要这两个字段
//                        if(StringUtils.isEmpty(vlwSplit[5])) {
//                            totalCumulativeGroundDistance = 0 + "nm";
//                        }else {
//                            totalCumulativeGroundDistance = vlwSplit[5] + "nm";
//                        }
//                        if(StringUtils.isEmpty(vlwSplit[7])){
//                            groundDistanceSinceReset = 0 + "nm";
//                        }else {
//                            groundDistanceSinceReset = vlwSplit[7] + "nm";
//                        }
                        navigationInformationPO.setTotalVoyage(totalCumulativeWaterDistance);
                        navigationInformationPO.setResetVoyage(waterDistanceSinceReset);
                    } catch (Exception e) {
                        log.info("文件内容$VLW内容有误"+e);
                        navigationInformationPO.setImoNumber("invalid");
                        sendErrorMessage(new ErrorLog("文件内容$VLW内容有误",(String)map.get("fileName")));
                        e.printStackTrace();
                    }
                } else {
                    log.info("文件缺少$VLW数据");
                    sendErrorMessage(new ErrorLog("文件缺少$VLW数据",(String)map.get("fileName")));
                }

                if (map.containsKey("VDO")) {
                    try {
                        String vdo = (String) map.get("VDO");
                        AtomicBoolean vdo1Flag = new AtomicBoolean(false);
                        AtomicBoolean vdo5Flag = new AtomicBoolean(false);
                        AISInputStreamReader vdoAis = new AISInputStreamReader(new ByteArrayInputStream(vdo.getBytes()), msg -> {
                            Integer messageId = msg.getMessageType().getCode();
                            Integer mmsiNumber = msg.getSourceMmsi().getMMSI();
                            navigationInformationPO.setName(getShipName(mmsiNumber.toString(), (String) map.get("fileName")));
                            Map<String, Object> dataFields = msg.dataFields();
                            navigationInformationPO.setMmsiNumber(mmsiNumber.toString());
                            String line = msg.getNmeaMessages()[0].getRawMessage();
                            String vdoStr = line.split(",")[5];
                            String vdo1Acsii = BinaryStringConverteUtil.stringToAscii(vdoStr);
                            String[] vdoSplit = vdo1Acsii.split(",");
                            StringBuilder vdo1bitTOmessage = new StringBuilder();
                            for (String s2 : vdoSplit) {
                                String bit6 = BinaryStringConverteUtil.to6Binary(Integer.valueOf(s2));
                                vdo1bitTOmessage.append(bit6);
                            }

                            if (messageId == 1) {

                                //航行状态
                                String navigationStatus = convertStatus((String) dataFields.get("navigationStatus"));
                                navigationInformationPO.setSailingStatus(navigationStatus);

                                //旋转速率
                                Integer rateOfTurn = convertRateOfTurn(bitToinfo(vdo1bitTOmessage.substring(42, 50)));
                                navigationInformationPO.setSteeringSpeed(rateOfTurn == -128 ? "invalid" : rateOfTurn + "°/min");

                                //SOG
                                Float speedOverGround = (Float) dataFields.get("speedOverGround");
                                navigationInformationPO.setGroundSpeed(speedOverGround >= 102.3 ? "invalid" : speedOverGround + "knots");

                                //经度
                                Integer longitudeA = bitToinfo(vdo1bitTOmessage.substring(61, 89));
                                navigationInformationPO.setLongitude(convertLongitudeAndLatitude(longitudeA,true,true));

                                //纬度
                                Integer latitudeA = bitToinfo(vdo1bitTOmessage.substring(89, 116));
                                navigationInformationPO.setLatitude(convertLongitudeAndLatitude(latitudeA,false,true));

                                //COG
                                Float courseOverGround = (Float) dataFields.get("courseOverGround");
                                navigationInformationPO.setCog(courseOverGround >= 360 ? "invalid" : courseOverGround + "°");

                                //实际航向
                                Integer trueHeading = (Integer) dataFields.get("trueHeading");
                                navigationInformationPO.setShipHead(trueHeading == 511 ? "invalid" : trueHeading + "°");

                                vdo1Flag.set(true);
                            } else if (messageId == 5) {

                                //imo编号
                                Integer imo = (Integer) dataFields.get("imo.IMO");
                                navigationInformationPO.setImoNumber(imo.toString());

                                //呼号
                                String callsign = (String) dataFields.get("callsign");
                                navigationInformationPO.setCallSign(callsign);

                                //名称
                                String shipNameInFile = (String) dataFields.get("shipName");
                                String shipName = getShipName(mmsiNumber.toString(),(String)map.get("fileName"));
                                navigationInformationPO.setName(shipName);

                                //船舶和货物类型
                                String shipType = (String) dataFields.get("shipType");
                                navigationInformationPO.setType(shipType);

                                //估计到达时间
                                String etaDateTime = convertETA((String) dataFields.get("eta"));
                                navigationInformationPO.setEta(DateUtils.str2Long(etaDateTime));

                                //目前最大静态吃水
                                Float draught = (Float) dataFields.get("draught");
                                navigationInformationPO.setMaxStaticDraft(draught + "m");

                                //目的地
                                String destination = (String) dataFields.get("destination");
                                navigationInformationPO.setDestination(destination);
                                vdo5Flag.set(true);
                            }
                        });
                        vdoAis.run();
                        if ("invalidmmsi".equals(navigationInformationPO.getName())){
                            return navigationInformationPO;
                        }
                        mmsi = navigationInformationPO.getMmsiNumber();

                        if (!vdo1Flag.get()){
                            log.info("文件缺少VDO1号信息数据");
                            navigationInformationPO.setImoNumber("invalid");
                        }

                        if (!vdo5Flag.get()){
                            log.info("文件缺少VDO5号信息数据");
                            if ("invalid".equals(mmsi)) {
                                navigationInformationPO.setImoNumber("invalid");
                            } else {
                                ShipDetailPO navigation = navigationInformationMapper.getNavigation(mmsi);
                                if(navigation!=null) {
                                    navigationInformationPO.setCallSign(navigation.getCallSign());
                                    navigationInformationPO.setName(navigation.getName());
                                    navigationInformationPO.setType(navigation.getType());
                                    navigationInformationPO.setEta(navigation.getEta());
                                    navigationInformationPO.setDestination(navigation.getDestination());
                                    navigationInformationPO.setMaxStaticDraft(navigation.getMaxStaticDraft());
                                    navigationInformationPO.setImoNumber(navigation.getImoNumber());
                                }else {
                                    log.error("文件首次运行即缺少VDO5类");
                                }
                            }
                            sendErrorMessage(new ErrorLog("文件首次运行即缺少VDO5类",(String)map.get("fileName")));
                        }

                    } catch (IOException e) {
                        log.error("文件内容vdo信息有误：",e);
                        navigationInformationPO.setImoNumber("invalid");
                        sendErrorMessage(new ErrorLog("文件内容vdo信息有误",(String)map.get("fileName")));
                    }
                }else {
                    log.info("文件缺少VDO数据");
                    navigationInformationPO.setImoNumber("invalid");
                    sendErrorMessage(new ErrorLog("文件缺少VDO数据",(String)map.get("fileName")));
                }

                if (map.containsKey("$HDT")) {
                    try {
                        String HDT = (String) map.get("$HDT");
                        String[] hdtSplit = HDT.split(",");
                        String s = hdtSplit[1];
                        navigationInformationPO.setHdtHeading(s);
                    } catch (Exception e) {
                        log.info("文件内容$HDT内容有误"+e);
                        sendErrorMessage(new ErrorLog("文件内容$HDT内容有误",(String)map.get("fileName")));
                        e.printStackTrace();
                    }
                } else {
                    log.info("文件缺少$HDT数据");
                    sendErrorMessage(new ErrorLog("文件缺少$HDT数据",(String)map.get("fileName")));
                }

                if (map.containsKey("$ROT")) {
                    try {
                        String ROT = (String) map.get("$ROT");
                        String[] rotSplit = ROT.split(",");
                        String s = rotSplit[1];
                        s = Double.valueOf(s).toString();
                        navigationInformationPO.setRotTurnRate(s);
                    } catch (Exception e) {
                        log.info("文件内容$rot内容有误"+e);
                        sendErrorMessage(new ErrorLog("文件内容$rot内容有误",(String)map.get("fileName")));
                        e.printStackTrace();
                    }
                } else {
                    log.info("文件缺少$rot数据");
                    sendErrorMessage(new ErrorLog("文件缺少$rot数据",(String)map.get("fileName")));
                }

                if (map.containsKey("$ZDA")) {
                    try {
                        String ZDA = (String) map.get("$ZDA");
                        String[] zdaSplit = ZDA.split(",");
                        String timeZone = zdaSplit[5].split("\\*")[0];
                        String year = zdaSplit[4];
                        String month = zdaSplit[3];
                        String day = zdaSplit[2];
                        String hour = zdaSplit[1].split("\\.")[0];
                        String timeStamp = year + "-" + month + "-" + day + " " + hour;
                        Long aLong = DateUtils.str2Longss(timeStamp);
                        log.info("timeStamp" + aLong);
                        if ("-08".equals(timeZone)) {
                            aLong = aLong + 28800000;
                        } else if ("+08".equals(timeZone)) {
                            aLong = aLong - 28800000;
                        }
                        String timeAfter = DateUtils.long2Str(aLong);
                        navigationInformationPO.setZdaTime(timeAfter);
                        navigationInformationPO.setZdaTimeZone(timeZone);
                    } catch (Exception e) {
                        log.info("文件内容$zda内容有误"+e);
                        sendErrorMessage(new ErrorLog("文件内容$zda内容有误",(String)map.get("fileName")));
                        e.printStackTrace();
                    }
                } else {
                    log.info("文件缺少$zda数据");
                    sendErrorMessage(new ErrorLog("文件缺少$zda数据",(String)map.get("fileName")));
                }

                if (map.containsKey("$KTSX5")) {
                    try {
                        String KTSX5 = (String) map.get("$KTSX5");
                        String[] ktsx5Split = KTSX5.split(",");
                        String pstSpeed = ktsx5Split[1];
                        String pstTorque = ktsx5Split[2];
                        String pstThrust = ktsx5Split[3];
                        String pstPower = ktsx5Split[4].split("\\*")[0];
                        navigationInformationPO.setPstSpeed(pstSpeed + "rev/min");
                        navigationInformationPO.setPstTorque(pstTorque + "kNm");
                        navigationInformationPO.setPstThrust(pstThrust + "kNm");
                        navigationInformationPO.setPstPower(pstPower + "kW");

                    } catch (Exception e) {
                        log.info("文件内容$KTSX5内容有误"+e);
                        sendErrorMessage(new ErrorLog("文件内容$KTSX5内容有误",(String)map.get("fileName")));
                        e.printStackTrace();
                    }
                } else {
                    log.info("文件缺少$KTSX5数据");
                    sendErrorMessage(new ErrorLog("文件缺少$KTSX5数据",(String)map.get("fileName")));
                }

                if (map.containsKey("$TTM_X")||map.containsKey("$TTM_S")) {
                    try {
                        if (map.containsKey("$TTM_S")){
                            String TTM = (String) map.get("$TTM_S");
                            String[] rotSplit = TTM.split(",");
                            String targetNumber = rotSplit[1];
                            String targetDistacefromOwnShip = rotSplit[2];
                            String targetAzimuthAngle = rotSplit[3]+rotSplit[4];
                            String targetSpeed = rotSplit[5];
                            String targetCourseDegree = rotSplit[6]+rotSplit[7];
                            String distanceofClosest = rotSplit[8];
                            String timetoCPA = rotSplit[9];
                            String speedDidtanceunits = rotSplit[10];
                            String targetName = rotSplit[11];
                            String targetStatus1 = rotSplit[12];
                            String referenceTarget2 = rotSplit[13];
                            String timeofDataStr = rotSplit[14];
                            String timeofData = null;
                            if (StrUtil.isNotEmpty(timeofDataStr)){
                                DateTime parse = DateUtil.parse(timeofDataStr.split("\\.")[0], "hhmmss");
                                timeofData = DateUtil.format(parse,"HH:mm:ss");
                            }
                            String typeofAcquistition = rotSplit[15].split("\\*")[0];
                            navigationInformationPO.setTargetNumber(targetNumber);
                            navigationInformationPO.setTargetDistacefromOwnShip(targetDistacefromOwnShip);
                            navigationInformationPO.setTargetAzimuthAngle(targetAzimuthAngle);
                            navigationInformationPO.setTargetSpeed(targetSpeed);
                            navigationInformationPO.setTargetCourseDegree(targetCourseDegree);
                            navigationInformationPO.setDistanceofClosest(distanceofClosest);
                            navigationInformationPO.setTimetoCPA(timetoCPA);
                            navigationInformationPO.setSpeedDidtanceunits(speedDidtanceunits);
                            navigationInformationPO.setTargetName(targetName);
                            navigationInformationPO.setTargetStatus1(targetStatus1);
                            navigationInformationPO.setReferenceTarget2(referenceTarget2);
                            navigationInformationPO.setTimeofData(timeofData);
                            navigationInformationPO.setTypeofAcquistition(typeofAcquistition);
                        }

                        if (map.containsKey("$TTM_X")){
                            String TTM = (String) map.get("$TTM_X");
                            String[] rotSplit = TTM.split(",");
                            String targetNumber = rotSplit[1];
                            String targetDistacefromOwnShip = rotSplit[2];
                            String targetAzimuthAngle = rotSplit[3]+rotSplit[4];
                            String targetSpeed = rotSplit[5];
                            String targetCourseDegree = rotSplit[6]+rotSplit[7];
                            String distanceofClosest = rotSplit[8];
                            String timetoCPA = rotSplit[9];
                            String speedDidtanceunits = rotSplit[10];
                            String targetName = rotSplit[11];
                            String targetStatus1 = rotSplit[12];
                            String referenceTarget2 = rotSplit[13];
                            String timeofDataStr = rotSplit[14];
                            String timeofData = null;
                            if (StrUtil.isNotEmpty(timeofDataStr)){
                                DateTime parse = DateUtil.parse(timeofDataStr.split("\\.")[0], "hhmmss");
                                timeofData = DateUtil.format(parse,"HH:mm:ss");
                            }
                            String typeofAcquistition = rotSplit[15].split("\\*")[0];
                            navigationInformationPO.setTargetNumber(targetNumber);
                            navigationInformationPO.setTargetDistacefromOwnShip(targetDistacefromOwnShip);
                            navigationInformationPO.setTargetAzimuthAngle(targetAzimuthAngle);
                            navigationInformationPO.setTargetSpeed(targetSpeed);
                            navigationInformationPO.setTargetCourseDegree(targetCourseDegree);
                            navigationInformationPO.setDistanceofClosest(distanceofClosest);
                            navigationInformationPO.setTimetoCPA(timetoCPA);
                            navigationInformationPO.setSpeedDidtanceunits(speedDidtanceunits);
                            navigationInformationPO.setTargetName(targetName);
                            navigationInformationPO.setTargetStatus1(targetStatus1);
                            navigationInformationPO.setReferenceTarget2(referenceTarget2);
                            navigationInformationPO.setTimeofData(timeofData);
                            navigationInformationPO.setTypeofAcquistition(typeofAcquistition);
                        }

                    } catch (Exception e) {
                        log.info("文件内容$TTM内容有误"+e);
                        sendErrorMessage(new ErrorLog("文件内容$TTM内容有误",(String)map.get("fileName")));
                        e.printStackTrace();
                    }
                } else {
                    log.info("文件缺少$TTM数据");
                    sendErrorMessage(new ErrorLog("文件缺少$TTM数据",(String)map.get("fileName")));
                }

                if (map.containsKey("$OSD_X")||map.containsKey("$OSD_S")) {
                    try {
                        //此处要求值解析X雷达的数据,S雷达数据注释
                        /*if (map.containsKey("$OSD_S")){
                            String OSD = (String) map.get("$OSD_S");
                            String[] rotSplit = OSD.split(",");
                            String heading = rotSplit[1];
                            String headingStatus = rotSplit[2];
                            String vesselCourse = rotSplit[3];
                            String courseReference = rotSplit[4];
                            String vesselSpeed = rotSplit[5];
                            String speedReference = rotSplit[6];
                            String speedUnits = rotSplit[9].split("\\*")[0];

                            navigationInformationPO.setHeading(heading);
                            navigationInformationPO.setHeadingStatus(headingStatus);
                            navigationInformationPO.setVesselCourse(vesselCourse);
                            navigationInformationPO.setCourseReference(courseReference);
                            navigationInformationPO.setVesselSpeed(vesselSpeed);
                            navigationInformationPO.setSpeedReference(speedReference);
                            navigationInformationPO.setSpeedUnits(speedUnits);
                        }*/

                        if (map.containsKey("$OSD_X")){
                            String OSD = (String) map.get("$OSD_X");
                            String[] rotSplit = OSD.split(",");
                            String heading = rotSplit[1];
                            String headingStatus = rotSplit[2];
                            String vesselCourse = rotSplit[3];
                            String courseReference = rotSplit[4];
                            String vesselSpeed = rotSplit[5];
                            String speedReference = rotSplit[6];
                            String speedUnits = rotSplit[9].split("\\*")[0];

                            navigationInformationPO.setHeading(heading);
                            navigationInformationPO.setHeadingStatus(headingStatus);
                            navigationInformationPO.setVesselCourse(vesselCourse);
                            navigationInformationPO.setCourseReference(courseReference);
                            navigationInformationPO.setVesselSpeed(vesselSpeed);
                            navigationInformationPO.setSpeedReference(speedReference);
                            navigationInformationPO.setSpeedUnits(speedUnits);
                        }

                    } catch (Exception e) {
                        log.info("文件内容$OSD内容有误"+e);
                        sendErrorMessage(new ErrorLog("文件内容$OSD内容有误",(String)map.get("fileName")));
                        e.printStackTrace();
                    }
                } else {
                    log.info("文件缺少$OSD数据");
                    sendErrorMessage(new ErrorLog("文件缺少$OSD数据",(String)map.get("fileName")));
                }

                if (map.containsKey("$RSA")) {
                    try {
                        String RSA = (String) map.get("$RSA");
                        String[] rotSplit = RSA.split(",");
                        String starboardRudderSensor = rotSplit[1];
                        String starboardStatus = rotSplit[2];

                        navigationInformationPO.setStarboardRudderSensor(starboardRudderSensor);
                        navigationInformationPO.setStarboardStatus(starboardStatus);
                    } catch (Exception e) {
                        log.info("文件内容$RSA内容有误"+e);
                        sendErrorMessage(new ErrorLog("文件内容$RSA内容有误",(String)map.get("fileName")));
                        e.printStackTrace();
                    }
                } else {
                    log.info("文件缺少$RSA数据");
                    sendErrorMessage(new ErrorLog("文件缺少$RSA数据",(String)map.get("fileName")));
                }

                if (map.containsKey("$VBW")) {
                    try {
                        String VBW = (String) map.get("$VBW");
                        String[] rotSplit = VBW.split(",");
                        String longitudinalWaterSpeed = rotSplit[1];//纵向对水速度
                        String transverseWaterSpeed = rotSplit[2];//横向对水速度
                        String longitudinalGroundSpeed = rotSplit[4];//纵向对地速度
                        String transverseGroundSpeed = rotSplit[5];//横向对地速度
                        String sternTransverseWaterSpeed = rotSplit[7];//船尾测得横向对水速度
                        String sternTransverseGroundSpeed = rotSplit[9];//船尾测得横向对地速度
                        String dataValidState = rotSplit[3];//对水速度状态

                        navigationInformationPO.setLongitudinalWaterSpeed(StrUtil.isEmpty(longitudinalWaterSpeed)? null:Double.valueOf(longitudinalWaterSpeed).toString());
                        navigationInformationPO.setTransverseWaterSpeed(transverseWaterSpeed);
                        navigationInformationPO.setLongitudinalGroundSpeed(StrUtil.isEmpty(longitudinalGroundSpeed)? null:Double.valueOf(longitudinalGroundSpeed).toString());
                        navigationInformationPO.setTransverseGroundSpeed(StrUtil.isEmpty(transverseGroundSpeed)? null:Double.valueOf(transverseGroundSpeed).toString());
                        navigationInformationPO.setSternTransverseWaterSpeed(sternTransverseWaterSpeed);
                        navigationInformationPO.setSternTransverseGroundSpeed(StrUtil.isEmpty(sternTransverseGroundSpeed)? null:Double.valueOf(sternTransverseGroundSpeed).toString());
                        navigationInformationPO.setDataValidState(dataValidState);
                    } catch (Exception e) {
                        log.info("文件内容$VBW内容有误"+e);
                        sendErrorMessage(new ErrorLog("文件内容$VBW内容有误",(String)map.get("fileName")));
                        e.printStackTrace();
                    }
                } else {
                    log.info("文件缺少$VBW数据");
                    sendErrorMessage(new ErrorLog("文件缺少$VBW数据",(String)map.get("fileName")));
                }
            } catch (Exception e) {
                sendErrorMessage(new ErrorLog("readData异常",(String)map.get("fileName")));
                e.printStackTrace();
            }
        } else {
            log.info("日志文件未读取到");
            navigationInformationPO.setImoNumber("invalid");
        }
        return navigationInformationPO;
    }

    public ShipDevicePO readCode(Map<String, Object> map) {
        ShipDevicePO shipDevicePO = new ShipDevicePO();
        if (map != null) {
            try {
                if (map.containsKey("$XDR")) {
                    try {
                        List<Map<String, String>> XDR = (List<Map<String, String>>) map.get("$XDR");
                        for (Map<String, String> xdrMap : XDR) {
                            String key = null;
                            String value = null;
                            for (Map.Entry<String, String> xdrEntry : xdrMap.entrySet()) {
                                key = xdrEntry.getKey();
                                value = xdrEntry.getValue();
                            }
                            if ("gasOutletTemp1".equals(key)) {
                                shipDevicePO.setGasOutletTemp1(value);
                            }
                            if ("revolutionSpeed".equals(key)) {
                                shipDevicePO.setRevolutionSpeed(value);
                            }
                            if ("aft_1".equals(key)) {
                                shipDevicePO.setAft1(value);
                            }
                            if ("alot_1".equals(key)) {
                                shipDevicePO.setAlot1(value);
                            }
                            if ("afp_1".equals(key)) {
                                shipDevicePO.setAfp1(value);
                            }
                            if ("alop_1".equals(key)) {
                                shipDevicePO.setAlop1(value);
                            }
                            if ("startingAirPressure".equals(key)) {
                                shipDevicePO.setStartingAirPressure(value);
                            }
                            if ("controlAirPressure".equals(key)) {
                                shipDevicePO.setControlAirPressure(value);
                            }
                            if ("siet".equals(key)) {
                                shipDevicePO.setSiet(value);
                            }
                            if ("meloit".equals(key)) {
                                shipDevicePO.setMeloit(value);
                            }
                            if ("mejit".equals(key)) {
                                shipDevicePO.setMejit(value);
                            }
                            if ("sloot".equals(key)) {
                                shipDevicePO.setSloot(value);
                            }
                            if ("cloit".equals(key)) {
                                shipDevicePO.setCloit(value);
                            }
                            if ("mejip".equals(key)) {
                                shipDevicePO.setMejip(value);
                            }
                            if ("sloip".equals(key)) {
                                shipDevicePO.setSloip(value);
                            }
                            if ("fip".equals(key)) {
                                shipDevicePO.setFip(value);
                            }
                            if ("meloip".equals(key)) {
                                shipDevicePO.setMeloip(value);
                            }
                            if ("meav".equals(key)) {
                                shipDevicePO.setMeav(value);
                            }
                            if ("stemDraft".equals(key)) {
                                shipDevicePO.setStemDraft(value);
                            }
                            if ("portDraft".equals(key)) {
                                shipDevicePO.setPortDraft(value);
                            }
                            if ("starboardDraft".equals(key)) {
                                shipDevicePO.setStarboardDraft(value);
                            }
                            if ("sternDraught".equals(key)) {
                                shipDevicePO.setSternDraught(value);
                            }
                            if ("soet".equals(key)) {
                                shipDevicePO.setSoet(value);
                            }
                            if ("airReceiverTemp".equals(key)) {
                                shipDevicePO.setAirReceiverTemp(value);
                            }
                            if ("airMainFoldPress".equals(key)) {
                                shipDevicePO.setAirMainFoldPress(value);
                            }
                            if ("fit".equals(key)) {
                                shipDevicePO.setFit(value);
                            }
                            if ("shaftBearingTempFore".equals(key)) {
                                shipDevicePO.setShaftBearingTempFore(value);
                            }
                            if ("shaftBearingTempAft".equals(key)) {
                                shipDevicePO.setShaftBearingTempAft(value);
                            }
                            if ("shaftBearingTempInter".equals(key)) {
                                shipDevicePO.setShaftBearingTempInter(value);
                            }
                            if ("coolSeaWPress".equals(key)) {
                                shipDevicePO.setCoolSeaWPress(value);
                            }
                            if ("thrustBearingTemp".equals(key)) {
                                shipDevicePO.setThrustBearingTemp(value);
                            }
                            if ("oilBunkerTempL".equals(key)) {
                                shipDevicePO.setOilBunkerTempL(value);
                            }
                            if ("oilBunkerTempR".equals(key)) {
                                shipDevicePO.setOilBunkerTempR(value);
                            }
                            if ("oilBunkerLevL".equals(key)) {
                                shipDevicePO.setOilBunkerLevL(value);
                            }
                            if ("oilBunkerLevR".equals(key)) {
                                shipDevicePO.setOilBunkerLevR(value);
                            }
                            if ("dieselFuelTankLev".equals(key)) {
                                shipDevicePO.setDieselFuelTankLev(value);
                            }
                            if ("generatorRunning_1".equals(key)) {
                                shipDevicePO.setGeneratorRunning1(value);
                            }
                            if ("generatorRunning_2".equals(key)) {
                                shipDevicePO.setGeneratorRunning2(value);
                            }
                            if ("generatorRunning_3".equals(key)) {
                                shipDevicePO.setGeneratorRunning3(value);
                            }
                            if ("aft_2".equals(key)) {
                                shipDevicePO.setAft2(value);
                            }
                            if ("alot_2".equals(key)) {
                                shipDevicePO.setAlot2(value);
                            }
                            if ("afp_2".equals(key)) {
                                shipDevicePO.setAfp2(value);
                            }
                            if ("alop_2".equals(key)) {
                                shipDevicePO.setAlop2(value);
                            }
                            if ("aft_3".equals(key)) {
                                shipDevicePO.setAft3(value);
                            }
                            if ("alot_3".equals(key)) {
                                shipDevicePO.setAlot3(value);
                            }
                            if ("afp_3".equals(key)) {
                                shipDevicePO.setAfp3(value);
                            }
                            if ("alop_3".equals(key)) {
                                shipDevicePO.setAlop3(value);
                            }
                            if ("fstartingAirPressure1".equals(key)) {
                                shipDevicePO.setFstartingAirPressure1(value);
                            }
                            if ("fstartingAirPressure2".equals(key)) {
                                shipDevicePO.setFstartingAirPressure2(value);
                            }
                            if ("fstartingAirPressure3".equals(key)) {
                                shipDevicePO.setFstartingAirPressure3(value);
                            }
                            if ("fcontrolAirPressure1".equals(key)) {
                                shipDevicePO.setFcontrolAirPressure1(value);
                            }
                            if ("fcontrolAirPressure2".equals(key)) {
                                shipDevicePO.setFcontrolAirPressure2(value);
                            }
                            if ("fcontrolAirPressure3".equals(key)) {
                                shipDevicePO.setFcontrolAirPressure3(value);
                            }
                            if ("gasOutletTemp2".equals(key)) {
                                shipDevicePO.setGasOutletTemp2(value);
                            }
                            if ("gasOutletTemp3".equals(key)) {
                                shipDevicePO.setGasOutletTemp3(value);
                            }
                            if ("mefc".equals(key)) {
                                shipDevicePO.setMefc(value);
                            }

                            if ("oneGwdit".equals(key)) {
                                shipDevicePO.setOneGwdit(value);
                            }
                            if ("oneGwdot".equals(key)) {
                                shipDevicePO.setOneGwdot(value);
                            }
                            if ("oneDwdit".equals(key)) {
                                shipDevicePO.setOneDwdit(value);
                            }
                            if ("oneGwdip".equals(key)) {
                                shipDevicePO.setOneGwdip(value);
                            }
                            if ("oneDwdip".equals(key)) {
                                shipDevicePO.setOneDwdip(value);
                            }
                            if ("oneoneOpot".equals(key)) {
                                shipDevicePO.setOneoneOpot(value);
                            }
                            if ("onetwoOpot".equals(key)) {
                                shipDevicePO.setOnetwoOpot(value);
                            }
                            if ("onethreeOpot".equals(key)) {
                                shipDevicePO.setOnethreeOpot(value);
                            }
                            if ("onefourOpot".equals(key)) {
                                shipDevicePO.setOnefourOpot(value);
                            }
                            if ("twofourTpot".equals(key)) {
                                shipDevicePO.setTwofourTpot(value);
                            }
                            if ("twothreeTpot".equals(key)) {
                                shipDevicePO.setTwothreeTpot(value);
                            }
                            if ("twotwoTpot".equals(key)) {
                                shipDevicePO.setTwotwoTpot(value);
                            }
                            if ("twooneTpot".equals(key)) {
                                shipDevicePO.setTwooneTpot(value);
                            }
                            if ("twoGwdip".equals(key)) {
                                shipDevicePO.setTwoGwdip(value);
                            }
                            if ("twoDwdip".equals(key)) {
                                shipDevicePO.setTwoDwdip(value);
                            }
                            if ("threefourTpot".equals(key)) {
                                shipDevicePO.setThreefourTpot(value);
                            }
                            if ("threethreeTpot".equals(key)) {
                                shipDevicePO.setThreethreeTpot(value);
                            }
                            if ("threetwoTpot".equals(key)) {
                                shipDevicePO.setThreetwoTpot(value);
                            }
                            if ("threeoneTpot".equals(key)) {
                                shipDevicePO.setThreeoneTpot(value);
                            }
                            if ("threeGwdip".equals(key)) {
                                shipDevicePO.setThreeGwdip(value);
                            }
                            if ("threeDwdip".equals(key)) {
                                shipDevicePO.setThreeDwdip(value);
                            }
                            if ("oneZpt".equals(key)) {
                                shipDevicePO.setOneZpt(value);
                            }
                            if ("twoZpt".equals(key)) {
                                shipDevicePO.setTwoZpt(value);
                            }
                            if ("threeZpt".equals(key)) {
                                shipDevicePO.setThreeZpt(value);
                            }
                            if ("fourZpt".equals(key)) {
                                shipDevicePO.setFourZpt(value);
                            }
                            if ("fiveZpt".equals(key)) {
                                shipDevicePO.setFiveZpt(value);
                            }
                            if ("sixZpt".equals(key)) {
                                shipDevicePO.setSixZpt(value);
                            }
                            if ("oneQot".equals(key)) {
                                shipDevicePO.setOneQot(value);
                            }
                            if ("twoQot".equals(key)) {
                                shipDevicePO.setTwoQot(value);
                            }
                            if ("threeQot".equals(key)) {
                                shipDevicePO.setThreeQot(value);
                            }
                            if ("fourQot".equals(key)) {
                                shipDevicePO.setFourQot(value);
                            }
                            if ("fiveQot".equals(key)) {
                                shipDevicePO.setFiveQot(value);
                            }
                            if ("sixQot".equals(key)) {
                                shipDevicePO.setSixQot(value);
                            }
                            if ("zjlqSt".equals(key)) {
                                shipDevicePO.setZjlqSt(value);
                            }
                            if ("zjlhSt".equals(key)) {
                                shipDevicePO.setZjlhSt(value);
                            }
                            if ("zjlqsJit".equals(key)) {
                                shipDevicePO.setZjlqsJit(value);
                            }
                            if ("zjlqsJot".equals(key)) {
                                shipDevicePO.setZjlqsJot(value);
                            }
                            if ("zjpAip".equals(key)) {
                                shipDevicePO.setZjpAip(value);
                            }
                            if ("zjlqJip".equals(key)) {
                                shipDevicePO.setZjlqJip(value);
                            }
                            if ("oneQhlot".equals(key)) {
                                shipDevicePO.setOneQhlot(value);
                            }
                            if ("twoQhlot".equals(key)) {
                                shipDevicePO.setTwoQhlot(value);
                            }
                            if ("threeQhlot".equals(key)) {
                                shipDevicePO.setThreeQhlot(value);
                            }
                            if ("fourQhlot".equals(key)) {
                                shipDevicePO.setFourQhlot(value);
                            }
                            if ("fiveQhlot".equals(key)) {
                                shipDevicePO.setFiveQhlot(value);
                            }
                            if ("sixQhlot".equals(key)) {
                                shipDevicePO.setSixQhlot(value);
                            }
                            if ("oneRct".equals(key)) {
                                shipDevicePO.setOneRct(value);
                            }
                            if ("oneRrt".equals(key)) {
                                shipDevicePO.setOneRrt(value);
                            }
                            if ("twoRct".equals(key)) {
                                shipDevicePO.setTwoRct(value);
                            }
                            if ("twoRrt".equals(key)) {
                                shipDevicePO.setTwoRrt(value);
                            }
                            if ("oneRcl".equals(key)) {
                                shipDevicePO.setOneRcl(value);
                            }
                            if ("oneRrl".equals(key)) {
                                shipDevicePO.setOneRrl(value);
                            }
                            if ("twoRcl".equals(key)) {
                                shipDevicePO.setTwoRcl(value);
                            }
                            if ("twoRrl".equals(key)) {
                                shipDevicePO.setTwoRrl(value);
                            }

                        }
                        // shipDevicePO.setTrim(getHeelAndTrim(shipDevicePO.getStemDraft(), shipDevicePO.getSternDraught(), "trim") + "°");//纵倾
                        // shipDevicePO.setHeel(getHeelAndTrim(shipDevicePO.getStarboardDraft(), shipDevicePO.getPortDraft(), "heel") + "°");//横倾
                    } catch (Exception e) {
                        log.info("XDR数据内容有误"+e);
                        sendErrorMessage(new ErrorLog("XDR数据内容有误",(String)map.get("fileName")));
                    }
                }else {
                    log.info("文件缺少XDR数据");
                    sendErrorMessage(new ErrorLog("文件缺少XDR数据",(String)map.get("fileName")));
                }
                if (map.containsKey("$FEC")) {
                    try {
                        String pfecStr = (String) map.get("$FEC");
                        String[] split = pfecStr.split(",");
                        // 纵倾
                        shipDevicePO.setTrim(split[3]);
                        // 横倾
                        shipDevicePO.setHeel(split[4].split("\\*")[0]);
                    } catch (Exception e) {
                        log.error("PFEC纵倾、横倾内容有误!" + e.getMessage(), e);
                        sendErrorMessage(new ErrorLog("PFEC数据内容有误", (String) map.get("fileName")));
                    }
                } else {
                    log.info("文件缺少PFEC数据");
                    sendErrorMessage(new ErrorLog("文件缺少PFEC数据", (String) map.get("fileName")));
                }
                if (map.containsKey("01") && map.containsKey("02") && map.containsKey("03") && map.containsKey("04") && map.containsKey("05") && map.containsKey("06")) {
                    try {
                        List<Map<String, String>> modbusList1 = (List<Map<String, String>>) map.get("01");
                        List<Map<String, String>> modbusList2 = (List<Map<String, String>>) map.get("02");
                        List<Map<String, String>> modbusList3 = (List<Map<String, String>>) map.get("03");
                        List<Map<String, String>> modbusList4 = (List<Map<String, String>>) map.get("04");
                        List<Map<String, String>> modbusList5 = (List<Map<String, String>>) map.get("05");
                        List<Map<String, String>> modbusList6 = (List<Map<String, String>>) map.get("06");
                        List<Map<String, String>> modbusList7 = (List<Map<String, String>>) map.get("07");
                        String imonumber = (String) map.get("imonumber");

                        List<ShipDeviceModPO> shipDeviceModPOList = new ArrayList<>();

                        for (int i = 0; i < 6; i++) {
                            ShipDeviceModPO shipDeviceModPO = new ShipDeviceModPO();
                            // if (modbusList1.size() == 6) {
                            if (modbusList1.size() > i) {
                                Map<String, String> modbus1 = modbusList1.get(i);
                                modbus1.forEach((k, v) -> {
                                    if ("RG9H201026157B4492".equals(imonumber) || "MG6Z210420157B4580".equals(imonumber)) {
                                        //神华536 RG9H201026157B4492；SHEN HUA 808 MG6Z210420157B4580
                                        String[] steamS = v.trim().split(" ");
                                        shipDeviceModPO.setGlll(hexToDecimal(getStartNum(1, steamS)));  //锅炉 流量
                                        shipDeviceModPO.setGlmd(hexToDecimal(getStartNum(2, steamS)));  //锅炉 密度
                                        shipDeviceModPO.setGlwd(hexToDecimal(getStartNum(3, steamS)));  //锅炉 温度
                                        shipDeviceModPO.setGlbz1(hexToDecimal(getStartNum(4, steamS)));
                                        shipDeviceModPO.setGlbz2(hexToDecimal(getStartNum(5, steamS)));
                                        shipDeviceModPO.setGlbz3(hexToDecimal(getStartNum(6, steamS)));
                                        shipDeviceModPO.setGlbz4(hexToDecimal(getStartNum(7, steamS)));
                                        shipDeviceModPO.setGlbz5(hexToDecimal(getStartNum(8, steamS)));
                                    } else if ("RG9H210716157B4761".equals(imonumber) || "RG9H210716157B47A3".equals(imonumber) || "RG9H201026157B44A0".equals(imonumber)){
                                        //神华515 RG9H210716157B4761；神华805 RG9H210716157B47A3；神华812 RG9H201026157B44A0
                                        String vv = v.replaceAll(" ", "");
                                        shipDeviceModPO.setZjjkll(hexToFloat(vv.substring(14, 22)));    //主机进口 瞬时流量
                                        shipDeviceModPO.setZjjkljll(hexToFloat(vv.substring(22, 30)));   //主机进口 累计流量
                                        shipDeviceModPO.setZjjkwd(hexToFloat(vv.substring(30, 38)));     //主机进口 温度
                                        shipDeviceModPO.setZjjkmd(hexToFloat(vv.substring(38, 46)));     //主机进口 密度
                                    }
                                });
                            }
                            // if (modbusList2.size() == 6) {
                            if (modbusList2.size() > i) {
                                Map<String, String> modbus2 = modbusList2.get(i);
                                modbus2.forEach((k, v) -> {
                                    if ("RG9H201026157B4492".equals(imonumber)) {
                                        //神华536 RG9H201026157B4492
                                        String[] steamS = v.trim().split(" ");
                                        shipDeviceModPO.setZjjkll(hexToDecimal(getStartNum(1, steamS)));    //主机进口 流量
                                        shipDeviceModPO.setZjjkmd(hexToDecimal(getStartNum(2, steamS)));    //主机进口 密度
                                        shipDeviceModPO.setZjjkwd(hexToDecimal(getStartNum(3, steamS)));    //主机进口 温度
                                        shipDeviceModPO.setZjjkbz1(hexToDecimal(getStartNum(4, steamS)));
                                        shipDeviceModPO.setZjjkbz2(hexToDecimal(getStartNum(5, steamS)));
                                        shipDeviceModPO.setZjjkbz3(hexToDecimal(getStartNum(6, steamS)));
                                        shipDeviceModPO.setZjjkbz4(hexToDecimal(getStartNum(7, steamS)));
                                        shipDeviceModPO.setZjjkbz5(hexToDecimal(getStartNum(8, steamS)));
                                    } else if ("MG6Z210420157B4580".equals(imonumber)){
                                        // SHEN HUA 808 MG6Z210420157B4580 02发电机出口
                                        String[] steamS = v.trim().split(" ");
                                        shipDeviceModPO.setFdjckll(hexToDecimal(getStartNum(1, steamS)));   //发电机出口 流量
                                        shipDeviceModPO.setFdjckmd(hexToDecimal(getStartNum(2, steamS)));   //发电机出口 密度
                                        shipDeviceModPO.setFdjckwd(hexToDecimal(getStartNum(3, steamS)));   //发电机出口 温度
                                        shipDeviceModPO.setFdjckbz1(hexToDecimal(getStartNum(4, steamS)));
                                        shipDeviceModPO.setFdjckbz2(hexToDecimal(getStartNum(5, steamS)));
                                        shipDeviceModPO.setFdjckbz3(hexToDecimal(getStartNum(6, steamS)));
                                        shipDeviceModPO.setFdjckbz4(hexToDecimal(getStartNum(7, steamS)));
                                        shipDeviceModPO.setFdjckbz5(hexToDecimal(getStartNum(8, steamS)));
                                    } else if ("RG9H210716157B4761".equals(imonumber) || "RG9H210716157B47A3".equals(imonumber) || "RG9H201026157B44A0".equals(imonumber)){
                                        //神华515 RG9H210716157B4761；神华805 RG9H210716157B47A3；神华812 RG9H201026157B44A0
                                        String vv = v.replaceAll(" ", "");
                                        shipDeviceModPO.setZjckll(hexToFloat(vv.substring(14, 22)));    //主机出口 瞬时流量
                                        shipDeviceModPO.setZjckljll(hexToFloat(vv.substring(22, 30)));   //主机出口 累计流量
                                        shipDeviceModPO.setZjckwd(hexToFloat(vv.substring(30, 38)));     //主机出口 温度
                                        shipDeviceModPO.setZjckmd(hexToFloat(vv.substring(38, 46)));     //主机出口 密度
                                    }
                                });
                            }
                            // if (modbusList3.size() == 6) {
                            if (modbusList3.size() > i) {
                                Map<String, String> modbus3 = modbusList3.get(i);
                                modbus3.forEach((k, v) -> {
                                    if ("RG9H201026157B4492".equals(imonumber)) {
                                        //神华536 RG9H201026157B4492
                                        String[] steamS = v.trim().split(" ");
                                        shipDeviceModPO.setZjckll(hexToDecimal(getStartNum(1, steamS)));    //主机出口 流量
                                        shipDeviceModPO.setZjckmd(hexToDecimal(getStartNum(2, steamS)));    //主机出口 密度
                                        shipDeviceModPO.setZjckwd(hexToDecimal(getStartNum(3, steamS)));    //主机出口 温度
                                        shipDeviceModPO.setZjckbz1(hexToDecimal(getStartNum(4, steamS)));
                                        shipDeviceModPO.setZjckbz2(hexToDecimal(getStartNum(5, steamS)));
                                        shipDeviceModPO.setZjckbz3(hexToDecimal(getStartNum(6, steamS)));
                                        shipDeviceModPO.setZjckbz4(hexToDecimal(getStartNum(7, steamS)));
                                        shipDeviceModPO.setZjckbz5(hexToDecimal(getStartNum(8, steamS)));
                                    } else if ("MG6Z210420157B4580".equals(imonumber)){
                                        // SHEN HUA 808 MG6Z210420157B4580 03发电机进口
                                        String[] steamS = v.trim().split(" ");
                                        shipDeviceModPO.setFdjjkll(hexToDecimal(getStartNum(1, steamS)));   //发电机进口 流量
                                        shipDeviceModPO.setFdjjkmd(hexToDecimal(getStartNum(2, steamS)));   //发电机进口 密度
                                        shipDeviceModPO.setFdjjkwd(hexToDecimal(getStartNum(3, steamS)));   //发电机进口 温度
                                        shipDeviceModPO.setFdjjkbz1(hexToDecimal(getStartNum(4, steamS)));
                                        shipDeviceModPO.setFdjjkbz2(hexToDecimal(getStartNum(5, steamS)));
                                        shipDeviceModPO.setFdjjkbz3(hexToDecimal(getStartNum(6, steamS)));
                                        shipDeviceModPO.setFdjjkbz4(hexToDecimal(getStartNum(7, steamS)));
                                        shipDeviceModPO.setFdjjkbz5(hexToDecimal(getStartNum(8, steamS)));
                                    } else if ("RG9H210716157B4761".equals(imonumber) || "RG9H210716157B47A3".equals(imonumber) || "RG9H201026157B44A0".equals(imonumber)){
                                        //神华515 RG9H210716157B4761；神华805 RG9H210716157B47A3；神华812 RG9H201026157B44A0
                                        String vv = v.replaceAll(" ", "");
                                        shipDeviceModPO.setFdjjkll(hexToFloat(vv.substring(14, 22)));    //发电机进口 瞬时流量
                                        shipDeviceModPO.setFdjjkljll(hexToFloat(vv.substring(22, 30)));   //发电机进口 累计流量
                                        shipDeviceModPO.setFdjjkwd(hexToFloat(vv.substring(30, 38)));     //发电机进口 温度
                                        shipDeviceModPO.setFdjjkmd(hexToFloat(vv.substring(38, 46)));     //发电机进口 密度
                                    }
                                });
                            }
                            // if (modbusList4.size() == 6) {
                            if (modbusList4.size() > i) {
                                Map<String, String> modbus4 = modbusList4.get(i);
                                modbus4.forEach((k, v) -> {
                                    if ("RG9H201026157B4492".equals(imonumber)) {
                                        //神华536 RG9H201026157B4492
                                        String[] steamS = v.trim().split(" ");
                                        shipDeviceModPO.setFdjjkll(hexToDecimal(getStartNum(1, steamS)));   //辅机进口 流量
                                        shipDeviceModPO.setFdjjkmd(hexToDecimal(getStartNum(2, steamS)));   //辅机进口 密度
                                        shipDeviceModPO.setFdjjkwd(hexToDecimal(getStartNum(3, steamS)));   //辅机进口 温度
                                        shipDeviceModPO.setFdjjkbz1(hexToDecimal(getStartNum(4, steamS)));
                                        shipDeviceModPO.setFdjjkbz2(hexToDecimal(getStartNum(5, steamS)));
                                        shipDeviceModPO.setFdjjkbz3(hexToDecimal(getStartNum(6, steamS)));
                                        shipDeviceModPO.setFdjjkbz4(hexToDecimal(getStartNum(7, steamS)));
                                        shipDeviceModPO.setFdjjkbz5(hexToDecimal(getStartNum(8, steamS)));
                                    } else if ("MG6Z210420157B4580".equals(imonumber)){
                                        // SHEN HUA 808 MG6Z210420157B4580 04主机进口
                                        String[] steamS = v.trim().split(" ");
                                        shipDeviceModPO.setZjjkll(hexToDecimal(getStartNum(1, steamS)));    //主机进口 流量
                                        shipDeviceModPO.setZjjkmd(hexToDecimal(getStartNum(2, steamS)));    //主机进口 密度
                                        shipDeviceModPO.setZjjkwd(hexToDecimal(getStartNum(3, steamS)));    //主机进口 温度
                                        shipDeviceModPO.setZjjkbz1(hexToDecimal(getStartNum(4, steamS)));
                                        shipDeviceModPO.setZjjkbz2(hexToDecimal(getStartNum(5, steamS)));
                                        shipDeviceModPO.setZjjkbz3(hexToDecimal(getStartNum(6, steamS)));
                                        shipDeviceModPO.setZjjkbz4(hexToDecimal(getStartNum(7, steamS)));
                                        shipDeviceModPO.setZjjkbz5(hexToDecimal(getStartNum(8, steamS)));
                                    } else if ("RG9H210716157B4761".equals(imonumber) || "RG9H210716157B47A3".equals(imonumber) || "RG9H201026157B44A0".equals(imonumber)){
                                        //神华515 RG9H210716157B4761；神华805 RG9H210716157B47A3；神华812 RG9H201026157B44A0
                                        String vv = v.replaceAll(" ", "");
                                        shipDeviceModPO.setFdjckll(hexToFloat(vv.substring(14, 22)));    //发电机出口 瞬时流量
                                        shipDeviceModPO.setFdjckljll(hexToFloat(vv.substring(22, 30)));   //发电机出口 累计流量
                                        shipDeviceModPO.setFdjckwd(hexToFloat(vv.substring(30, 38)));     //发电机出口 温度
                                        shipDeviceModPO.setFdjckmd(hexToFloat(vv.substring(38, 46)));     //发电机出口 密度
                                    }
                                });
                            }
                            // if (modbusList5.size() == 6) {
                            if (modbusList5.size() > i) {
                                Map<String, String> modbus5 = modbusList5.get(i);
                                modbus5.forEach((k, v) -> {
                                    if ("RG9H201026157B4492".equals(imonumber)) {
                                        //神华536 RG9H201026157B4492
                                        String[] steamS = v.trim().split(" ");
                                        shipDeviceModPO.setFdjckll(hexToDecimal(getStartNum(1, steamS)));   //辅机出口 流量
                                        shipDeviceModPO.setFdjckmd(hexToDecimal(getStartNum(2, steamS)));   //辅机出口 密度
                                        shipDeviceModPO.setFdjckwd(hexToDecimal(getStartNum(3, steamS)));   //辅机出口 温度
                                        shipDeviceModPO.setFdjckbz1(hexToDecimal(getStartNum(4, steamS)));
                                        shipDeviceModPO.setFdjckbz2(hexToDecimal(getStartNum(5, steamS)));
                                        shipDeviceModPO.setFdjckbz3(hexToDecimal(getStartNum(6, steamS)));
                                        shipDeviceModPO.setFdjckbz4(hexToDecimal(getStartNum(7, steamS)));
                                        shipDeviceModPO.setFdjckbz5(hexToDecimal(getStartNum(8, steamS)));
                                    } else if ("MG6Z210420157B4580".equals(imonumber)){
                                        // SHEN HUA 808 MG6Z210420157B4580 05主机出口
                                        String[] steamS = v.trim().split(" ");
                                        shipDeviceModPO.setZjckll(hexToDecimal(getStartNum(1, steamS)));    //主机出口 流量
                                        shipDeviceModPO.setZjckmd(hexToDecimal(getStartNum(2, steamS)));    //主机出口 密度
                                        shipDeviceModPO.setZjckwd(hexToDecimal(getStartNum(3, steamS)));    //主机出口 温度
                                        shipDeviceModPO.setZjckbz1(hexToDecimal(getStartNum(4, steamS)));
                                        shipDeviceModPO.setZjckbz2(hexToDecimal(getStartNum(5, steamS)));
                                        shipDeviceModPO.setZjckbz3(hexToDecimal(getStartNum(6, steamS)));
                                        shipDeviceModPO.setZjckbz4(hexToDecimal(getStartNum(7, steamS)));
                                        shipDeviceModPO.setZjckbz5(hexToDecimal(getStartNum(8, steamS)));
                                    } else if ("RG9H210716157B4761".equals(imonumber) || "RG9H210716157B47A3".equals(imonumber) || "RG9H201026157B44A0".equals(imonumber)){
                                        //神华515 RG9H210716157B4761；神华805 RG9H210716157B47A3；神华812 RG9H201026157B44A0
                                        String vv = v.replaceAll(" ", "");
                                        shipDeviceModPO.setGlll(hexToFloat(vv.substring(14, 22)));    //锅炉进口 瞬时流量
                                        shipDeviceModPO.setGlljll(hexToFloat(vv.substring(22, 30)));   //锅炉进口 累计流量
                                        shipDeviceModPO.setGlwd(hexToFloat(vv.substring(30, 38)));     //锅炉进口 温度
                                        shipDeviceModPO.setGlmd(hexToFloat(vv.substring(38, 46)));     //锅炉进口 密度
                                    }
                                });
                            }
                            // if (modbusList6.size() == 6) {
                            if (modbusList6.size() > i) {
                                Map<String, String> modbus6 = modbusList6.get(i);
                                modbus6.forEach((k, v) -> {
                                    if ("RG9H210716157B47A3".equals(imonumber) || "RG9H201026157B44A0".equals(imonumber)){
                                        //神华805 RG9H210716157B47A3；神华812 RG9H201026157B44A0 有modbus6
                                        //另外俩船没有
                                        String vv = v.replaceAll(" ", "");
                                        shipDeviceModPO.setGlckll(hexToFloat(vv.substring(14, 22)));    //锅炉出口 瞬时流量
                                        shipDeviceModPO.setGlckljll(hexToFloat(vv.substring(22, 30)));   //锅炉出口 累计流量
                                        shipDeviceModPO.setGlckwd(hexToFloat(vv.substring(30, 38)));     //锅炉出口 温度
                                        shipDeviceModPO.setGlckmd(hexToFloat(vv.substring(38, 46)));     //锅炉出口 密度
                                    }
                                });
                            }
                            // if (modbusList7.size() == 6) {
                            if (modbusList7.size() > i) {
                                Map<String, String> modbus7 = modbusList7.get(i);
                                modbus7.forEach((k, v) -> {
                                    if ("RG9H201026157B44A0".equals(imonumber) || "RG9H201026157B4492".equals(imonumber)) {
                                        // 神华812 RG9H201026157B44A0；神华536 RG9H201026157B4492
                                        String vv = v.replaceAll(" ", "");
                                        shipDeviceModPO.setFdjglxh1(getGeneratorPower(hexToDecimal(vv.substring(6,10))));
                                        shipDeviceModPO.setFdjglxh2(getGeneratorPower(hexToDecimal(vv.substring(10,14))));
                                        shipDeviceModPO.setFdjglxh3(getGeneratorPower(hexToDecimal(vv.substring(14,18))));
                                    }
                                });
                            }
                            shipDeviceModPOList.add(shipDeviceModPO);
                        }
                        shipDevicePO.setShipDeviceModPOList(shipDeviceModPOList);

                    } catch (Exception e) {
                        log.error("MODBUS内容有误" + e);
                        sendErrorMessage(new ErrorLog("MODBUS内容有误", (String) map.get("fileName")));
                    }
                }else {
                    log.info("文件缺少MODBUS数据");
                    sendErrorMessage(new ErrorLog("文件缺少MODBUS数据",(String)map.get("fileName")));
                }
            } catch (Exception e) {
                log.error("设备相关文件内容有误"+e);
                sendErrorMessage(new ErrorLog("设备相关文件内容有误",(String)map.get("fileName")));
                e.printStackTrace();
            }
        } else {
            log.info("日志文件未读取到");
            shipDevicePO.setImoNumber("invalid");
        }
        return shipDevicePO;
    }

    public Map<String,ShipVdmPO> readVDM(Map<String,Object> map) throws ParseException {
        Map<String, ShipVdmPO> shipVdmPOMap = new HashMap<>();

        String dataSyncTime = (String) map.get("dataSyncTime");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Long collectTime = sdf.parse(dataSyncTime).getTime();

        if (map.containsKey("VDM1")) {
            try {
                String vdm1 = (String) map.get("VDM1");
                AISInputStreamReader vdmAis = new AISInputStreamReader(new ByteArrayInputStream(vdm1.getBytes()), msg -> {
                    Integer messageId = msg.getMessageType().getCode();
                    Integer mmsiNumber = msg.getSourceMmsi().getMMSI();
                    Map<String, Object> dataFields = msg.dataFields();

                    ShipVdmPO shipVdmPO = new ShipVdmPO();
                    shipVdmPO.setMessageId(messageId.toString());
                    shipVdmPO.setMmsiNumber(mmsiNumber.toString());
                    shipVdmPO.setUpdateTime(collectTime);

                    //经纬度手动解析
                    String line = msg.getNmeaMessages()[0].getRawMessage();
                    String vdm1Str = line.split(",")[5];
                    String vdm1Acsii = BinaryStringConverteUtil.stringToAscii(vdm1Str);
                    String[] vod1Split = vdm1Acsii.split(",");
                    StringBuilder vdm1bitTOmessage = new StringBuilder();
                    for (String s2 : vod1Split) {
                        String bit6 = BinaryStringConverteUtil.to6Binary(Integer.valueOf(s2));
                        vdm1bitTOmessage.append(bit6);
                    }

                    if (messageId == 1 || messageId == 2 || messageId == 3) {
                        //vdm1/2/3
                        //只取第一个，重复的不要
                        if (!shipVdmPOMap.containsKey(mmsiNumber.toString())) {

                            //航行状态
                            String navigationStatus = convertStatus((String) dataFields.get("navigationStatus"));
                            shipVdmPO.setNavigationalStatus(navigationStatus);

                            //旋转速率
                            Integer rateOfTurn = convertRateOfTurn(bitToinfo(vdm1bitTOmessage.substring(42, 50)));
                            shipVdmPO.setSteeringSpeed(rateOfTurn == -128 ? "invalid" : rateOfTurn + "°/min");

                            //SOG
                            Float speedOverGround = (Float) dataFields.get("speedOverGround");
                            shipVdmPO.setSog(speedOverGround >= 102.3 ? "invalid" : speedOverGround + "knots");

                            //位置准确度
                            Boolean positionAccuracy = (Boolean) dataFields.get("positionAccuracy");
                            shipVdmPO.setPositionAccuracy(positionAccuracy ? "1" : "0");

                            //经度
                            Integer longitudeA = bitToinfo(vdm1bitTOmessage.substring(61, 89));
                            shipVdmPO.setLongitude(convertLongitudeAndLatitude(longitudeA, true, true));

                            //纬度
                            Integer latitudeA = bitToinfo(vdm1bitTOmessage.substring(89, 116));
                            shipVdmPO.setLatitude(convertLongitudeAndLatitude(latitudeA, false, true));

                            //地面航线  COG
                            Float courseOverGround = (Float) dataFields.get("courseOverGround");
                            shipVdmPO.setCog(courseOverGround >= 360 ? "invalid" : courseOverGround + "°");

                            //实际航向
                            Integer trueHeading = (Integer) dataFields.get("trueHeading");
                            shipVdmPO.setTrueHeading(trueHeading == 511 ? "invalid" : trueHeading + "°");

                            //时戳
                            Integer second = (Integer) dataFields.get("second");
                            shipVdmPO.setUtcTimeStamp(second.toString());

                            //特定操作指示符
                            String specialManeuverIndicator = (String) dataFields.get("specialManeuverIndicator");
                            shipVdmPO.setSpecificManoeuvreIndicator(specialManeuverIndicator);

                            //电子定位装置的接收机自主整体检测标志
                            Boolean raimFlag = (Boolean) dataFields.get("raimFlag");
                            shipVdmPO.setRaimFlag(raimFlag ? "1" : "0");

                            if (messageId == 1) {
                                shipVdmPO.setVdm1Time(collectTime);
                            } else if (messageId == 2) {
                                shipVdmPO.setVdm2Time(collectTime);
                            } else if (messageId == 3) {
                                shipVdmPO.setVdm3Time(collectTime);
                            }
                            shipVdmPO.setPositionUpdateTime(collectTime);
                            shipVdmPOMap.put(mmsiNumber.toString(), shipVdmPO);
                        }

                    } else if (messageId == 18) {
                        //vdm18
                        if (!shipVdmPOMap.containsKey(mmsiNumber.toString())) {
                            //SOG
                            Float speedOverGround = (Float) dataFields.get("speedOverGround");
                            shipVdmPO.setSog(speedOverGround >= 102.3 ? "invalid" : speedOverGround + "knots");

                            //位置准确度
                            Boolean positionAccuracy = (Boolean) dataFields.get("positionAccurate");
                            shipVdmPO.setPositionAccuracy(positionAccuracy ? "1" : "0");

                            //经度
                            Integer longitudeA = bitToinfo(vdm1bitTOmessage.substring(57, 85));
                            shipVdmPO.setLongitude(convertLongitudeAndLatitude(longitudeA, true, true));

                            //纬度
                            Integer latitudeA = bitToinfo(vdm1bitTOmessage.substring(85, 112));
                            shipVdmPO.setLatitude(convertLongitudeAndLatitude(latitudeA, false, true));

                            //地面航线  COG
                            Float courseOverGround = (Float) dataFields.get("courseOverGround");
                            shipVdmPO.setCog(courseOverGround >= 360 ? "invalid" : courseOverGround + "°");

                            //实际航向
                            Integer trueHeading = (Integer) dataFields.get("trueHeading");
                            shipVdmPO.setTrueHeading(trueHeading == 511 ? "invalid" : trueHeading + "°");

                            //时戳
                            Integer second = (Integer) dataFields.get("second");
                            shipVdmPO.setUtcTimeStamp(second.toString());

                            //电子定位装置的接收机自主整体检测标志
                            Boolean raimFlag = (Boolean) dataFields.get("raimFlag");
                            shipVdmPO.setRaimFlag(raimFlag ? "1" : "0");

                            shipVdmPO.setVdm18Time(collectTime);
                            shipVdmPO.setPositionUpdateTime(collectTime);
                            shipVdmPOMap.put(mmsiNumber.toString(), shipVdmPO);
                        }
                    } else if (messageId == 24) {
                        if (!shipVdmPOMap.containsKey(mmsiNumber.toString())) {

                            int partNumber = (int) dataFields.get("partNumber");
                            if (partNumber == 0) {
                                //vdm24 part A
                                shipVdmPO.setMessageId("24A");
                                //名称
                                String shipName = (String) dataFields.get("shipName");
                                shipVdmPO.setName(shipName);
                            }else {
                                //vdm24 part B
                                shipVdmPO.setMessageId("24B");
                                //船舶和货物类型
                                String shipType = (String) dataFields.get("shipType");
                                shipVdmPO.setShipType(shipType);

                                //供应商ID
                                //String vendorId = (String) dataFields.get("vendorId");

                                //呼号
                                String callsign = (String) dataFields.get("callsign");
                                shipVdmPO.setCallSign(callsign);

                                //总体尺寸位置参考
                                Integer shipDimensionsA = (Integer) dataFields.get("toBow");
                                Integer shipDimensionsB = (Integer) dataFields.get("toStern");
                                Integer shipDimensionsC = (Integer) dataFields.get("toPort");
                                Integer shipDimensionsD = (Integer) dataFields.get("toStarboard");
                                String shipDimensions = "A=" + shipDimensionsA
                                        + ",B=" + shipDimensionsB
                                        + ",C=" + shipDimensionsC
                                        + ",D=" + shipDimensionsD;
                                shipVdmPO.setShipDimensions(shipDimensions);

                                //电子定位装置类型
                                //String epfdType = (String) dataFields.get("positionFixingDevice");
                                //shipVdmPO.setEpfdType(epfdType);
                            }
                            shipVdmPO.setVdm24Time(collectTime);
                            shipVdmPO.setStaticUpdateTime(collectTime);
                            shipVdmPOMap.put(mmsiNumber.toString(), shipVdmPO);
                        }
                    } else if (messageId == 27) {
                        //vdm27
                        //只取第一个，重复的不要
                        if (!shipVdmPOMap.containsKey(mmsiNumber.toString())) {
                            //位置准确度
                            Boolean positionAccuracy = (Boolean) dataFields.get("positionAccuracy");
                            shipVdmPO.setPositionAccuracy(positionAccuracy ? "1" : "0");

                            //电子定位装置的接收机自主整体检测标志
                            Boolean raimFlag = (Boolean) dataFields.get("raim");
                            shipVdmPO.setRaimFlag(raimFlag ? "1" : "0");

                            //航行状态
                            String navigationStatus = convertStatus((String) dataFields.get("navigationStatus"));
                            shipVdmPO.setNavigationalStatus(navigationStatus);

                            //经度
                            Integer longitudeA = bitToinfo(vdm1bitTOmessage.substring(44, 62));
                            shipVdmPO.setLongitude(convertLongitudeAndLatitude(longitudeA, true, false));

                            //维度
                            Integer latitudeA = bitToinfo(vdm1bitTOmessage.substring(62, 79));
                            shipVdmPO.setLatitude(convertLongitudeAndLatitude(latitudeA, false, false));

                            //地面航速
                            Float speedOverGround = (Float) dataFields.get("speedOverGround");
                            shipVdmPO.setSog(speedOverGround >= 63 ? "invalid" : speedOverGround + "knots");

                            //地面航线
                            Float courseOverGround = (Float) dataFields.get("courseOverGround");
                            shipVdmPO.setCog(courseOverGround >= 511 ? "invalid" : courseOverGround + "°");

                            //位置等待时间
                            Integer positionLatency = (Integer) dataFields.get("positionLatency");
                            shipVdmPO.setStatusOfCurrentGnssPosition(positionLatency.toString());

                            shipVdmPO.setVdm27Time(collectTime);
                            shipVdmPO.setPositionUpdateTime(collectTime);
                            shipVdmPOMap.put(mmsiNumber.toString(), shipVdmPO);
                        }
                    }
                });
                vdmAis.run();
            } catch (IOException e) {
                log.info("文件VDM1数据有误：", e);
                sendErrorMessage(new ErrorLog("文件VDM1数据有误",(String)map.get("fileName")));
            }
        } else {
            log.info("文件缺少VDM1数据");
            sendErrorMessage(new ErrorLog("文件缺少VDM1数据",(String)map.get("fileName")));

        }

        if (map.containsKey("VDM5")) {
            try {
                String vdm5 = (String) map.get("VDM5");
                AISInputStreamReader vdmAis = new AISInputStreamReader(new ByteArrayInputStream(vdm5.getBytes()), msg -> {
                    Integer messageId = msg.getMessageType().getCode();
                    Integer mmsiNumber = msg.getSourceMmsi().getMMSI();
                    Map<String, Object> dataFields = msg.dataFields();

                    ShipVdmPO shipVdmPO = new ShipVdmPO();
                    shipVdmPO.setMessageId(messageId.toString());
                    shipVdmPO.setMmsiNumber(mmsiNumber.toString());
                    shipVdmPO.setUpdateTime(collectTime);

                    if (messageId == 5) {
                        //vdm5
                        if (!shipVdmPOMap.containsKey("5-" + mmsiNumber.toString())) {
                            //imo编号
                            Integer imo = (Integer) dataFields.get("imo.IMO");
                            shipVdmPO.setImoNumber(imo.toString());

                            //呼号
                            String callsign = (String) dataFields.get("callsign");
                            shipVdmPO.setCallSign(callsign);

                            //名称
                            String shipName = (String) dataFields.get("shipName");
                            shipVdmPO.setName(shipName);

                            //船舶和货物类型
                            String shipType = (String) dataFields.get("shipType");
                            shipVdmPO.setShipType(shipType);

                            //总体尺寸位置参考
                            Integer shipDimensionsA = (Integer) dataFields.get("toBow");
                            Integer shipDimensionsB = (Integer) dataFields.get("toStern");
                            Integer shipDimensionsC = (Integer) dataFields.get("toPort");
                            Integer shipDimensionsD = (Integer) dataFields.get("toStarboard");
                            String shipDimensions = "A=" + shipDimensionsA
                                    + ",B=" + shipDimensionsB
                                    + ",C=" + shipDimensionsC
                                    + ",D=" + shipDimensionsD;
                            shipVdmPO.setShipDimensions(shipDimensions);

                            //电子定位装置类型
                            String epfdType = (String) dataFields.get("positionFixingDevice");
                            shipVdmPO.setEpfdType(epfdType);

                            //估计到达时间
                            String etaDateTime = convertETA((String) dataFields.get("eta"));
                            shipVdmPO.setEta(DateUtils.str2Long(etaDateTime).toString());

                            //目前最大静态吃水
                            Float draught = (Float) dataFields.get("draught");
                            shipVdmPO.setMaxStaticDraft(draught + "m");

                            //目的地
                            String destination = (String) dataFields.get("destination");
                            shipVdmPO.setDestination(destination);

                            //数据终端就绪
                            Boolean dte = (Boolean) dataFields.get("dataTerminalReady");
                            shipVdmPO.setDte(dte ? "1" : "0");

                            shipVdmPO.setVdm5Time(collectTime);
                            shipVdmPO.setStaticUpdateTime(collectTime);
                            shipVdmPOMap.put("5-" + mmsiNumber.toString(), shipVdmPO);
                        }
                    }else if (messageId == 19){
                        //经纬度手动解析
                        String line = msg.getNmeaMessages()[0].getRawMessage();
                        String str = line.split(",")[5];
                        String ascii = BinaryStringConverteUtil.stringToAscii(str);
                        String[] vod1Split = ascii.split(",");
                        StringBuilder bitMessage = new StringBuilder();
                        for (String s2 : vod1Split) {
                            String bit6 = BinaryStringConverteUtil.to6Binary(Integer.valueOf(s2));
                            bitMessage.append(bit6);
                        }

                        //vdm19
                        if (!shipVdmPOMap.containsKey("19-" + mmsiNumber.toString())) {

                            //SOG
                            Integer speedOverGround = bitToinfo(bitMessage.substring(46, 56));
                            shipVdmPO.setSog(speedOverGround == 1023 ? "invalid" : BigDecimal.valueOf(speedOverGround * 0.1).setScale(1, RoundingMode.HALF_UP).toString() + "knots");

                            //位置准确度
                            Boolean positionAccuracy = (Boolean) dataFields.get("positionAccurate");
                            shipVdmPO.setPositionAccuracy(positionAccuracy ? "1" : "0");

                            //经度
                            Integer longitudeA = bitToinfo(bitMessage.substring(57, 85));
                            shipVdmPO.setLongitude(convertLongitudeAndLatitude(longitudeA, true, true));

                            //纬度
                            Integer latitudeA = bitToinfo(bitMessage.substring(85, 112));
                            shipVdmPO.setLatitude(convertLongitudeAndLatitude(latitudeA, false, true));

                            //地面航线  COG
                            Float courseOverGround = (Float) dataFields.get("courseOverGround");
                            shipVdmPO.setCog(courseOverGround >= 360 ? "invalid" : courseOverGround + "°");

                            //实际航向
                            Integer trueHeading = (Integer) dataFields.get("trueHeading");
                            shipVdmPO.setTrueHeading(trueHeading == 511 ? "invalid" : trueHeading + "°");

                            //时戳
                            Integer second = (Integer) dataFields.get("second");
                            shipVdmPO.setUtcTimeStamp(second.toString());

                            //名称
                            String shipName = (String) dataFields.get("shipName");
                            shipVdmPO.setName(shipName);

                            //船舶和货物类型
                            String shipType = (String) dataFields.get("shipType");
                            shipVdmPO.setShipType(shipType);

                            //总体尺寸位置参考
                            Integer shipDimensionsA = (Integer) dataFields.get("toBow");
                            Integer shipDimensionsB = (Integer) dataFields.get("toStern");
                            Integer shipDimensionsC = (Integer) dataFields.get("toPort");
                            Integer shipDimensionsD = (Integer) dataFields.get("toStarboard");
                            String shipDimensions = "A=" + shipDimensionsA
                                    + ",B=" + shipDimensionsB
                                    + ",C=" + shipDimensionsC
                                    + ",D=" + shipDimensionsD;
                            shipVdmPO.setShipDimensions(shipDimensions);

                            //电子定位装置类型
                            String epfdType = (String) dataFields.get("positionFixingDevice");
                            shipVdmPO.setEpfdType(epfdType);

                            //电子定位装置的接收机自主整体检测标志
                            Boolean raimFlag = (Boolean) dataFields.get("raimFlag");
                            shipVdmPO.setRaimFlag(raimFlag ? "1" : "0");

                            //数据终端就绪
                            Boolean dte = (Boolean) dataFields.get("dataTerminalReady");
                            shipVdmPO.setDte(dte ? "1" : "0");

                            shipVdmPO.setVdm19Time(collectTime);
                            shipVdmPO.setPositionUpdateTime(collectTime);
                            shipVdmPO.setStaticUpdateTime(collectTime);
                            shipVdmPOMap.put("19-" + mmsiNumber.toString(), shipVdmPO);
                        }
                    }
                });
                vdmAis.run();
            } catch (Exception e) {
                log.info("文件VDM5数据有误：", e);
                sendErrorMessage(new ErrorLog("文件VDM5数据有误",(String)map.get("fileName")));
            }
        } else {
            log.info("文件缺少VDM5数据");
            sendErrorMessage(new ErrorLog("文件缺少VDM5数据",(String)map.get("fileName")));
        }
        return shipVdmPOMap;
    }

    //模拟量获取数据
    public static String getStartNum(Integer num, String[] codes) {
        int start = 3 + (num - 1) * 2;
        return codes[start] + codes[start + 1];
    }

    //将六位二进制转化为十进制数据，二进制转为十进制也可使用该方法
    public Integer bitToinfo(String value) {
        return Integer.parseInt(value, 2);
    }

    //经纬度转换
    public String convertLongitudeAndLatitude(Integer in, boolean isLon, boolean isVdo) {
        int divisor;
        //经纬度可能为负值
        if (isVdo) {
            //vdo1和vdm1
            if (isLon){
                //经度
                if (in > 2 << 26) {
                    in = in - (2 << 27) + 1;
                }
            }else {
                //纬度
                if (in > 2 << 25) {
                    in = in - (2 << 26) + 1;
                }
            }
            divisor = 600000;
        }else {
            //vdm27
            if (isLon){
                //经度
                if (in > 2 << 16) {
                    in = in - (2 << 17) + 1;
                }
            }else {
                //纬度
                if (in > 2 << 15) {
                    in = in - (2 << 16) + 1;
                }
            }
            divisor = 600;
        }
        BigDecimal bigDecimal = new BigDecimal(String.valueOf(in));
        BigDecimal after = bigDecimal.divide(BigDecimal.valueOf(divisor), 7, RoundingMode.HALF_DOWN);
        return String.valueOf(after);
    }

    //旋转速率转换
    public Integer convertRateOfTurn(Integer a){
        if (a>= 2<<6){
            a = a - (2<<7);
        }
        return a;
    }

    //航行状态转换
    public String convertStatus(String navigationStatus){
        String status = null;
        if ("UnderwayUsingEngine".equals(navigationStatus)) {
            status = "发动机使用中";
        } else if ("AtAnchor".equals(navigationStatus)) {
            status = "锚泊";
        } else if ("NotUnderCommand".equals(navigationStatus)) {
            status = "未操纵";
        } else if ("RestrictedManoeuverability".equals(navigationStatus)) {
            status = "有限适航性";
        } else if ("ConstrainedByHerDraught".equals(navigationStatus)) {
            status = "受船舶吃水限制";
        } else if ("Moored".equals(navigationStatus)) {
            status = "系泊";
        } else if ("Aground".equals(navigationStatus)) {
            status = "搁浅";
        } else if ("EngagedInFising".equals(navigationStatus)) {
            status = "从事捕捞";
        } else if ("UnderwaySailing".equals(navigationStatus)) {
            status = "航行中";
        } else if ("ReservedForFutureUse9".equals(navigationStatus)) {
            //status = "9";//修正导航状态
        } else if ("ReservedForFutureUse10".equals(navigationStatus)) {
            //status = "10";//修正导航状态
        } else if ("PowerDrivenVesselTowingAstern".equals(navigationStatus)) {
            status = "机动船尾推作业";
        } else if ("PowerDrivenVesselPushingAheadOrTowingAlongside".equals(navigationStatus)) {
            status = "机动船顶推或侧推作业";
        } else if ("ReservedForFutureUse13".equals(navigationStatus)) {
            //status = "13";//留作将来用
        } else if ("SartMobOrEpirb".equals(navigationStatus)) {
            //status = "SartMobOrEpirb";
        } else if ("Undefined".equals(navigationStatus)) {
            status = "未规定";
        }
        return status;
    }

    //ETA 预计到达时间  入参格式 dd-MM HH:mm  出参格式 yyyy-MM-dd HH:mm:ss
    public String convertETA(String eta){

        String[] date = eta.split(" ");
        String[] dayMonth = date[0].split("-");
        String day = dayMonth[0];
        String month = dayMonth[1];
        String[] hourMinute = date[1].split(":");
        String hour = hourMinute[0];
        String minute = hourMinute[1];

        if ("0".equals(day) || "0".equals(month) || "24".equals(hour) || "60".equals(minute)){
            return "1970-01-01 00:00:00";
        }

        int year = Calendar.getInstance().get(Calendar.YEAR);

        return year + "-" + month + "-" + day + " " + hour + ":" + minute + ":00";
    }
    /**
     * 此规则只支持536船
     * 对于需要使用excel表格来对应关系的数据，这里生成一个key v键值对
     * key:数据名称
     * v:数据对应的具体名称
     * 如：2010 : 进口油量
     *
     * @return
     */
    public static String numberForType(String key) {
        Map<String, String> excelMap = new HashMap<>();
        excelMap.put("FUEL INDEX", "mefc");//主机燃油刻度
        excelMap.put("ME RPM", "revolutionSpeed");//主机转速
        excelMap.put("0315", "aft_1");//1号辅机燃油温度
        excelMap.put("0318", "alot_1");//1号辅机滑油温度
        excelMap.put("0314", "afp_1");//1号辅机燃油压力
        excelMap.put("0317", "alop_1");//1号辅机滑油压力
        excelMap.put("0265", "startingAirPressure");//主机起动空气压力
        excelMap.put("0267", "controlAirPressure");//主机控制空气压力
        excelMap.put("0321", "fstartingAirPressure1");//1号辅机起动空气压力
        excelMap.put("0322", "fcontrolAirPressure1");//1号辅机控制空气压力
        //之前是0423和0422错误,改为0422和0421
        excelMap.put("0421", "fstartingAirPressure2");//2号辅机起动空气压力
        excelMap.put("0422", "fcontrolAirPressure2");//2号辅机控制空气压力
        excelMap.put("0521", "fstartingAirPressure3");//3号辅机起动空气压力
        excelMap.put("0522", "fcontrolAirPressure3");//3号辅机控制空气压力
        excelMap.put("0332", "gasOutletTemp1");//1号辅机排气出口温度
        excelMap.put("0432", "gasOutletTemp2");//2号辅机排气出口温度
        excelMap.put("0532", "gasOutletTemp3");//3号辅机排气出口温度
        excelMap.put("0257", "siet");//增压器进口排气温度
        excelMap.put("0258", "soet");//增压器出口排气温度
        excelMap.put("0208", "meloit");//主机滑油进口温度
        excelMap.put("0243", "airReceiverTemp");//主机扫气储气罐温度
        excelMap.put("0229", "mejit");//主机缸套冷却水进口温度
        excelMap.put("0210", "sloot");//增压器滑油出口温度
        excelMap.put("0281", "cloit");//汽缸滑油进口温度
        excelMap.put("0228", "mejip");//主机缸套冷却水进口压力
//        excelMap.put("0265","起动空气压力");//起动空气压力
        excelMap.put("0209", "sloip");//增压器滑油进口压力
        excelMap.put("0261", "airMainFoldPress");//主机扫气集管压力
//        excelMap.put("0267","控制空气压力");//控制空气压力
        excelMap.put("0201", "fip");//主机燃油进口压力
        excelMap.put("0203", "fit");//主机燃油进口温度
        excelMap.put("0296", "shaftBearingTempFore");//艉部前轴承温度
        excelMap.put("0295", "shaftBearingTempAft");//艉部后轴承温度
        excelMap.put("0294", "shaftBearingTempInter");//中间轴承温度
        excelMap.put("0241", "coolSeaWPress");//冷却海水压力
        excelMap.put("0205", "thrustBearingTemp");//推力轴承温度
        excelMap.put("0207", "meloip");//主机滑油进口压力
        excelMap.put("0276", "meav");//主机轴向振动
        excelMap.put("1005", "oilBunkerTempL");//左燃油舱温度
        excelMap.put("1006", "oilBunkerTempR");//右燃油舱温度
        excelMap.put("1018", "oilBunkerLevL");//左燃油舱液位
        excelMap.put("1019", "oilBunkerLevR");//右燃油舱液位
        excelMap.put("1022", "dieselFuelTankLev");//柴油舱液位
        excelMap.put("1133", "stemDraft");//艏部吃水
        excelMap.put("1135", "portDraft");//左舷吃水
        excelMap.put("1136", "starboardDraft");//右舷吃水
        excelMap.put("1134", "sternDraught");//艉部吃水
        excelMap.put("0616", "generatorRunning_1");//1号辅机运行
        excelMap.put("0617", "generatorRunning_2");//2号辅机运行
        excelMap.put("0618", "generatorRunning_3");//3号辅机运行
        excelMap.put("0415", "aft_2");//2号辅机燃油温度
        excelMap.put("0418", "alot_2");//2号辅机滑油温度
        excelMap.put("0414", "afp_2");//2号辅机燃油压力
        excelMap.put("0417", "alop_2");//2号辅机滑油压力
        excelMap.put("0515", "aft_3");//3号辅机燃油温度
        excelMap.put("0518", "alot_3");//3号辅机滑油温度
        excelMap.put("0514", "afp_3");//3号辅机燃油压力
        excelMap.put("0517", "alop_3");//3号辅机滑油压力

        //20210809新增XDR属性解析
        excelMap.put("0324", "oneGwdit");//1号辅机高温淡水进口温度
        excelMap.put("0325", "oneGwdot");//1号辅机高温淡水出口温度
        excelMap.put("0327", "oneDwdit");//1号辅机低温淡水进口温度
        excelMap.put("0323", "oneGwdip");//1号辅机高温淡水进口压力
        excelMap.put("0326", "oneDwdip");//1号辅机低温淡水进口压力
        excelMap.put("0328", "oneoneOpot");//1号辅机1号气缸排气温度
        excelMap.put("0329", "onetwoOpot");//1号辅机2号气缸排气温度
        excelMap.put("0330", "onethreeOpot");//1号辅机3号气缸排气温度
        excelMap.put("0331", "onefourOpot");//1号辅机4号气缸排气温度
        excelMap.put("0431", "twofourTpot");//2号辅机4号气缸排气温度
        excelMap.put("0430", "twothreeTpot");//2号辅机3号气缸排气温度
        excelMap.put("0429", "twotwoTpot");//2号辅机2号气缸排气温度
        excelMap.put("0428", "twooneTpot");//2号辅机1号气缸排气温度
        excelMap.put("0423", "twoGwdip");//2号辅机高温淡水进口压力
        excelMap.put("0426", "twoDwdip");//2号辅机低温淡水进口压力
        excelMap.put("0531", "threefourTpot");//3号辅机4号气缸排气温度
        excelMap.put("0530", "threethreeTpot");//3号辅机3号气缸排气温度
        excelMap.put("0529", "threetwoTpot");//3号辅机2号气缸排气温度
        excelMap.put("0528", "threeoneTpot");//3号辅机1号气缸排气温度
        excelMap.put("0523", "threeGwdip");//3号辅机高温淡水进口压力
        excelMap.put("0526", "threeDwdip");//3号辅机低温淡水进口压力
        excelMap.put("0251", "oneZpt");//主机1号气缸排气温度
        excelMap.put("0252", "twoZpt");//主机2号气缸排气温度
        excelMap.put("0253", "threeZpt");//主机3号气缸排气温度
        excelMap.put("0254", "fourZpt");//主机4号气缸排气温度
        excelMap.put("0255", "fiveZpt");//主机5号气缸排气温度
        excelMap.put("0256", "sixZpt");//主机6号气缸排气温度
        excelMap.put("0232", "oneQot");//主机1号气缸缸套冷却水出口温度
        excelMap.put("0233", "twoQot");//主机2号气缸缸套冷却水出口温度
        excelMap.put("0234", "threeQot");//主机3号气缸缸套冷却水出口温度
        excelMap.put("0235", "fourQot");//主机4号气缸缸套冷却水出口温度
        excelMap.put("0236", "fiveQot");//主机5号气缸缸套冷却水出口温度
        excelMap.put("0237", "sixQot");//主机6号气缸缸套冷却水出口温度
        excelMap.put("0260", "zjlqSt");//主机空冷器前扫气温度
        excelMap.put("0262", "zjlhSt");//主机空冷器后扫气温度
        excelMap.put("0239", "zjlqsJit");//主机空冷器冷却水进口温度
        excelMap.put("0240", "zjlqsJot");//主机空冷器冷却水出口温度
        excelMap.put("0271", "zjpAip");//主机排气阀弹簧空气压力
        excelMap.put("0238", "zjlqJip");//主机空冷器冷却水进口压力
        excelMap.put("0215", "oneQhlot");//主机1号气缸活塞冷却油出口温度
        excelMap.put("0216", "twoQhlot");//主机2号气缸活塞冷却油出口温度
        excelMap.put("0217", "threeQhlot");//主机3号气缸活塞冷却油出口温度
        excelMap.put("0218", "fourQhlot");//主机4号气缸活塞冷却油出口温度
        excelMap.put("0219", "fiveQhlot");//主机5号气缸活塞冷却油出口温度
        excelMap.put("0220", "sixQhlot");//主机6号气缸活塞冷却油出口温度
        excelMap.put("1003", "oneRct");//1号燃油沉淀舱温度
        excelMap.put("1001", "oneRrt");//1号燃油日用舱温度
        excelMap.put("1004", "twoRct");//2号燃油沉淀舱温度
        excelMap.put("1002", "twoRrt");//2号燃油日用舱温度
        excelMap.put("1016", "oneRcl");//1号燃油沉淀舱液位
        excelMap.put("1014", "oneRrl");//1号燃油日用舱液位
        excelMap.put("1017", "twoRcl");//2号燃油沉淀舱液位
        excelMap.put("1015", "twoRrl");//2号燃油日用舱液位

        String value = excelMap.get(key);
        if (value == null) {
            return key;
        } else {
            return value;
        }
    }

    /**
     * 此规则只支持515船
     * 对于需要使用excel表格来对应关系的数据，这里生成一个key v键值对
     * key:数据名称
     * v:数据对应的具体名称
     * 如：2010 : 进口油量
     *
     * @return
     */
    public static String numberForTypeTo515(String key) {
        Map<String, String> excelMap = new HashMap<>();
        excelMap.put("FUEL INDEX", "mefc");//主机燃油刻度
        excelMap.put("ME RPM", "revolutionSpeed");//主机转速
        //515船无此字段
        //excelMap.put("0315", "aft_1");//1号辅机燃油温度
        excelMap.put("0318", "alot_1");//1号辅机滑油温度
        //标识符相同、英文描述不同，是否可以表示（1号辅机燃油压力）
        //excelMap.put("0314", "afp_1");//1号辅机燃油压力
        excelMap.put("0317", "alop_1");//1号辅机滑油压力
        excelMap.put("0265", "startingAirPressure");//主机起动空气压力
        excelMap.put("0267", "controlAirPressure");//主机控制空气压力
        excelMap.put("0321", "fstartingAirPressure1");//1号辅机起动空气压力
        excelMap.put("0322", "fcontrolAirPressure1");//1号辅机控制空气压力
        //之前是0423和0422错误,改为0422和0421
        excelMap.put("0421", "fstartingAirPressure2");//2号辅机起动空气压力
        excelMap.put("0422", "fcontrolAirPressure2");//2号辅机控制空气压力
        excelMap.put("0521", "fstartingAirPressure3");//3号辅机起动空气压力
        excelMap.put("0522", "fcontrolAirPressure3");//3号辅机控制空气压力
        excelMap.put("0332", "gasOutletTemp1");//1号辅机排气出口温度
        excelMap.put("0432", "gasOutletTemp2");//2号辅机排气出口温度
        //515船无此标识
        //excelMap.put("0532", "gasOutletTemp3");//3号辅机排气出口温度
        excelMap.put("0257", "siet");//增压器进口排气温度
        excelMap.put("0258", "soet");//增压器出口排气温度
        excelMap.put("0208", "meloit");//主机滑油进口温度
        excelMap.put("0243", "airReceiverTemp");//主机扫气储气罐温度
        excelMap.put("0229", "mejit");//主机缸套冷却水进口温度
        excelMap.put("0210", "sloot");//增压器滑油出口温度
        excelMap.put("0281", "cloit");//汽缸滑油进口温度
        excelMap.put("0228", "mejip");//主机缸套冷却水进口压力
//        excelMap.put("0265","起动空气压力");//起动空气压力
        excelMap.put("0209", "sloip");//增压器滑油进口压力
        excelMap.put("0261", "airMainFoldPress");//主机扫气集管压力
//        excelMap.put("0267","控制空气压力");//控制空气压力
        excelMap.put("0201", "fip");//主机燃油进口压力
        excelMap.put("0203", "fit");//主机燃油进口温度
        excelMap.put("0296", "shaftBearingTempFore");//艉部前轴承温度
        excelMap.put("0295", "shaftBearingTempAft");//艉部后轴承温度
        excelMap.put("0294", "shaftBearingTempInter");//中间轴承温度
        excelMap.put("0241", "coolSeaWPress");//冷却海水压力
        excelMap.put("0205", "thrustBearingTemp");//推力轴承温度
        excelMap.put("0207", "meloip");//主机滑油进口压力
        excelMap.put("0276", "meav");//主机轴向振动
        excelMap.put("1005", "oilBunkerTempL");//左燃油舱温度
        excelMap.put("1006", "oilBunkerTempR");//右燃油舱温度
        excelMap.put("1018", "oilBunkerLevL");//左燃油舱液位
        excelMap.put("1019", "oilBunkerLevR");//右燃油舱液位
        excelMap.put("1022", "dieselFuelTankLev");//柴油舱液位
        excelMap.put("1133", "stemDraft");//艏部吃水
        excelMap.put("1135", "portDraft");//左舷吃水
        excelMap.put("1136", "starboardDraft");//右舷吃水
        excelMap.put("1134", "sternDraught");//艉部吃水
        excelMap.put("0658", "generatorRunning_1");//1号辅机运行
        excelMap.put("0659", "generatorRunning_2");//2号辅机运行
        excelMap.put("0660", "generatorRunning_3");//3号辅机运行
        excelMap.put("0415", "aft_2");//2号辅机燃油温度
        excelMap.put("0418", "alot_2");//2号辅机滑油温度
        //excelMap.put("0414", "afp_2");//2号辅机燃油压力
        excelMap.put("0417", "alop_2");//2号辅机滑油压力
        excelMap.put("0515", "aft_3");//3号辅机燃油温度
        excelMap.put("0518", "alot_3");//3号辅机滑油温度
        excelMap.put("0514", "afp_3");//3号辅机燃油压力
        excelMap.put("0517", "alop_3");//3号辅机滑油压力

        //20210809新增XDR属性解析

        //excelMap.put("0324", "oneGwdit");//1号辅机高温淡水进口温度//515船无此标识
        excelMap.put("0325", "oneGwdot");//1号辅机高温淡水出口温度
        //excelMap.put("0327", "oneDwdit");//1号辅机低温淡水进口温度//515船无此标识
        excelMap.put("0323", "oneGwdip");//1号辅机高温淡水进口压力
        //excelMap.put("0326", "oneDwdip");//1号辅机低温淡水进口压力
        excelMap.put("0328", "oneoneOpot");//1号辅机1号气缸排气温度
        //excelMap.put("0328", "onetwoOpot");//1号辅机2号气缸排气温度
        //excelMap.put("0328", "onethreeOpot");//1号辅机3号气缸排气温度
        excelMap.put("0329", "onefourOpot");//1号辅机4号气缸排气温度
        //excelMap.put("0329", "onefiveOpot");//1号辅机5号气缸排气温度
        //excelMap.put("0329", "onesexOpot");//1号辅机6号气缸排气温度

        excelMap.put("0429", "twofourTpot");//2号辅机4号气缸排气温度
        //excelMap.put("0430", "twothreeTpot");//2号辅机3号气缸排气温度
        //excelMap.put("0429", "twotwoTpot");//2号辅机2号气缸排气温度
        excelMap.put("0428", "twooneTpot");//2号辅机1号气缸排气温度
        excelMap.put("0423", "twoGwdip");//2号辅机高温淡水进口压力
        //515船无此标识
        //excelMap.put("0426", "twoDwdip");//2号辅机低温淡水进口压力
        excelMap.put("0531", "threefourTpot");//3号辅机4号气缸排气温度
        excelMap.put("0530", "threethreeTpot");//3号辅机3号气缸排气温度
        excelMap.put("0529", "threetwoTpot");//3号辅机2号气缸排气温度
        excelMap.put("0528", "threeoneTpot");//3号辅机1号气缸排气温度
        excelMap.put("0523", "threeGwdip");//3号辅机高温淡水进口压力
        //515船无此标识
        //excelMap.put("0526", "threeDwdip");//3号辅机低温淡水进口压力
        excelMap.put("0251", "oneZpt");//主机1号气缸排气温度
        excelMap.put("0252", "twoZpt");//主机2号气缸排气温度
        excelMap.put("0253", "threeZpt");//主机3号气缸排气温度
        excelMap.put("0254", "fourZpt");//主机4号气缸排气温度
        excelMap.put("0255", "fiveZpt");//主机5号气缸排气温度
        excelMap.put("0256", "sixZpt");//主机6号气缸排气温度
        excelMap.put("0232", "oneQot");//主机1号气缸缸套冷却水出口温度
        excelMap.put("0233", "twoQot");//主机2号气缸缸套冷却水出口温度
        excelMap.put("0234", "threeQot");//主机3号气缸缸套冷却水出口温度
        excelMap.put("0235", "fourQot");//主机4号气缸缸套冷却水出口温度
        excelMap.put("0236", "fiveQot");//主机5号气缸缸套冷却水出口温度
        excelMap.put("0237", "sixQot");//主机6号气缸缸套冷却水出口温度
        excelMap.put("0260", "zjlqSt");//主机空冷器前扫气温度
        excelMap.put("0262", "zjlhSt");//主机空冷器后扫气温度
        excelMap.put("0239", "zjlqsJit");//主机空冷器冷却水进口温度
        excelMap.put("0240", "zjlqsJot");//主机空冷器冷却水出口温度
        excelMap.put("0271", "zjpAip");//主机排气阀弹簧空气压力
        excelMap.put("0238", "zjlqJip");//主机空冷器冷却水进口压力
        excelMap.put("0215", "oneQhlot");//主机1号气缸活塞冷却油出口温度
        excelMap.put("0216", "twoQhlot");//主机2号气缸活塞冷却油出口温度
        excelMap.put("0217", "threeQhlot");//主机3号气缸活塞冷却油出口温度
        excelMap.put("0218", "fourQhlot");//主机4号气缸活塞冷却油出口温度
        excelMap.put("0219", "fiveQhlot");//主机5号气缸活塞冷却油出口温度
        excelMap.put("0220", "sixQhlot");//主机6号气缸活塞冷却油出口温度
        excelMap.put("1003", "oneRct");//1号燃油沉淀舱温度
        excelMap.put("1001", "oneRrt");//1号燃油日用舱温度
        excelMap.put("1004", "twoRct");//2号燃油沉淀舱温度
        excelMap.put("1002", "twoRrt");//2号燃油日用舱温度
        excelMap.put("1016", "oneRcl");//1号燃油沉淀舱液位
        excelMap.put("1014", "oneRrl");//1号燃油日用舱液位
        excelMap.put("1017", "twoRcl");//2号燃油沉淀舱液位
        excelMap.put("1015", "twoRrl");//2号燃油日用舱液位

        String value = excelMap.get(key);
        if (value == null) {
            return key;
        } else {
            return value;
        }
    }

    public String getShipName(String mmsi,String fileName){
        ShipPO shipBo = shipMapper.selectShipInfoByMmsi(mmsi);
        if (shipBo == null) {
            sendErrorMessage(new ErrorLog("该条船未配置mmsi编号为" + mmsi, fileName));
            return "invalidmmsi";
        } else {
            return shipBo.getName();
        }
    }

    /**
     *  单位转换
     */
    public static String unit(String un) {
        Map<String, String> unitMap = new HashMap<>();
        unitMap.put("C", "℃");
        unitMap.put("B", "Mpa");
        unitMap.put("M", "m");
        unitMap.put("P", "%");
        unitMap.put("E", "mm");
        unitMap.put("R", "RPM");
        un = unitMap.get(un);
        return un;
    }

    /**
     * 16进制 转 10进制整数
     */
    public static String hexToDecimal(String hex) {
        int out = 0;
        for (int i = 0; i < hex.length(); i++) {
            char hexChar = hex.charAt(i);
            out = out * 16 + charToDecimal(hexChar);
        }
        return String.valueOf(out);
    }

    /**
     * 16进制 转 10进制浮点数
     */
    public static String hexToFloat(String hex) {
        return String.valueOf((double)Float.intBitsToFloat(Integer.parseInt(hexToDecimal(hex))));
    }

    /**
     * 将字符转化为数字 （0-9 A B C D E F）->（0-15）   16进制字符转换
     */
    public static int charToDecimal(char c) {
        if (c >= 'A' && c <= 'F') return 10 + c - 'A';
        else if (c >= 'a' && c <= 'f') return 10 + c - 'a';
        else if (c >= '0' && c <= '9') return c - '0';
        else throw new RuntimeException("");
    }

    /**
     * 获取船的横倾纵倾
     */
    public Double getHeelAndTrim(String minus,String minute,String type){
        Double dminus = Double.valueOf(minus.split("m")[0]);
        Double dminute = Double.valueOf(minute.split("m")[0]);
        Double value = 0.0;
        if("heel".equals(type)){
            Double heel = (-1.6/180)*Math.PI;
            Double right = 7.3;
            Double left = 8.2;
            Double R1 = right-left;
            Double L1 = R1/(Math.sin(heel));
            Double r1 = dminus - dminute;
            value = Math.asin(r1/L1)/Math.PI*180;
            BigDecimal bg = new BigDecimal(value);
            value = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        }else if("trim".equals(type)){
            Double trim = (-1.1/180)*Math.PI;
            Double head = 6.9;
            Double bottom = 9.1;
            Double R2 = head-bottom;
            Double L2 = R2/(Math.sin(trim));
            Double r2 = dminus - dminute;
            value = Math.asin(r2/L2)/Math.PI*180;
            BigDecimal bg = new BigDecimal(value);
            value = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        return value;
    }

    public void sendErrorMessage(ErrorLog errorLog){
        try {
            Date date = new Date();//获得系统时间.
            SimpleDateFormat sdf = new SimpleDateFormat(" yyyy-MM-dd HH:mm:ss ");
            String nowTime = sdf.format(date);
            errorLog.setReadTime(nowTime);
            errorLogMapper.insert(errorLog);
        }catch (Exception e){
            log.error("记录错误数据库出错",e);
        }
    }

    /** 
     * 计算发电机功率信号
     * @methodName getGeneratorPower
     * @author Zhongwl
     * @date 2022/3/14 17:02 
     * @param hexDecimal hexDecimal 
     * @return java.lang.String
     */
    public String getGeneratorPower(String hexDecimal) {
        if (!StringUtils.isEmpty(hexDecimal)) {
            double value = Double.parseDouble(hexDecimal) * 1000 / 4095;
            BigDecimal bg = new BigDecimal(value);
            BigDecimal power = bg.setScale(2, RoundingMode.HALF_UP);
            return String.valueOf(power);
        }
        return "0.00";
    }

    public static void main(String[] args) {
        String v = "02 03 14 40 B9 E1 39 45 3D 8C E0 4A 39 19 AF 42 E7 61 EC 44 5B F2 7B ";
        String vv = v.replaceAll(" ","");

        System.out.println(hexToFloat(vv.substring(14, 22)));
        System.out.println(hexToFloat(vv.substring(22, 30)));
        System.out.println(hexToFloat(vv.substring(30, 38)));
        System.out.println(hexToFloat(vv.substring(38, 46)));
    }

    /**
     * 发送告警邮件
     * @methodName sendAlarmEmail
     * @author Zhongwl
     * @date 2022/3/10 18:35
     * @param emailConfigMap emailConfigMap
     * @param contentMap contentMap
     * @param alarmConfigMap alarmConfigMap
     * @return void
     */
    private static void sendAlarmEmail(Map<String,String> emailConfigMap, Map<String,String> contentMap,Map<String, String> alarmConfigMap) {
        try {
            MailAccount account = new MailAccount();
            account.setHost(emailConfigMap.get("email_host"));
            account.setPort(Integer.parseInt(emailConfigMap.get("email_port")));
            account.setAuth(true);
            account.setFrom(emailConfigMap.get("email_from"));
            if (!StringUtils.isEmpty(emailConfigMap.get("email_user"))) {
                account.setUser(emailConfigMap.get("email_user"));
            }
            account.setPass(emailConfigMap.get("email_pass"));
            String convertContent = "";
            if (contentMap.containsKey("logPath")) {
                File file = new File(contentMap.get("logPath"));
                contentMap.put("logFileName", file.getName());
                convertContent = "船舶名称：${shipName}，日志文件：${logFileName} ，log文件异常，异常类型：${errorType}。";
            } else {
                convertContent = "船舶名称：${shipName}，日志目录：${logFolder} ，log文件异常，异常类型：${errorType}。";
            }
            for (String convertText : contentMap.keySet()) {
                convertContent = convertContent.replace("${" + convertText + "}", contentMap.get(convertText));
            }
            StringBuffer content = new StringBuffer();
            // 个性签名，通过http://mail.apsat.com/，登录邮箱获取邮箱签名，图片转成base64.
            String signature = "<p style=\"color: rgb(0, 0, 0); font-family: &quot;Microsoft YaHei&quot;; font-size: medium; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; text-decoration-thickness: initial; text-decoration-style: initial; text-decoration-color: initial;\"> <strong style=\"color: rgb(128, 128, 128); font-family: Arial; font-size: 14px; white-space: normal;\">船舶数字化与岸端辅助系统运营中心<br/></strong><span style=\"color: rgb(128, 128, 128); font-family: Arial; font-size: 14px;\"><span style=\"color: rgb(128, 128, 128); font-family: Arial; font-size: 14px;\"> Operation Center</span> of Ship Digitalization and Shore Auxiliary System</span><span style=\"color: rgb(128, 128, 128); font-family: Arial; font-size: 14px;\"></span><br style=\"color: rgb(128, 128, 128); font-family: Arial; font-size: 14px; white-space: normal;\"/><span style=\"color: rgb(128, 128, 128); font-family: Arial; font-size: 14px;\">APT Mobile SatCom Limited (APSATCOM) | 亚太卫星宽带通信（深圳）有限公司</span> </p> <p style=\"color: rgb(0, 0, 0); font-family: &quot;Microsoft YaHei&quot;; font-size: medium; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; text-decoration-thickness: initial; text-decoration-style: initial; text-decoration-color: initial;\"> <span style=\"color: rgb(128, 128, 128); font-family: Arial; font-size: 14px;\"><span style=\"color: rgb(128, 128, 128); font-family: Arial; font-size: 14px;\">Tel: </span>+86 17601021623<br/></span> </p> <p style=\"color: rgb(0, 0, 0); font-family: &quot;Microsoft YaHei&quot;; font-size: medium; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; text-decoration-thickness: initial; text-decoration-style: initial; text-decoration-color: initial;\"> <span style=\"color: rgb(128, 128, 128); font-family: Arial; font-size: 14px;\"><span style=\"color: rgb(128, 128, 128); font-family: Arial; font-size: 14px;\">Fax: +86 755 23508989</span><br/></span> </p> <p style=\"color: rgb(0, 0, 0); font-family: &quot;Microsoft YaHei&quot;; font-size: medium; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; text-decoration-thickness: initial; text-decoration-style: initial; text-decoration-color: initial;\"> <span style=\"color: rgb(128, 128, 128); font-family: Arial; font-size: 14px;\"><img src=\"data:image/png;base64,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\" title=\"APSATlogo.png\" style=\"width: 201px; height: 72px;\"/></span> </p> <p style=\"color: rgb(0, 0, 0); font-family: &quot;Microsoft YaHei&quot;; font-size: medium; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; text-decoration-thickness: initial; text-decoration-style: initial; text-decoration-color: initial;\"> <a class=\"moz-txt-link-freetext\" href=\"http://www.apsat.com/\" target=\"_blank\" style=\"font-family: Arial; font-size: 14px; white-space: normal;\">http://www.apsat.com</a> </p> <table width=\"603\"> <tbody> <tr class=\"firstRow\" valign=\"top\" height=\"8\"> <td style=\"border-width: 0px; border-style: solid; border-color: rgb(0, 0, 0); padding: 0px;\" width=\"206\" valign=\"middle\"> <img src=\"http://www.zqfinancial.com/images/mail/fen1.jpg\" style=\"width: 479px; height: 8px;\"/><br/><span style=\"font-size: 12px; color: rgb(79, 79, 79); font-family: sans-serif;\">本邮件载有秘密信息，请您恪守保密义务；未经授权者不得复印、公开、使用本邮件及内容！谢谢合作！</span><br/><span style=\"font-size: 12px; color: rgb(79, 79, 79); font-family: sans-serif;\">This email communication is confidential, Recipient(s) named above is (are) obligated to maintain secrecy. Any unauthorized dissemination, distribution or copying of this communication is (are) strictly prohibited. Thank you.</span> </td> </tr> </tbody> </table> <p> <br/> </p>";
            content.append("&nbsp;系统管理员：<br>");
            content.append("&nbsp;&nbsp;&nbsp;&nbsp;(北京)时间(").append(DateUtil.now()).append(")，").append(convertContent).append(" <br>");
            content.append("&nbsp;&nbsp;&nbsp;&nbsp;为避免带来不便，请登录系统查看详情！<br><br><br><br>");
            // content.append("<hr style=\"width: 210px;\" color=\"#b5c4df\" size=\"1\" align=\"left\">");
            // content.append("&nbsp;亚太星通智慧航运团队 <br>");
            // content.append("&nbsp;").append(DateUtil.format(new Date(), DatePattern.CHINESE_DATE_PATTERN));
            log.info("告警邮件内容：" + content.toString());
            content.append(signature);
            Mail mail = Mail.create(account)
                    .setTos(alarmConfigMap.get("warn_email_to"))
                    .setTitle(alarmConfigMap.get("warn_email_title"))
                    .setContent(content.toString(), true);
            mail.send();
            log.info("发送告警邮件完成...");
        } catch (Exception e) {
            log.error(contentMap.toString() + "发送告警邮件异常!" + e.getMessage(), e);
        }
    }
}
