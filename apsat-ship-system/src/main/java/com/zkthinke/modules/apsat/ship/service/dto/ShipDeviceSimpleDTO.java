package com.zkthinke.modules.apsat.ship.service.dto;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
* @author weicb
* @date 2020-10-28
*/
@Data
@ApiModel(value="船舶设备(能效)信息简单字段,供能效管理查询使用")
public class ShipDeviceSimpleDTO implements Serializable {

    private Long id;

    // 船舶 id
    @ApiModelProperty(notes = "船舶 id")
    private Long shipId;

    // 主机负荷
    @ApiModelProperty(notes = "主机负荷")
    private String hostLoad;

    // 主机燃油刻度
    @ApiModelProperty(notes = "主机燃油刻度")
    private String mefc;

    // 主机燃油进口流量
    @ApiModelProperty(notes = "主机燃油进口流量")
    private String mefif;

    // 主机燃油出口流量
    @ApiModelProperty(notes = "主机燃油出口流量")
    private String mefof;

    // 辅机燃油进口流量
    @ApiModelProperty(notes = "辅机燃油进口流量")
    private String sefif;

    // 辅机燃油出口流量
    @ApiModelProperty(notes = "辅机燃油出口流量")
    private String sefof;

    // 锅炉燃油进口流量
    @ApiModelProperty(notes = "锅炉燃油进口流量")
    private String bfoif;

    // 锅炉燃油出口流量
    @ApiModelProperty(notes = "锅炉燃油出口流量")
    private String bfoof;

    // 数据采集时间
    @ApiModelProperty(notes = "数据采集时间")
    private Long collectTime;

}