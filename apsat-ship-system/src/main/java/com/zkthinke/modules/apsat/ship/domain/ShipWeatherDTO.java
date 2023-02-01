package com.zkthinke.modules.apsat.ship.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


/**
 * 获取气象信息数据传输对象
 *
 * @author dww
 * @since 1.0
 */
@Data
@ApiModel(value = "获取气象信息数据传输对象")
public class ShipWeatherDTO implements Serializable {

    // 当前日期
    @ApiModelProperty(notes = "当前日期")
    private String time;

    // 经度
    @ApiModelProperty(notes = "经度")
    private String longitude;

    // 纬度
    @ApiModelProperty(notes = "纬度")
    private String latitude;

}