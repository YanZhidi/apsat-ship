package com.zkthinke.modules.apsat.ship.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


/**
 * @author weicb
 * @date 2020-10-15
 */
@Data
@ApiModel(value = "航行偏轨预警")
public class ShipRoutePlanAlarmDTO implements Serializable {

    @ApiModelProperty(notes = "船舶id")
    private String shipId;

    @ApiModelProperty(notes = "船舶计划轨迹id")
    private String planId;

    @ApiModelProperty(notes = "预警信息发生时间")
    private String occurrenceTime;

    @ApiModelProperty(notes = "预警信息")
    private String description;

    @ApiModelProperty(notes = "预警类型")
    //0:偏航 1:恢复航线 2.目的地变更 3.失去动力
    private String alarmType;

    private static final long serialVersionUID = 1L;
}