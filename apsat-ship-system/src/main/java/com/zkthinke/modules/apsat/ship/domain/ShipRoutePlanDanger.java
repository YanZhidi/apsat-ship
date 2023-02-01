package com.zkthinke.modules.apsat.ship.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "计划轨迹危险区域")
public class ShipRoutePlanDanger implements Serializable {

    @ApiModelProperty(notes = "主键id")
    private Integer id;

    @ApiModelProperty(notes = "船舶id")
    private String shipId;

    @ApiModelProperty(notes = "船舶计划轨迹id")
    private String planId;

    @ApiModelProperty(notes = "区域内船舶数量")
    private String count;

    @ApiModelProperty(notes = "经度A")
    private String longA;

    @ApiModelProperty(notes = "纬度A")
    private String latA;

    @ApiModelProperty(notes = "经度B")
    private String longB;

    @ApiModelProperty(notes = "纬度B")
    private String latB;

    @ApiModelProperty(notes = "经度C")
    private String longC;

    @ApiModelProperty(notes = "纬度C")
    private String latC;

    @ApiModelProperty(notes = "经度D")
    private String longD;

    @ApiModelProperty(notes = "纬度D")
    private String latD;

    private static final long serialVersionUID = 1L;
}