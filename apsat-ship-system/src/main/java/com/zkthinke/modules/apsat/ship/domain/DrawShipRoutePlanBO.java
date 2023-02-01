package com.zkthinke.modules.apsat.ship.domain;

import lombok.Data;

import java.util.List;

@Data
public class DrawShipRoutePlanBO {
    private String shipId;      //船舶Id
    private String planId;      //计划ID

    private String voyageNumber;//航次
    private String planName;    //计划名称
    private String legBegin;    //起始港
    private String legEnd;      //终结港
    private String etd;         //预离泊时间
    private String eta;         //预抵泊时间

    private List<PointBO> pointList;    //轨迹点
}
