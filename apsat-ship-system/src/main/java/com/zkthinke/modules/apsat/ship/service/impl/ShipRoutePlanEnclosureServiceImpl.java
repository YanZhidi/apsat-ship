package com.zkthinke.modules.apsat.ship.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zkthinke.modules.apsat.ship.domain.*;
import com.zkthinke.modules.apsat.ship.mapper.ReefShipwrecksMapper;
import com.zkthinke.modules.apsat.ship.mapper.ShipRoutePlanAlarmDao;
import com.zkthinke.modules.apsat.ship.mapper.ShipRoutePlanDao;
import com.zkthinke.modules.apsat.ship.mapper.ShipRoutePlanEnclosureDao;
import com.zkthinke.modules.apsat.ship.service.ArtificialFenceService;
import com.zkthinke.modules.apsat.ship.service.ShipAlarmThresholdService;
import com.zkthinke.modules.apsat.ship.service.ShipRoutePlanEnclosureService;
import com.zkthinke.modules.apsat.ship.service.TyphoonService;
import com.zkthinke.modules.apsat.ship.service.dto.ShipRoutePlanAlarmQueryCriteria;
import com.zkthinke.modules.apsat.ship.service.dto.ShipRoutePlanDetailDTO;
import com.zkthinke.modules.apsat.ship.service.dto.ShipRoutePlanEnclosureDTO;
import com.zkthinke.modules.apsat.ship.service.dto.ShipRoutePlanEnclosureQueryCriteria;
import com.zkthinke.modules.apsat.ship.service.mapper.ShipRoutePlanDetailMapper;
import com.zkthinke.modules.apsat.ship.service.mapper.ShipRoutePlanEnclosureMapper;
import com.zkthinke.modules.apsat.ship.utils.FieldUnitUtil;
import com.zkthinke.modules.apsat.ship.utils.WaterDeepUtil;
import com.zkthinke.modules.apsat.ship.utils.WeatherUtil;
import com.zkthinke.modules.common.constant.Constant;
import com.zkthinke.modules.common.utils.LatitudeLongitideUtils;
import com.zkthinke.response.ResponseResult;
import com.zkthinke.service.SMSService;
import com.zkthinke.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.awt.geom.Point2D;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author weicb
 * @date 2020-10-15
 */
@Service
@Slf4j
public class ShipRoutePlanEnclosureServiceImpl implements ShipRoutePlanEnclosureService {

    @Autowired
    private ShipRoutePlanEnclosureDao shipRoutePlanEnclosureDao;

    @Autowired
    private ShipRoutePlanEnclosureMapper shipRoutePlanEnclosureMapper;

    @Autowired
    private ShipRoutePlanDetailMapper shipRoutePlanDetailMapper;

    @Autowired
    private ShipRoutePlanAlarmDao shipRoutePlanAlarmDao;

    @Autowired
    private ShipRoutePlanDao shipRoutePlanDao;

    @Autowired
    private ShipAlarmThresholdService shipAlarmThresholdService;

    @Resource
    private ReefShipwrecksMapper reefShipwrecksMapper;

    @Autowired
    private SMSService smsService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TyphoonService typhoonService;

    @Autowired
    private ArtificialFenceService artificialFenceService;

    @Autowired
    private WeatherUtil weatherUtil;

    @Override
    public List<ShipRoutePlanEnclosureDTO> findAll(ShipRoutePlanEnclosureQueryCriteria criteria) {
        List<ShipRoutePlanEnclosure> shipRoutePlanEnclosureList = shipRoutePlanEnclosureDao.findAll(criteria);

        List<ShipRoutePlanEnclosureDTO> list = shipRoutePlanEnclosureList.stream().map(s -> {
            ShipRoutePlanEnclosureDTO shipRoutePlanEnclosureDTO = shipRoutePlanEnclosureMapper.toDto(s);
            return shipRoutePlanEnclosureDTO;
        }).collect(Collectors.toList());
        return list;
    }

