package com.zkthinke.modules.apsat.sync.service.dto;

import lombok.Data;

import javax.persistence.Column;
import java.io.Serializable;


/**
* @author weicb
* @date 2020-10-19
*/
@Data
public class SyncShipDTO implements Serializable {

    private Long id;

    // 船舶 IMO 唯一编号
    private String imoNumber;

    // 船名
    private String name;

    // 设备名
    private String deviceName;

    // 相对风向
    private String rerativeWind;

    // 富裕水深
    private String sensorDepth;

    // 传感器水面距离
    private String swsd;

    // 风速
    private String sindSpeed;

    // 累计对地航程
    private String totalCumulativeGroundDistance;

    // 复位对地航程
    private String groundDistanceSinceReset;

    // MMSI编号
    private String mmsiNumber;

    // 航行状态
    private String navigationalStatus;

    // 出发时间
    private String departuretime;

    // 转向速度
    private String steeringSpeed;

    // 对地航速
    private String groundSpeed;

    // 经度
    private String longitude;

    // 纬度
    private String latitude;

    // 对地航向
    private String cog;

    // 船首向
    private String shipHead;

    // 呼号
    private String callSign;

    // 船舶和货物类型
    private String type;

    // 估计到达时间
    private String eta;

    // 最大静态吃水
    private String maximumStaticDraft;

    // 目的地
    private String destination;

    // 同步时间
    private Long syncTime;

    private String sourceId;
}