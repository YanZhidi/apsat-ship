package com.zkthinke.modules.apsat.ship.domain;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "航行偏轨预警")
public class ShipRoutePlanAlarm implements Serializable {

    @ApiModelProperty(notes = "航行偏轨预警信息id")
    private Integer id;

    @ApiModelProperty(notes = "船舶id")
    private String shipId;

    @ApiModelProperty(notes = "船舶计划轨迹id")
    private String planId;

    @ApiModelProperty(notes = "预警信息发生时间")
    private String occurrenceTime;

    @ApiModelProperty(notes = "预警信息详情")
    private String description;

    @ApiModelProperty(notes = "预警类型")
    //0:偏航 1:恢复航线 2.目的地变更 3.失去动力
    private String alarmType;

    private static final long serialVersionUID = 1L;
}