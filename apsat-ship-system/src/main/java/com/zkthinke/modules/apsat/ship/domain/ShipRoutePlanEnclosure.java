package com.zkthinke.modules.apsat.ship.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @auther SONGXF
 * @date 2021/3/25 16:39
 */
@Data
@ApiModel(value = "船舶计划轨迹电子围栏")
public class ShipRoutePlanEnclosure {

    @ApiModelProperty(notes = "船舶计划轨迹id")
    private Long id;

    @ApiModelProperty(notes = "船舶id")
    private String shipId;

    @ApiModelProperty(notes = "序号")
    private String orderNum;

    @ApiModelProperty(notes = "起点左侧经度")
    private String beginLeftLongitude;

    @ApiModelProperty(notes = "起点左侧纬度")
    private String beginLeftLatitude;

    @ApiModelProperty(notes = "起点右侧经度")
    private String beginRightLongitude;

    @ApiModelProperty(notes = "起点右侧纬度")
    private String beginRightLatitude;

    @ApiModelProperty(notes = "终点左侧经度")
    private String endLeftLongitude;

    @ApiModelProperty(notes = "终点左侧纬度")
    private String endLeftLatitude;

    @ApiModelProperty(notes = "终点右侧经度")
    private String endRightLongitude;

    @ApiModelProperty(notes = "终点右侧纬度")
    private String endRightLatitude;

    @ApiModelProperty(notes = "真航向")
    private String trueCourse;
}
