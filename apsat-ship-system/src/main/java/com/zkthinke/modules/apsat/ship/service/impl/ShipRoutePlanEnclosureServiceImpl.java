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
            //??????????????????
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
        //??????shipId??????shipName
        String shipName = shipRoutePlanDao.queryShipNameById(shipId);

        //??????shipId????????????????????????
        String voyageNumber = shipRoutePlanDao.queryShipVoyageNumberById(shipId);
        //??????shipId????????????????????????
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

        //??????shipId????????????????????????
        List<ShipRoutePlanEnclosure> shipRoutePlanEnclosureList = shipRoutePlanEnclosureDao.findAll(criteria);
        if (shipRoutePlanEnclosureList.isEmpty()) {
            log.info("????????????ID??????????????????????????????????????????");
            return;
        }
        planId = shipRoutePlanEnclosureList.get(0).getId();
        //??????????????????????????????????????????????????????
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
        String radius = shipAlarmThresholdService.getThresholdLimit(shipId,"??????????????????????????????");
        //1?????? = 1852???
        double radiusD = Double.parseDouble(radius) * 1852;
        //?????????????????????
        if (!flag) {
            //??????shipId????????????????????????
            List<ShipRoutePlanDetail> shipRoutePlanDetailList = shipRoutePlanEnclosureDao.findAllDetail(criteria);
            if (shipRoutePlanDetailList.isEmpty()) {
                log.info("????????????ID??????????????????????????????????????????");
                return;
            }
            planId = shipRoutePlanDetailList.get(0).getId();
            //?????????????????????????????????????????????????????????
            for (int i = 0; i < shipRoutePlanDetailList.size(); i++) {
                ShipRoutePlanDetail shipRoutePlanDetail = shipRoutePlanDetailList.get(i);
                boolean inCircle = LatitudeLongitideUtils.isInCircle(radiusD, latitude, longitide, Double.parseDouble(shipRoutePlanDetail.getLatitude()), Double.parseDouble(shipRoutePlanDetail.getLongitude()));
                if (inCircle) {
                    flag = true;
                    break;
                }
            }
        }
        log.info("???????????????????????????[{}]???????????????[{}],[{}],??????????????????:{},?????????:{}", flag,longitide,latitude,timeStamp,timeMsg);
        //??????Redis??????????????????[yawFlag]  true:????????????  false ???????????????
        boolean yawFlag = false;
        Object residsFlag = redisTemplate.opsForValue().get(redisKey);
        log.info("Redis???????????????????????????[{}]", residsFlag);
        if (residsFlag == null) {
            //???????????????Redis??????,????????????
            redisTemplate.opsForValue().set(redisKey, false);
        } else {
            yawFlag = (boolean) residsFlag;
        }

        //????????????
        Map<String, String> format = LatitudeLongitideUtils.format(longitide, latitude);
        Map<String, String> map = new HashMap<>();
        map.put("voyageNumber", voyageNumber);
        map.put("name", shipName);
        map.put("shipName", shipName);
        map.put("time", timeMsg);
        map.put("longLat", "???" + format.get("longitide") + "???" + format.get("latitude") + "???");

        //????????????
        ShipRoutePlanAlarm alarm = new ShipRoutePlanAlarm();
        alarm.setShipId(shipId);
        alarm.setPlanId(String.valueOf(planId));
        alarm.setOccurrenceTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        //?????????????????????,????????????(?????????????????????),?????????????????????,??????????????????,????????????????????????true
        if (!yawFlag && !flag) {

            alarm.setDescription(shipName + " " + timeMsg + " " + Constant.ALARM_TYPE_YAW_DESC);
            alarm.setAlarmType(Constant.ALARM_TYPE_YAW);
            //????????????
            shipRoutePlanAlarmDao.insertAlarm(alarm);
            //????????????
            if (phones.length() > 0) {
                smsService.doSend(phones, Constant.SMS_TEMPLATECODE_YAW, map);
            }
            log.info("??????????????????????????????????????????[{}]", phones);
            //????????????????????????
            redisTemplate.opsForValue().set(redisKey, true);
        }

        //??????????????????,???????????????(??????????????????),?????????????????????,??????????????????,????????????????????????false
        if (yawFlag && flag) {
            alarm.setDescription(shipName + " " + timeMsg + " " + Constant.ALARM_TYPE_NOT_YAW_DESC);
            alarm.setAlarmType(Constant.ALARM_TYPE_NOT_YAW);
            //????????????
            shipRoutePlanAlarmDao.insertAlarm(alarm);
            //????????????
            if (phones.length() > 0) {
                smsService.doSend(phones, Constant.SMS_TEMPLATECODE_NOT_YAW, map);
            }
            log.info("??????????????????????????????????????????[{}]", phones);
            //????????????????????????
            redisTemplate.opsForValue().set(redisKey, false);
        }
    }

    @Override
    public void insertAlarmForNoPower(String shipId, Double revolutionSpeed, Double groundSpeed, Long timeStamp, Double longitide, Double latitude) {
        String timeMsg = DateUtils.long2StrForAlarmMsg(timeStamp);
        String timeLog = DateUtils.long2StrForAlarmLog(timeStamp);
        String redisKey = "noPowerFlag:" + shipId;
        //??????Redis??????????????????true:????????????,false:???????????????
        boolean noPowerFlag = false;
        Object residsFlag = redisTemplate.opsForValue().get(redisKey);
        log.info("Redis???????????????????????????[{}]", residsFlag);
        if (residsFlag == null) {
            //???????????????Redis??????,????????????
            redisTemplate.opsForValue().set(redisKey, false);
        } else {
            noPowerFlag = (boolean) residsFlag;
        }
        //??????shipId??????shipName
        String shipName = shipRoutePlanDao.queryShipNameById(shipId);
        //???????????????????????????
        List<String> phoneList = shipRoutePlanDao.queryPhoneByRoleLevel(shipId);
        //??????shipId????????????????????????
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
        //????????????
        Map<String, String> map = new HashMap<>();
        map.put("name", shipName);
        map.put("shipName", shipName);
        map.put("time", timeMsg);
        map.put("voyageNumber", voyageNumber);
        map.put("longLat", "???" + format.get("longitide") + "???" + format.get("latitude") + "???");

        if (revolutionSpeed == 0 && groundSpeed > 0.5) {
            if (!noPowerFlag) {
                //????????????
                ShipRoutePlanAlarm alarm = new ShipRoutePlanAlarm();
                alarm.setShipId(shipId);
                alarm.setOccurrenceTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                alarm.setAlarmType(Constant.ALARM_TYPE_NO_POWER);
                alarm.setDescription(shipName + " " + timeMsg + " " + Constant.ALARM_TYPE_NO_POWER_DESC);
                shipRoutePlanAlarmDao.insertAlarm(alarm);

                //????????????
                if (phones.length() > 0) {
                    smsService.doSend(phones, Constant.SMS_TEMPLATECODE_NO_POWER, map);
                    //????????????????????????
                    redisTemplate.opsForValue().set(redisKey, true);
                }
                log.info("??????????????????????????????????????????[{}]", phones);
            }
        } else {
            if (noPowerFlag) {

                //????????????
                ShipRoutePlanAlarm alarm = new ShipRoutePlanAlarm();
                alarm.setShipId(shipId);
                alarm.setOccurrenceTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                alarm.setAlarmType(Constant.ALARM_TYPE_NOT_NO_POWER);
                alarm.setDescription(shipName + " " + timeMsg + " " + Constant.ALARM_TYPE_NOT_NO_POWER_DESC);
                shipRoutePlanAlarmDao.insertAlarm(alarm);

                //????????????
                if (phones.length() > 0) {
                    smsService.doSend(phones, Constant.SMS_TEMPLATECODE_NOT_NO_POWER, map);
                    //????????????????????????
                    redisTemplate.opsForValue().set(redisKey, false);
                }
                log.info("??????????????????????????????????????????[{}]", phones);
            }
        }

    }

    @Override
    public void insertAlarmForChangeTarget(String shipId, Long timeStamp) {
        String timeMsg = DateUtils.long2StrForAlarmMsg(timeStamp);
        String timeLog = DateUtils.long2StrForAlarmLog(timeStamp);
        //??????shipId??????shipName
        String shipName = shipRoutePlanDao.queryShipNameById(shipId);
        //???????????????????????????
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
        //????????????
        ShipRoutePlanAlarm alarm = new ShipRoutePlanAlarm();
        alarm.setShipId(shipId);
        alarm.setOccurrenceTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        alarm.setAlarmType(Constant.ALARM_TYPE_CHANGE_TARGET);
        alarm.setDescription(shipName + " " + timeMsg + " " + Constant.ALARM_TYPE_CHANGE_TARGET_DESC);
        shipRoutePlanAlarmDao.insertAlarm(alarm);

        //????????????
        Map<String, String> map = new HashMap<>();
        map.put("name", shipName);
        map.put("time", timeMsg);

        //????????????
        if (phones.length() > 0) {
            smsService.doSend(phones, Constant.SMS_TEMPLATECODE_CHANGE_TARGET, map);
        }
        log.info("??????????????????????????????????????????[{}]", phones);
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
        //??????Redis????????????????????????true:?????????????????????,false:?????????????????????
        boolean hitTheRockFlag = false;
        Object residsFlag = redisTemplate.opsForValue().get(redisKey);
        log.info("Redis?????????????????????????????????[{}]", residsFlag);
        if (residsFlag == null) {
            //???????????????Redis??????,????????????
            redisTemplate.opsForValue().set(redisKey, false);
        } else {
            hitTheRockFlag = (boolean) residsFlag;
        }*/

        //??????shipId??????shipName
        String shipName = shipRoutePlanDao.queryShipNameById(shipId);

        //?????????????????????????????????
        Double retWaterDeep = WaterDeepUtil.getWaterDeep(longitide, latitude);

        if (retWaterDeep==null){
            log.info("??????API????????????????????????");
            return;
        }
        //????????????
        Double surplusDeep = retWaterDeep-shipDeep;
        //????????????????????????
        String surplusDeepLimit = shipAlarmThresholdService.getThresholdLimit(shipId,"????????????????????????");

        if (surplusDeep < Double.parseDouble(surplusDeepLimit)) {
            //??????????????????,????????????
            //????????????
            ShipRoutePlanAlarm alarm = new ShipRoutePlanAlarm();
            alarm.setShipId(shipId);
            alarm.setOccurrenceTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            alarm.setAlarmType(Constant.ALARM_TYPE_HIT_ROCK);
            alarm.setDescription(shipName + " " + timeMsg + " ????????????"+String.format("%.2f", surplusDeep)+"???," + Constant.ALARM_TYPE_HIT_ROCK_DESC);
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
            //Redis??????????????????????????????
            redisTemplate.opsForValue().set(anchorWalkKey, false);
        } else {
            anchorWalkFlag = (boolean) redisFlag;
        }

        if (!"??????".equals(sailingStatus)) {
            //??????????????????
            if (anchorWalkFlag) {
                //??????????????????????????????????????????????????????????????????false
                alarm.setShipId(shipId);
                alarm.setOccurrenceTime(occurrenceTime);
                alarm.setAlarmType(Constant.ALARM_TYPE_NOT_ANCHOR_WALK);
                alarm.setDescription(shipName + " " + descriptionTime + " " + Constant.ALARM_TYPE_NOT_ANCHOR_WALK_DESC);
                shipRoutePlanAlarmDao.insertAlarm(alarm);
                redisTemplate.opsForValue().set(anchorWalkKey, false);
            }
            //???????????????
            redisTemplate.delete(anchorWalkLonLatKey);
        }else {
            //???????????????

            //???????????????????????????
            Object lonLatRedis = redisTemplate.opsForValue().get(anchorWalkLonLatKey);
            String lastLon;
            String lastLat;
            if (lonLatRedis == null){
                //???????????????????????????????????????????????????????????????????????????
                redisTemplate.opsForValue().set(anchorWalkLonLatKey,longitude+","+latitude);
                return;
            }else {
                lastLon = ((String)lonLatRedis).split(",")[0];
                lastLat = ((String)lonLatRedis).split(",")[1];
            }

            //????????????
            double distance = LatitudeLongitideUtils.calculateDistance(lastLon,lastLat,longitude,latitude);

            String anchorWalkDistanceLimit = shipAlarmThresholdService.getThresholdLimit(shipId,"????????????????????????");

            if (anchorWalkFlag){
                //????????????????????????????????????
                if (distance <= Double.parseDouble(anchorWalkDistanceLimit)){
                    //????????????????????????????????????????????????????????????false
                    alarm.setShipId(shipId);
                    alarm.setOccurrenceTime(occurrenceTime);
                    alarm.setAlarmType(Constant.ALARM_TYPE_NOT_ANCHOR_WALK);
                    alarm.setDescription(shipName + " " + descriptionTime + " " + Constant.ALARM_TYPE_NOT_ANCHOR_WALK_DESC);
                    shipRoutePlanAlarmDao.insertAlarm(alarm);
                    redisTemplate.opsForValue().set(anchorWalkKey, false);
                }
            }else {
                //???????????????????????????????????????
                if (distance> Double.parseDouble(anchorWalkDistanceLimit)){
                    //????????????????????????????????????????????????????????????true
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
            //????????????
            double distance = LatitudeLongitideUtils.calculateDistance(pointBO.getLongitude(), pointBO.getLatitude(), longitude, latitude);
            if (distance < Double.parseDouble(pointBO.getRadius()) * 1852) {
                ShipRoutePlanAlarm alarm = new ShipRoutePlanAlarm();
                alarm.setShipId(shipId);
                alarm.setOccurrenceTime(occurrenceTime);
                alarm.setAlarmType(Constant.ALARM_TYPE_HIT_ROCK);
                alarm.setDescription("???????????? " + shipName + descriptionTime + "??????????????????????????????");
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
            //????????????
            double distance = LatitudeLongitideUtils.calculateDistance(pointBO.getLongitude(), pointBO.getLatitude(), longitude, latitude);
            if (distance < Double.parseDouble(pointBO.getRadius()) * 1852) {
                ShipRoutePlanAlarm alarm = new ShipRoutePlanAlarm();
                alarm.setShipId(shipId);
                alarm.setOccurrenceTime(occurrenceTime);
                alarm.setAlarmType(Constant.ALARM_TYPE_HIT_ROCK);
                alarm.setDescription("???????????? " + shipName + descriptionTime + "?????????????????????");
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
        //???????????????????????????
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
            //??????????????????
            TyphoonTrackBO newestTrack = trackList.get(trackList.size() - 1);
            //????????????
            double distance = LatitudeLongitideUtils.calculateDistance(newestTrack.getLng(), newestTrack.getLat(), longitude, latitude);
            if (distance <= 200.0 * 1852) {
                ShipRoutePlanAlarm alarm = new ShipRoutePlanAlarm();
                alarm.setShipId(shipId);
                alarm.setOccurrenceTime(occurrenceTime);
                alarm.setAlarmType(Constant.ALARM_TYPE_TYPHOON);
                alarm.setDescription(shipName + descriptionTime + "????????????<=200????????????????????????");
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
            alarm.setDescription(shipName + descriptionTime + " ???????????????" + sensorDepthVal + "?????????????????????");
            shipRoutePlanAlarmDao.insertAlarm(alarm);

            //???????????????????????????
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
                alarm.setDescription(shipName + descriptionTime + "????????????????????????????????????");
                alarmList.add(alarm);
            }
        }
        if (!alarmList.isEmpty()) {
            shipRoutePlanAlarmDao.batchInsertAlarm(alarmList);

            //???????????????????????????
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
