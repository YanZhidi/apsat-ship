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
* @date 2020-10-15
*/
@Entity
@Data
@Table(name="t_ship_detail")
@ApiModel(value="船舶详细信息简单数据,供能效管理查询使用")
public class ShipDetailSimple implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(notes = "id")
    private Long id;

    // 船舶 id
    @ApiModelProperty(notes = "船舶 id")
    @Column(name = "ship_id",nullable = false)
    private Long shipId;

    // 对地航速
    @Column(name = "ground_speed")
    @ApiModelProperty(notes = "对地航速")
    private String groundSpeed;

    // 数据采集时间
    @ApiModelProperty(notes = "数据采集时间")
    @Column(name = "collect_time")
    private Long collectTime;

    public void copy(ShipDetailSimple source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}