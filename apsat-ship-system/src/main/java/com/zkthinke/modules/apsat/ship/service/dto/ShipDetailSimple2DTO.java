package com.zkthinke.modules.apsat.ship.service.dto;

import com.zkthinke.modules.apsat.ship.domain.ShipDetail;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
* @author weicb
* @date 2020-10-15
*/
@Data
@ApiModel(value="船舶详细信息简单数据,供能效管理查询使用")
public class ShipDetailSimple2DTO implements Serializable {

    // 航行状态
    @ApiModelProperty(notes = "航行状态")
    private String sailingStatus;

    // 当前经度
    @ApiModelProperty(notes = "当前经度")
    private String longitude;

    // 当前纬度
    @ApiModelProperty(notes = "当前纬度")
    private String latitude;

    // 数据采集时间
    @ApiModelProperty(notes = "数据采集时间")
    private Long collectTime;

    @ApiModelProperty(notes = "水深")
    private String sensorDepth;

    @ApiModelProperty(notes = "航速")
    private String groundSpeed;

    public ShipDetailSimple2DTO() {
    }

    public ShipDetailSimple2DTO(ShipDetail shipDetail) {
        this.sailingStatus = shipDetail.getSailingStatus();
        this.longitude= shipDetail.getLongitude();
        this.latitude = shipDetail.getLatitude();
        this.groundSpeed = shipDetail.getGroundSpeed();
        this.sensorDepth = shipDetail.getSensorDepth();
        this.collectTime = shipDetail.getCollectTime();
    }
}