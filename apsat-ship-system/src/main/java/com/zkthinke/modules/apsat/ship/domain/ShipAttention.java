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
* @date 2020-11-01
*/
@Entity
@Data
@Table(name="t_ship_attention")
@ApiModel(value="t_ship_attention")
public class ShipAttention implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 船舶 id
    @ApiModelProperty(notes = "船舶 id")
    @JoinColumn(name = "ship_id")
    @ManyToOne
    private Ship ship;

    // 用户 id
    @ApiModelProperty(notes = "用户 id")
    @Column(name = "user_id")
    private Long userId;

    // 是否关注 0 否 1 是
    @ApiModelProperty(notes = "是否关注 0 否 1 是")
    @Column(name = "attention")
    private Integer attention;

    // 关注时间
    @ApiModelProperty(notes = "关注时间")
    @Column(name = "attention_time")
    private Long attentionTime;

    // 创建时间
    @ApiModelProperty(notes = "创建时间")
    @Column(name = "create_time")
    private Long createTime;

    // 更新时间
    @ApiModelProperty(notes = "更新时间")
    @Column(name = "update_time")
    private Long updateTime;

    public void copy(ShipAttention source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}