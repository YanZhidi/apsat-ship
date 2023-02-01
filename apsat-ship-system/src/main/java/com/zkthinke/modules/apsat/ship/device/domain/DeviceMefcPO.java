package com.zkthinke.modules.apsat.ship.device.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 主机、辅机、锅炉、总量
 */

@Data
@ApiModel(value="船舶燃油耗能")
public class DeviceMefcPO implements Serializable {

    @ApiModelProperty(notes = "设备表主键Id")
    private long deviceId;
    @ApiModelProperty(notes = "数据采集时间")
    private String collectTime;
    @ApiModelProperty(notes = "数据类型")
    private String type;
    @ApiModelProperty(notes = "数据值")
    private String value;

    private static final long serialVersionUID = 1L;
}