    @Override
    public List<ShipRoutePlanDetailDTO> findAllDetail(ShipRoutePlanEnclosureQueryCriteria criteria) {
        List<ShipRoutePlanDetail> shipRoutePlanDetailList = shipRoutePlanEnclosureDao.findAllDetail(criteria);

        List<ShipRoutePlanDetailDTO> list = shipRoutePlanDetailList.stream().map(s -> {
            ShipRoutePlanDetailDTO shipRoutePlanDetailDTO = shipRoutePlanDetailMapper.toDto(s);
            return shipRoutePlanDetailDTO;
        }).collect(Collectors.toList());
        return list;
    }

    @Override
    public List<ShipRoutePlanDetailDTO> getPlanDetailByPlanId(String planId) {
        List<ShipRoutePlanDetail> shipRoutePlanDetailList = shipRoutePlanEnclosureDao.getPlanDetailByPlanId(planId);

        List<ShipRoutePlanDetailDTO> list = shipRoutePlanDetailList.stream()
                .map(s -> shipRoutePlanDetailMapper.toDto(s))
                .collect(Collectors.toList());
        return list;
    }

    @Override
    public Map<String, Object> findAlarmByParam(PageParam<ShipRoutePlanAlarmQueryCriteria> param,List<Long> shipIds) {
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        if (CollectionUtils.isEmpty(shipIds) && !SecurityUtils.getAdminRole()) {
            //用户没有权限
            return PageUtil.toPage(new ArrayList<>(),0);
        }
        List<ShipRoutePlanAlarm> list = shipRoutePlanAlarmDao.findAlarmByParam(param.getParam(),shipIds);
        PageInfo<ShipRoutePlanAlarm> pageInfo = new PageInfo<>(list);
        return PageUtil.toPage(list, pageInfo.getTotal());
    }

    @Override
    public List<ShipRoutePlanAlarm> findAlarmLimit(String shipId) {
        List<ShipRoutePlanAlarm> list = shipRoutePlanAlarmDao.findAlarmLimit(shipId);
        return list;
    }

