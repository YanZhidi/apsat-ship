package com.zkthinke.modules.apsat.ship.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.persistence.*;
import java.io.Serializable;

/**
* @author weicb
* @date 2020-10-15
*/
@Entity
@Data
@Table(name="t_ship_detail")
@ApiModel(value="船舶详细信息")
public class ShipDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(notes = "id")
    private Long id;

    // 船舶 id
    @Column(name = "ship_id",nullable = false)
    @ApiModelProperty(notes = "船舶 id")
    private Long shipId;

    // 航行状态
    @Column(name = "sailing_status")
    @ApiModelProperty(notes = "航行状态")
    private String sailingStatus;

    // 复位对地航程(当前航程)
    @Column(name = "reset_voyage")
    @ApiModelProperty(notes = "复位对地航程(当前航程)")
    private String resetVoyage;

    // 累计对地航程(累计航程)
    @Column(name = "total_voyage")
    @ApiModelProperty(notes = "累计对地航程(累计航程)")
    private String totalVoyage;

    // 转向速度
    @Column(name = "steering_speed")
    @ApiModelProperty(notes = "转向速度")
    private String steeringSpeed;

    // 对地航速
    @Column(name = "ground_speed")
    @ApiModelProperty(notes = "对地航速")
    private String groundSpeed;

    // 当前经度
    @Column(name = "longitude")
    @ApiModelProperty(notes = "当前经度")
    private String longitude;

    // 当前纬度
    @Column(name = "latitude")
    @ApiModelProperty(notes = "当前纬度")
    private String latitude;

    // 对地航向
    @Column(name = "cog")
    @ApiModelProperty(notes = "对地航向")
    private String cog;

    // 船首向
    @Column(name = "ship_head")
    @ApiModelProperty(notes = "船首向")
    private String shipHead;

    // 估计到达时间
    @Column(name = "eta")
    @ApiModelProperty(notes = "估计到达时间")
    private Long eta;

    // 出发时间
    @Column(name = "departure_time")
    @ApiModelProperty(notes = "出发时间")
    private Long departureTime;

    // 目的地
    @Column(name = "destination")
    @ApiModelProperty(notes = "目的地")
    private String destination;

    // 最大静态吃水
    @Column(name = "max_static_draft")
    @ApiModelProperty(notes = "最大静态吃水")
    private String maxStaticDraft;

    // 风速
    @Column(name = "wind_speed")
    @ApiModelProperty(notes = "风速")
    private String windSpeed;

    // 富裕水深
    @Column(name = "sensor_depth")
    @ApiModelProperty(notes = "富裕水深")
    private String sensorDepth;

    // 相对风向
    @Column(name = "relative_wind")
    @ApiModelProperty(notes = "相对风向")
    private String relativeWind;

    // 设备名称
    @Column(name = "device_name")
    @ApiModelProperty(notes = "设备名称")
    private String deviceName;

    // 创建时间
    @Column(name = "create_time")
    private Long createTime;

    // 更新时间
    @Column(name = "update_time")
    private Long updateTime;

    // 数据采集时间
    @ApiModelProperty(notes = "数据采集时间")
    @Column(name = "collect_time")
    private Long collectTime;

    // 出发地
    @ApiModelProperty(notes = "出发地")
    @Column(name = "departure")
    private String departure;

    // 对应同步数据 id
    @ApiModelProperty(notes = "对应同步数据 id")
    @Column(name = "source_id")
    private Long sourceId;

    public void copy(ShipDetail source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}