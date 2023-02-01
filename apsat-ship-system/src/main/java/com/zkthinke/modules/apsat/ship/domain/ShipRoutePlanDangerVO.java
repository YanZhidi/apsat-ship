package com.zkthinke.modules.apsat.ship.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "计划轨迹危险区域VO")
public class ShipRoutePlanDangerVO implements Serializable {

    @ApiModelProperty(notes = "经度")
    private String longitude;

    @ApiModelProperty(notes = "纬度")
    private String latitude;

    @ApiModelProperty(notes = "危险等级")
    private String level;

    private static final long serialVersionUID = 1L;
}