    @Override
    public void isInEnclosure(String shipId, Double longitide, Double latitude, Long timeStamp) {
        String timeMsg = DateUtils.long2StrForAlarmMsg(timeStamp);
        String timeLog = DateUtils.long2StrForAlarmLog(timeStamp);
        String redisKey = "yawFlag:" + shipId;
        Long planId = 0L;
        boolean flag = false;
        ShipRoutePlanEnclosureQueryCriteria criteria = new ShipRoutePlanEnclosureQueryCriteria();
        criteria.setShipId(shipId);
        //根据shipId查询shipName
        String shipName = shipRoutePlanDao.queryShipNameById(shipId);

        //根据shipId查询计划轨迹航次
        String voyageNumber = shipRoutePlanDao.queryShipVoyageNumberById(shipId);
        //根据shipId查询计划轨迹航次
        List<String> phoneList = shipRoutePlanDao.queryPhoneByRoleLevel(shipId);
        String phones = "";
        if (!phoneList.isEmpty()) {
            for (int i = 0; i < phoneList.size(); i++) {
                phones += phoneList.get(i);
                if (i != phoneList.size() - 1) {
                    phones += ",";
                }
            }
        }

        //根据shipId查询电子围栏信息
        List<ShipRoutePlanEnclosure> shipRoutePlanEnclosureList = shipRoutePlanEnclosureDao.findAll(criteria);
        if (shipRoutePlanEnclosureList.isEmpty()) {
            log.info("根据船舶ID未获取到的电子围栏经纬度数据");
            return;
        }
        planId = shipRoutePlanEnclosureList.get(0).getId();
        //判断经纬度数据是否在电子围栏的矩形内
        for (int i = 0; i < shipRoutePlanEnclosureList.size(); i++) {
            if (i > 0) {
                ShipRoutePlanEnclosure obj = shipRoutePlanEnclosureList.get(i);
                ShipRoutePlanEnclosure objBefore = shipRoutePlanEnclosureList.get(i - 1);
                Point2D.Double point = new Point2D.Double(longitide, latitude);
                List<Point2D.Double> pts = new ArrayList<>();
                pts.add(new Point2D.Double(Double.parseDouble(objBefore.getEndLeftLongitude()), Double.parseDouble(objBefore.getEndLeftLatitude())));
                pts.add(new Point2D.Double(Double.parseDouble(objBefore.getEndRightLongitude()), Double.parseDouble(objBefore.getEndRightLatitude())));
                pts.add(new Point2D.Double(Double.parseDouble(obj.getBeginRightLongitude()), Double.parseDouble(obj.getBeginRightLatitude())));
                pts.add(new Point2D.Double(Double.parseDouble(obj.getBeginLeftLongitude()), Double.parseDouble(obj.getBeginLeftLatitude())));
                boolean inPolygon = LatitudeLongitideUtils.isInPolygon(point, pts);
                if (inPolygon) {
                    flag = true;
                    break;
                }
            }
        }
        String radius = shipAlarmThresholdService.getThresholdLimit(shipId,"计划航线电子围栏半径");
        //1海里 = 1852米
        double radiusD = Double.parseDouble(radius) * 1852;
        //不在矩形范围内
        if (!flag) {
            //根据shipId查询计划轨迹信息
            List<ShipRoutePlanDetail> shipRoutePlanDetailList = shipRoutePlanEnclosureDao.findAllDetail(criteria);
            if (shipRoutePlanDetailList.isEmpty()) {
                log.info("根据船舶ID未获取到的计划轨迹经纬度数据");
                return;
            }
            planId = shipRoutePlanDetailList.get(0).getId();
            //判断经纬度数据是否在计划轨迹坐标的圆内
            for (int i = 0; i < shipRoutePlanDetailList.size(); i++) {
                ShipRoutePlanDetail shipRoutePlanDetail = shipRoutePlanDetailList.get(i);
                boolean inCircle = LatitudeLongitideUtils.isInCircle(radiusD, latitude, longitide, Double.parseDouble(shipRoutePlanDetail.getLatitude()), Double.parseDouble(shipRoutePlanDetail.getLongitude()));
                if (inCircle) {
                    flag = true;
                    break;
                }
            }
        }
        log.info("当前点是否在围栏内[{}]，经纬度为[{}],[{}],调用时间戳为:{},时间为:{}", flag,longitide,latitude,timeStamp,timeMsg);
        //查询Redis中的偏航标记[yawFlag]  true:上次偏航  false 上次未偏航
        boolean yawFlag = false;
        Object residsFlag = redisTemplate.opsForValue().get(redisKey);
        log.info("Redis获取的偏航标记数据[{}]", residsFlag);
        if (residsFlag == null) {
            //该条船没有Redis标记,新增标记
            redisTemplate.opsForValue().set(redisKey, false);
        } else {
            yawFlag = (boolean) residsFlag;
        }

        //短信对象
        Map<String, String> format = LatitudeLongitideUtils.format(longitide, latitude);
        Map<String, String> map = new HashMap<>();
        map.put("voyageNumber", voyageNumber);
        map.put("name", shipName);
        map.put("shipName", shipName);
        map.put("time", timeMsg);
        map.put("longLat", "（" + format.get("longitide") + "，" + format.get("latitude") + "）");

        //日志对象
        ShipRoutePlanAlarm alarm = new ShipRoutePlanAlarm();
        alarm.setShipId(shipId);
        alarm.setPlanId(String.valueOf(planId));
        alarm.setOccurrenceTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        //如果上次未偏航,本次偏航(不在电子围栏内),则记录偏航日志,发送偏航短信,上次偏航标记改为true
        if (!yawFlag && !flag) {

            alarm.setDescription(shipName + " " + timeMsg + " " + Constant.ALARM_TYPE_YAW_DESC);
            alarm.setAlarmType(Constant.ALARM_TYPE_YAW);
            //记录日志
            shipRoutePlanAlarmDao.insertAlarm(alarm);
            //发送短信
            if (phones.length() > 0) {
                smsService.doSend(phones, Constant.SMS_TEMPLATECODE_YAW, map);
            }
            log.info("发送的偏离航线的预警短信号码[{}]", phones);
            //修改上次偏航标记
            redisTemplate.opsForValue().set(redisKey, true);
        }

        //如果上次偏航,本次未偏航(在电子围栏内),则记录恢复日志,发送恢复短信,上次偏航标记改为false
        if (yawFlag && flag) {
            alarm.setDescription(shipName + " " + timeMsg + " " + Constant.ALARM_TYPE_NOT_YAW_DESC);
            alarm.setAlarmType(Constant.ALARM_TYPE_NOT_YAW);
            //记录日志
            shipRoutePlanAlarmDao.insertAlarm(alarm);
            //发送短信
            if (phones.length() > 0) {
                smsService.doSend(phones, Constant.SMS_TEMPLATECODE_NOT_YAW, map);
            }
            log.info("发送的恢复航线的预警短信号码[{}]", phones);
            //修改上次偏航标记
            redisTemplate.opsForValue().set(redisKey, false);
        }
    }

