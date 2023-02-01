package com.zkthinke.modules.apsat.ship.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;


/**
* @author weicb
* @date 2020-10-15
*/
@Data
@ApiModel(value="船舶详细信息(含航行信息)")
public class ShipDetailDTO implements Serializable {

    private Long id;

    // 船舶 id
    @ApiModelProperty(notes = "船舶 id")
    private Long shipId;

    // 航行状态
    @ApiModelProperty(notes = "航行状态")
    private String sailingStatus;

    // 复位对地航程(当前航程)
    @ApiModelProperty(notes = "复位对地航程(当前航程)")
    private String resetVoyage;

    // 累计对地航程(累计航程)
    @ApiModelProperty(notes = "累计对地航程(累计航程)")
    private String totalVoyage;

    // 转向速度
    @ApiModelProperty(notes = "转向速度")
    private String steeringSpeed;

    // 对地航速
    @ApiModelProperty(notes = "对地航速")
    private String groundSpeed;

    // 当前经度
    @ApiModelProperty(notes = "当前经度")
    private String longitude;

    // 当前纬度
    @ApiModelProperty(notes = "当前纬度")
    private String latitude;

    // 对地航向
    @ApiModelProperty(notes = "对地航向")
    private String cog;

    // 船首向
    @ApiModelProperty(notes = "船首向")
    private String shipHead;

    // 估计到达时间
    @ApiModelProperty(notes = "估计到达时间")
    private Long eta;

    // 出发时间
    @ApiModelProperty(notes = "出发时间")
    private Long departureTime;

    // 目的地
    @ApiModelProperty(notes = "目的地")
    private String destination;

    // 最大静态吃水
    @ApiModelProperty(notes = "最大静态吃水")
    private String maxStaticDraft;

    // 风速
    @ApiModelProperty(notes = "风速")
    private String windSpeed;

    // 富裕水深
    @ApiModelProperty(notes = "富裕水深")
    private String sensorDepth;

    // 相对风向
    @ApiModelProperty(notes = "相对风向")
    private String relativeWind;

    // 设备名称
    @ApiModelProperty(notes = "设备名称")
    private String deviceName;

    // 创建时间
    private Long createTime;

    // 更新时间
    private Long updateTime;

    // 数据采集时间
    @ApiModelProperty(notes = "数据采集时间")
    private Long collectTime;

    // 出发地
    @ApiModelProperty(notes = "出发地")
    private String departure;

    // 对应同步数据 id
    @ApiModelProperty(notes = "对应同步数据 id")
    private Long sourceId;

    // 航行进度百分比
    @ApiModelProperty(notes = "航行进度百分比,0~1 的小数,保留两位")
    private BigDecimal percent;

}