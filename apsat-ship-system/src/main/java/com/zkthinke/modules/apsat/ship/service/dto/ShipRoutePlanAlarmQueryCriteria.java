package com.zkthinke.modules.apsat.ship.service.dto;

import lombok.Data;

import java.util.List;

/**
 * @auther: SONGXF
 * @date: 2021/3/26 13:33
 */
@Data
public class ShipRoutePlanAlarmQueryCriteria {

    private List<String> shipIdList;

    private List<String> alarmTypeList;

    private String planId;

    private String startTime;

    private String endTime;

}