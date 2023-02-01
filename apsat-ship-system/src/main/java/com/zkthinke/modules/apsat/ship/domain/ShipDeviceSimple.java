package com.zkthinke.modules.apsat.ship.domain;

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
@Entity
@Data
@Table(name="t_ship_device")
@ApiModel(value="船舶设备(能效)信息简单字段,供能效管理查询使用")
public class ShipDeviceSimple implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 船舶 id
    @ApiModelProperty(notes = "船舶 id")
    @Column(name = "ship_id",nullable = false)
    private Long shipId;

    // 主机负荷
    @ApiModelProperty(notes = "主机负荷")
    @Column(name = "host_load")
    private String hostLoad;

    // 主机燃油刻度
    @ApiModelProperty(notes = "主机燃油刻度")
    @Column(name = "mefc")
    private String mefc;

    // 主机燃油进口流量
    @ApiModelProperty(notes = "主机燃油进口流量")
    @Column(name = "mefif")
    private String mefif;

    // 主机燃油出口流量
    @ApiModelProperty(notes = "主机燃油出口流量")
    @Column(name = "mefof")
    private String mefof;

    // 辅机燃油进口流量
    @ApiModelProperty(notes = "辅机燃油进口流量")
    @Column(name = "sefif")
    private String sefif;

    // 辅机燃油出口流量
    @ApiModelProperty(notes = "辅机燃油出口流量")
    @Column(name = "sefof")
    private String sefof;

    // 锅炉燃油进口流量
    @ApiModelProperty(notes = "锅炉燃油进口流量")
    @Column(name = "bfoif")
    private String bfoif;

    // 锅炉燃油出口流量
    @ApiModelProperty(notes = "锅炉燃油出口流量")
    @Column(name = "bfoof")
    private String bfoof;

    // 数据采集时间
    @ApiModelProperty(notes = "数据采集时间")
    @Column(name = "collect_time")
    private Long collectTime;

    public void copy(ShipDeviceSimple source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}