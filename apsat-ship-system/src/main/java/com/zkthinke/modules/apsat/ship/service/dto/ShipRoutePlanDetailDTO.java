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
@ApiModel(value = "船舶计划轨迹电子围栏")
public class ShipRoutePlanDetailDTO implements Serializable {

    @ApiModelProperty(notes = "船舶计划轨迹id")
    private Long id;

    @ApiModelProperty(notes = "序号")
    private String orderNum;

    @ApiModelProperty(notes = "经度")
    private String longitude;

    @ApiModelProperty(notes = "纬度")
    private String latitude;

    @ApiModelProperty(notes = "真航向")
    private String trueCourse;

    @ApiModelProperty(notes = "航距")
    private String range;

    @ApiModelProperty(notes = "到港距离")
    private String distanceTodo;

    @ApiModelProperty(notes = "转向点备注")
    private String wpRemark;
}