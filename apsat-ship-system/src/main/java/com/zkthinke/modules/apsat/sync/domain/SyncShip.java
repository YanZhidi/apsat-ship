package com.zkthinke.modules.apsat.sync.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.persistence.*;
import java.io.Serializable;

/**
* @author weicb
* @date 2020-10-19
*/
@Entity
@Data
@Table(name="t_sync_ship")
public class SyncShip implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 船舶 IMO 唯一编号
    @Column(name = "imo_number",nullable = false)
    private String imoNumber;

    // 船名
    @Column(name = "name")
    private String name;

    // 设备名
    @Column(name = "device_name")
    private String deviceName;

    // 相对风向
    @Column(name = "rerative_wind")
    private String rerativeWind;

    // 富裕水深
    @Column(name = "sensor_depth")
    private String sensorDepth;

    // 传感器水面距离
    @Column(name = "swsd")
    private String swsd;

    // 风速
    @Column(name = "sind_speed")
    private String sindSpeed;

    // 累计对地航程
    @Column(name = "total_cumulative_ground_distance")
    private String totalCumulativeGroundDistance;

    // 复位对地航程
    @Column(name = "ground_distance_since_reset")
    private String groundDistanceSinceReset;

    // MMSI编号
    @Column(name = "mmsi_number")
    private String mmsiNumber;

    // 航行状态
    @Column(name = "navigational_status")
    private String navigationalStatus;

    // 出发时间
    @Column(name = "departure_time")
    private String departuretime;

    // 转向速度
    @Column(name = "steering_speed")
    private String steeringSpeed;

    // 对地航速
    @Column(name = "ground_speed")
    private String groundSpeed;

    // 经度
    @Column(name = "longitude")
    private String longitude;

    // 纬度
    @Column(name = "latitude")
    private String latitude;

    // 对地航向
    @Column(name = "cog")
    private String cog;

    // 船首向
    @Column(name = "ship_head")
    private String shipHead;

    // 呼号
    @Column(name = "call_sign")
    private String callSign;

    // 船舶和货物类型
    @Column(name = "type")
    private String type;

    // 估计到达时间
    @Column(name = "eta")
    private String eta;

    // 最大静态吃水
    @Column(name = "maximum_static_draft")
    private String maximumStaticDraft;

    // 目的地
    @Column(name = "destination")
    private String destination;

    // 同步时间
    @Column(name = "sync_time")
    private Long syncTime;

    // 源 id
    @Column(name = "source_id")
    private String sourceId;

    // 出发地
    @Column(name = "departure")
    private String departure;

    // 接口方数据采集时间
    @Column(name = "data_sync_time")
    private String dataSyncTime;

    public void copy(SyncShip source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}