    @Override
    public void insertAlarmForNoPower(String shipId, Double revolutionSpeed, Double groundSpeed, Long timeStamp, Double longitide, Double latitude) {
        String timeMsg = DateUtils.long2StrForAlarmMsg(timeStamp);
        String timeLog = DateUtils.long2StrForAlarmLog(timeStamp);
        String redisKey = "noPowerFlag:" + shipId;
        //查询Redis中的失速标记true:上次失速,false:上次未失速
        boolean noPowerFlag = false;
        Object residsFlag = redisTemplate.opsForValue().get(redisKey);
        log.info("Redis获取的失速标记数据[{}]", residsFlag);
        if (residsFlag == null) {
            //该条船没有Redis标记,新增标记
            redisTemplate.opsForValue().set(redisKey, false);
        } else {
            noPowerFlag = (boolean) residsFlag;
        }
        //根据shipId查询shipName
        String shipName = shipRoutePlanDao.queryShipNameById(shipId);
        //查询短信接收人号码
        List<String> phoneList = shipRoutePlanDao.queryPhoneByRoleLevel(shipId);
        //根据shipId查询计划轨迹航次
        String voyageNumber = shipRoutePlanDao.queryShipVoyageNumberById(shipId);
        String phones = "";
        if (!phoneList.isEmpty()) {
            for (int i = 0; i < phoneList.size(); i++) {
                phones += phoneList.get(i);
                if (i != phoneList.size() - 1) {
                    phones += ",";
                }
            }
        }

        Map<String, String> format = LatitudeLongitideUtils.format(longitide, latitude);
        //短信对象
        Map<String, String> map = new HashMap<>();
        map.put("name", shipName);
        map.put("shipName", shipName);
        map.put("time", timeMsg);
        map.put("voyageNumber", voyageNumber);
        map.put("longLat", "（" + format.get("longitide") + "，" + format.get("latitude") + "）");

        if (revolutionSpeed == 0 && groundSpeed > 0.5) {
            if (!noPowerFlag) {
                //日志对象
                ShipRoutePlanAlarm alarm = new ShipRoutePlanAlarm();
                alarm.setShipId(shipId);
                alarm.setOccurrenceTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                alarm.setAlarmType(Constant.ALARM_TYPE_NO_POWER);
                alarm.setDescription(shipName + " " + timeMsg + " " + Constant.ALARM_TYPE_NO_POWER_DESC);
                shipRoutePlanAlarmDao.insertAlarm(alarm);

                //发送短信
                if (phones.length() > 0) {
                    smsService.doSend(phones, Constant.SMS_TEMPLATECODE_NO_POWER, map);
                    //修改上次失速标记
                    redisTemplate.opsForValue().set(redisKey, true);
                }
                log.info("发送的恢复航线的预警短信号码[{}]", phones);
            }
        } else {
            if (noPowerFlag) {

                //日志对象
                ShipRoutePlanAlarm alarm = new ShipRoutePlanAlarm();
                alarm.setShipId(shipId);
                alarm.setOccurrenceTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                alarm.setAlarmType(Constant.ALARM_TYPE_NOT_NO_POWER);
                alarm.setDescription(shipName + " " + timeMsg + " " + Constant.ALARM_TYPE_NOT_NO_POWER_DESC);
                shipRoutePlanAlarmDao.insertAlarm(alarm);

                //发送短信
                if (phones.length() > 0) {
                    smsService.doSend(phones, Constant.SMS_TEMPLATECODE_NOT_NO_POWER, map);
                    //修改上次失速标记
                    redisTemplate.opsForValue().set(redisKey, false);
                }
                log.info("发送的恢复航线的预警短信号码[{}]", phones);
            }
        }

    }

