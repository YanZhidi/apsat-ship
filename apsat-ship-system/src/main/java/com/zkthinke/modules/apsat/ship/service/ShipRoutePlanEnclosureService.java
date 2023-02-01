package com.zkthinke.modules.apsat.ship.service;

import com.zkthinke.modules.apsat.ship.domain.ShipRoutePlanAlarm;
import com.zkthinke.modules.apsat.ship.domain.ShipRoutePlanDanger;
import com.zkthinke.modules.apsat.ship.domain.ShipWeatherDTO;
import com.zkthinke.modules.apsat.ship.service.dto.ShipRoutePlanAlarmQueryCriteria;
import com.zkthinke.modules.apsat.ship.service.dto.ShipRoutePlanDetailDTO;
import com.zkthinke.modules.apsat.ship.service.dto.ShipRoutePlanEnclosureDTO;
import com.zkthinke.modules.apsat.ship.service.dto.ShipRoutePlanEnclosureQueryCriteria;
import com.zkthinke.response.ResponseResult;
import com.zkthinke.utils.PageParam;

import java.util.List;
import java.util.Map;

/**
 * @auther: SONGXF
 * @date: 2021/3/25 16:32
 */
public interface ShipRoutePlanEnclosureService {


    List<ShipRoutePlanEnclosureDTO> findAll(ShipRoutePlanEnclosureQueryCriteria criteria);

    List<ShipRoutePlanDetailDTO> findAllDetail(ShipRoutePlanEnclosureQueryCriteria criteria);

    List<ShipRoutePlanDetailDTO> getPlanDetailByPlanId(String planId);

    Map<String, Object> findAlarmByParam(PageParam<ShipRoutePlanAlarmQueryCriteria> param, List<Long> shipIds);

    List<ShipRoutePlanAlarm> findAlarmLimit(String shipId);

    void isInEnclosure(String shipId, Double longitide, Double latitude, Long timeStamp);

    /**
     * 日志解析时调用预警日志添加接口
     *
     * @param shipId
     * @param revolutionSpeed
     * @param groundSpeed
     */
    void insertAlarmForNoPower(String shipId, Double revolutionSpeed, Double groundSpeed, Long timeStamp, Double longitide, Double latitude);

    void insertAlarmForChangeTarget(String shipId, Long timeStamp);

    List<ShipRoutePlanDanger> findAllDanger(String shipId);

    /**
     * 根据经纬度获取水深,再根据传的最大吃水深度判断是否有触礁危险
     *
     * @param shipId
     * @param longitide
     * @param latitude
     * @param shipDeep
     * @param timeStamp
     */
    void isHitTheRock(String shipId, Double longitide, Double latitude, Double shipDeep, Long timeStamp);

    void anchorWalkingWarning(String shipId, String sailingStatus, Long collectTime, String longitude, String latitude);

    /**
     * 距离暗礁岛屿过近
     */
    void isCloseToReef(String longitude, String latitude, String shipId, Long collectTime);

    /**
     * 距离沉船过近
     */
    void isCloseToShipwrecks(String longitude, String latitude, String shipId, Long collectTime);

    /**
     * 台风预警 靠近台风，沉船风险告警
     */
    void isCloseToTyphoon(String longitude, String latitude, String toString, Long collectTime);

    /**
     * 水深仪测得水深不住3米，搁浅告警
     */
    void isStranding(String shipId, String sensorDepth, Long collectTime);

    /**
     * 人工围栏预警
     */
    void isInArtificialFence(String longitude, String latitude, String shipId, Long collectTime);

    /**
     * 获取气象信息
     *
     * @param shipWeatherDto 获取气象信息数据传输对象
     * @return 统一视图对象
     */
    ResponseResult getWeather(ShipWeatherDTO shipWeatherDto);
}