    @Override
    public void insertAlarmForChangeTarget(String shipId, Long timeStamp) {
        String timeMsg = DateUtils.long2StrForAlarmMsg(timeStamp);
        String timeLog = DateUtils.long2StrForAlarmLog(timeStamp);
        //根据shipId查询shipName
        String shipName = shipRoutePlanDao.queryShipNameById(shipId);
        //查询短信接收人号码
        List<String> phoneList = shipRoutePlanDao.queryPhoneByRoleLevel(shipId);
        String phones = "";
        if (!phoneList.isEmpty()) {
            for (int i = 0; i < phoneList.size(); i++) {
                phones += phoneList.get(i);
                if (i != phoneList.size() - 1) {
                    phones += ",";
                }
            }
        }
        //日志对象
        ShipRoutePlanAlarm alarm = new ShipRoutePlanAlarm();
        alarm.setShipId(shipId);
        alarm.setOccurrenceTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        alarm.setAlarmType(Constant.ALARM_TYPE_CHANGE_TARGET);
        alarm.setDescription(shipName + " " + timeMsg + " " + Constant.ALARM_TYPE_CHANGE_TARGET_DESC);
        shipRoutePlanAlarmDao.insertAlarm(alarm);

        //短信对象
        Map<String, String> map = new HashMap<>();
        map.put("name", shipName);
        map.put("time", timeMsg);

        //发送短信
        if (phones.length() > 0) {
            smsService.doSend(phones, Constant.SMS_TEMPLATECODE_CHANGE_TARGET, map);
        }
        log.info("发送的恢复航线的预警短信号码[{}]", phones);
    }

    @Override
    public List<ShipRoutePlanDanger> findAllDanger(String shipId) {
        List<ShipRoutePlanDanger> list = shipRoutePlanEnclosureDao.findAllDanger(shipId);
        return list;
    }

    @Override
    public void isHitTheRock(String shipId, Double longitide, Double latitude, Double shipDeep, Long timeStamp) {

        String timeMsg = DateUtils.long2StrForAlarmMsg(timeStamp);
        /*String redisKey = "hitTheRockFlag:" + shipId;
        //查询Redis中的触礁风险标记true:上次有触礁风险,false:上次无触礁风险
        boolean hitTheRockFlag = false;
        Object residsFlag = redisTemplate.opsForValue().get(redisKey);
        log.info("Redis获取的触礁风险标记数据[{}]", residsFlag);
        if (residsFlag == null) {
            //该条船没有Redis标记,新增标记
            redisTemplate.opsForValue().set(redisKey, false);
        } else {
            hitTheRockFlag = (boolean) residsFlag;
        }*/

        //根据shipId查询shipName
        String shipName = shipRoutePlanDao.queryShipNameById(shipId);

        //根据经纬度信息获取水深
        Double retWaterDeep = WaterDeepUtil.getWaterDeep(longitide, latitude);

        if (retWaterDeep==null){
            log.info("调用API未获取到水深数值");
            return;
        }
        //剩余水深
        Double surplusDeep = retWaterDeep-shipDeep;
        //查询剩余水深限制
        String surplusDeepLimit = shipAlarmThresholdService.getThresholdLimit(shipId,"船舶吃水富余深度");

        if (surplusDeep < Double.parseDouble(surplusDeepLimit)) {
            //存在触礁风险,发出警告
            //日志对象
            ShipRoutePlanAlarm alarm = new ShipRoutePlanAlarm();
            alarm.setShipId(shipId);
            alarm.setOccurrenceTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            alarm.setAlarmType(Constant.ALARM_TYPE_HIT_ROCK);
            alarm.setDescription(shipName + " " + timeMsg + " 吃水富余"+String.format("%.2f", surplusDeep)+"米," + Constant.ALARM_TYPE_HIT_ROCK_DESC);
            shipRoutePlanAlarmDao.insertAlarm(alarm);
        }
    }

    @Override
    public void anchorWalkingWarning(String shipId, String sailingStatus,Long collectTime, String longitude, String latitude) {
        boolean anchorWalkFlag = false;

        String shipName = shipRoutePlanDao.queryShipNameById(shipId);
        String occurrenceTime = DateUtils.long2StrForAlarmLog(collectTime);
        String descriptionTime = DateUtils.long2StrForAlarmMsg(collectTime);
        ShipRoutePlanAlarm alarm = new ShipRoutePlanAlarm();

        String anchorWalkLonLatKey = "anchorWalkLonLat:"+shipId;
        String anchorWalkKey = "anchorWalkFlag:" + shipId;
        Object redisFlag = redisTemplate.opsForValue().get(anchorWalkKey);
        if (redisFlag == null) {
            //Redis中没有标记，设置一个
            redisTemplate.opsForValue().set(anchorWalkKey, false);
        } else {
            anchorWalkFlag = (boolean) redisFlag;
        }

        if (!"锚泊".equals(sailingStatus)) {
            //当前不是锚泊
            if (anchorWalkFlag) {
                //之前是走锚，新增一条恢复信息，设置走锚标记为false
                alarm.setShipId(shipId);
                alarm.setOccurrenceTime(occurrenceTime);
                alarm.setAlarmType(Constant.ALARM_TYPE_NOT_ANCHOR_WALK);
                alarm.setDescription(shipName + " " + descriptionTime + " " + Constant.ALARM_TYPE_NOT_ANCHOR_WALK_DESC);
                shipRoutePlanAlarmDao.insertAlarm(alarm);
                redisTemplate.opsForValue().set(anchorWalkKey, false);
            }
            //删除经纬度
            redisTemplate.delete(anchorWalkLonLatKey);
        }else {
            //当前是锚泊

            //第一次锚泊的经纬度
            Object lonLatRedis = redisTemplate.opsForValue().get(anchorWalkLonLatKey);
            String lastLon;
            String lastLat;
            if (lonLatRedis == null){
                //第一次锚泊，保存经纬度，这一次不可能走锚，直接结束
                redisTemplate.opsForValue().set(anchorWalkLonLatKey,longitude+","+latitude);
                return;
            }else {
                lastLon = ((String)lonLatRedis).split(",")[0];
                lastLat = ((String)lonLatRedis).split(",")[1];
            }

            //计算距离
            double distance = LatitudeLongitideUtils.calculateDistance(lastLon,lastLat,longitude,latitude);

            String anchorWalkDistanceLimit = shipAlarmThresholdService.getThresholdLimit(shipId,"船舶走锚预警半径");

            if (anchorWalkFlag){
                //之前是走锚，判断是否恢复
                if (distance <= Double.parseDouble(anchorWalkDistanceLimit)){
                    //已恢复，新增一条恢复信息，设置走锚标记为false
                    alarm.setShipId(shipId);
                    alarm.setOccurrenceTime(occurrenceTime);
                    alarm.setAlarmType(Constant.ALARM_TYPE_NOT_ANCHOR_WALK);
                    alarm.setDescription(shipName + " " + descriptionTime + " " + Constant.ALARM_TYPE_NOT_ANCHOR_WALK_DESC);
                    shipRoutePlanAlarmDao.insertAlarm(alarm);
                    redisTemplate.opsForValue().set(anchorWalkKey, false);
                }
            }else {
                //之前不是走锚，判断是否走锚
                if (distance> Double.parseDouble(anchorWalkDistanceLimit)){
                    //现在是走锚，添加告警信息，设置走锚标记为true
                    alarm.setShipId(shipId);
                    alarm.setOccurrenceTime(occurrenceTime);
                    alarm.setAlarmType(Constant.ALARM_TYPE_ANCHOR_WALK);
                    alarm.setDescription(shipName + " " + descriptionTime + " " + Constant.ALARM_TYPE_ANCHOR_WALK_DESC);
                    shipRoutePlanAlarmDao.insertAlarm(alarm);
                    redisTemplate.opsForValue().set(anchorWalkKey, true);
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void isCloseToReef(String longitude, String latitude, String shipId, Long collectTime) {
        List<PointBO> pointList = reefShipwrecksMapper.getReefList();
        String shipName = shipRoutePlanDao.queryShipNameById(shipId);
        String occurrenceTime = DateUtils.long2StrForAlarmLog(collectTime);
        String descriptionTime = DateUtils.long2StrForAlarmMsg(collectTime);
        List<ShipRoutePlanAlarm> alarmList = new ArrayList<>();
        for (PointBO pointBO : pointList) {
            //计算距离
            double distance = LatitudeLongitideUtils.calculateDistance(pointBO.getLongitude(), pointBO.getLatitude(), longitude, latitude);
            if (distance < Double.parseDouble(pointBO.getRadius()) * 1852) {
                ShipRoutePlanAlarm alarm = new ShipRoutePlanAlarm();
                alarm.setShipId(shipId);
                alarm.setOccurrenceTime(occurrenceTime);
                alarm.setAlarmType(Constant.ALARM_TYPE_HIT_ROCK);
                alarm.setDescription("触礁风险 " + shipName + descriptionTime + "距离暗礁岛屿距离过近");
                alarmList.add(alarm);
            }
        }
        if (!alarmList.isEmpty()) {
            shipRoutePlanAlarmDao.batchInsertAlarm(alarmList);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void isCloseToShipwrecks(String longitude, String latitude, String shipId, Long collectTime) {
        List<PointBO> pointList = reefShipwrecksMapper.getShipwrecksList();
        String shipName = shipRoutePlanDao.queryShipNameById(shipId);
        String occurrenceTime = DateUtils.long2StrForAlarmLog(collectTime);
        String descriptionTime = DateUtils.long2StrForAlarmMsg(collectTime);
        List<ShipRoutePlanAlarm> alarmList = new ArrayList<>();
        for (PointBO pointBO : pointList) {
            //计算距离
            double distance = LatitudeLongitideUtils.calculateDistance(pointBO.getLongitude(), pointBO.getLatitude(), longitude, latitude);
            if (distance < Double.parseDouble(pointBO.getRadius()) * 1852) {
                ShipRoutePlanAlarm alarm = new ShipRoutePlanAlarm();
                alarm.setShipId(shipId);
                alarm.setOccurrenceTime(occurrenceTime);
                alarm.setAlarmType(Constant.ALARM_TYPE_HIT_ROCK);
                alarm.setDescription("触礁风险 " + shipName + descriptionTime + "距离沉船点过近");
                alarmList.add(alarm);
            }
        }
        if (!alarmList.isEmpty()) {
            shipRoutePlanAlarmDao.batchInsertAlarm(alarmList);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void isCloseToTyphoon(String longitude, String latitude, String shipId, Long collectTime) {
        //获取激活的台风列表
        List<TyphoonBO> typhoonList = typhoonService.getActivatedTyphoonDetailList();
        if (CollectionUtils.isEmpty(typhoonList)) {
            return;
        }
        String shipName = shipRoutePlanDao.queryShipNameById(shipId);
        String occurrenceTime = DateUtils.long2StrForAlarmLog(collectTime);
        String descriptionTime = DateUtils.long2StrForAlarmMsg(collectTime);

        List<ShipRoutePlanAlarm> alarmList = new ArrayList<>();
        for (TyphoonBO typhoonBO : typhoonList) {
            List<TyphoonTrackBO> trackList = typhoonBO.getTrackList();
            if (CollectionUtils.isEmpty(trackList)) {
                continue;
            }
            //最新的轨迹点
            TyphoonTrackBO newestTrack = trackList.get(trackList.size() - 1);
            //计算距离
            double distance = LatitudeLongitideUtils.calculateDistance(newestTrack.getLng(), newestTrack.getLat(), longitude, latitude);
            if (distance <= 200.0 * 1852) {
                ShipRoutePlanAlarm alarm = new ShipRoutePlanAlarm();
                alarm.setShipId(shipId);
                alarm.setOccurrenceTime(occurrenceTime);
                alarm.setAlarmType(Constant.ALARM_TYPE_TYPHOON);
                alarm.setDescription(shipName + descriptionTime + "距离台风<=200海里，有沉船风险");
                alarmList.add(alarm);
            }
        }
        if (!alarmList.isEmpty()) {
            shipRoutePlanAlarmDao.batchInsertAlarm(alarmList);
        }
    }

    @Override
    public void isStranding(String shipId, String sensorDepth, Long collectTime) {
        if (StringUtils.isEmpty(sensorDepth)) {
            return;
        }
        String shipName = shipRoutePlanDao.queryShipNameById(shipId);
        String occurrenceTime = DateUtils.long2StrForAlarmLog(collectTime);
        String descriptionTime = DateUtils.long2StrForAlarmMsg(collectTime);

        String sensorDepthVal = FieldUnitUtil.removeUnit(sensorDepth);

        if (Double.parseDouble(sensorDepthVal) < 3) {
            ShipRoutePlanAlarm alarm = new ShipRoutePlanAlarm();
            alarm.setShipId(shipId);
            alarm.setOccurrenceTime(occurrenceTime);
            alarm.setAlarmType(Constant.ALARM_TYPE_STRANDING);
            alarm.setDescription(shipName + descriptionTime + " 测深仪水深" + sensorDepthVal + "米，有搁浅风险");
            shipRoutePlanAlarmDao.insertAlarm(alarm);

            //查询短信接收人号码
            List<String> phoneList = shipRoutePlanDao.queryPhoneByRoleLevel(shipId);
            String phones = "";
            if (!phoneList.isEmpty()) {
                for (int i = 0; i < phoneList.size(); i++) {
                    phones += phoneList.get(i);
                    if (i != phoneList.size() - 1) {
                        phones += ",";
                    }
                }
                Map<String, String> map = new HashMap<>();
                map.put("name", shipName);
                map.put("shipName", shipName);
                map.put("time", descriptionTime);
                map.put("depth", sensorDepthVal);
                smsService.doSend(phones, Constant.SMS_TEMPLATECODE_DEPTH_STRANDING, map);
            }
        }
    }

    @Override
    public void isInArtificialFence(String longitude, String latitude, String shipId, Long collectTime) {
        if (StringUtils.isEmpty(longitude) || StringUtils.isEmpty(latitude)) {
            return;
        }

        String shipName = shipRoutePlanDao.queryShipNameById(shipId);
        String occurrenceTime = DateUtils.long2StrForAlarmLog(collectTime);
        String descriptionTime = DateUtils.long2StrForAlarmMsg(collectTime);

        Point2D.Double point = new Point2D.Double(Double.parseDouble(longitude), Double.parseDouble(latitude));
        List<ArtificialFenceBO> artificialFenceList = artificialFenceService.getArtificialFenceList();

        List<ShipRoutePlanAlarm> alarmList = new ArrayList<>();
        for (ArtificialFenceBO artificialFenceBO : artificialFenceList) {
            List<Point2D.Double> pointList = artificialFenceBO.getPointList().stream()
                    .map(e -> new Point2D.Double(Double.parseDouble(e.getLongitude()), Double.parseDouble(e.getLatitude())))
                    .collect(Collectors.toList());
            if (LatitudeLongitideUtils.isInPolygon(point, pointList)) {
                ShipRoutePlanAlarm alarm = new ShipRoutePlanAlarm();
                alarm.setShipId(shipId);
                alarm.setOccurrenceTime(occurrenceTime);
                alarm.setAlarmType(Constant.ALARM_TYPE_STRANDING);
                alarm.setDescription(shipName + descriptionTime + "进入浅水区域，有搁浅风险");
                alarmList.add(alarm);
            }
        }
        if (!alarmList.isEmpty()) {
            shipRoutePlanAlarmDao.batchInsertAlarm(alarmList);

            //查询短信接收人号码
            List<String> phoneList = shipRoutePlanDao.queryPhoneByRoleLevel(shipId);
            String phones = "";
            if (!phoneList.isEmpty()) {
                for (int i = 0; i < phoneList.size(); i++) {
                    phones += phoneList.get(i);
                    if (i != phoneList.size() - 1) {
                        phones += ",";
                    }
                }
                Map<String, String> map = new HashMap<>();
                map.put("name", shipName);
                map.put("shipName", shipName);
                map.put("time", descriptionTime);
                smsService.doSend(phones, Constant.SMS_TEMPLATECODE_ARTIFICIAL_FENCE_STRANDING, map);
            }
        }
    }

    @Override
    public ResponseResult getWeather(ShipWeatherDTO shipWeatherDto) {
        return weatherUtil.getWeatherValue(shipWeatherDto);
    }
}
