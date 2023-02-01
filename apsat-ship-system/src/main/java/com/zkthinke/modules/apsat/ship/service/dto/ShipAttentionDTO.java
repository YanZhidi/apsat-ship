package com.zkthinke.modules.apsat.ship.service.dto;

import com.zkthinke.modules.apsat.ship.domain.Ship;
import lombok.Data;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.OneToMany;


/**
* @author weicb
* @date 2020-11-01
*/
@Data
@ApiModel(value="ShipAttention")
public class ShipAttentionDTO implements Serializable {

    private Long id;

    // 船舶 id
    @ApiModelProperty(notes = "船舶 id")
    private Long shipId;

    // 用户 id
    @ApiModelProperty(notes = "用户 id")
    private Long userId;

    // 是否关注 0 否 1 是
    @ApiModelProperty(notes = "是否关注 0 否 1 是")
    private Integer attention;

    // 关注时间
    @ApiModelProperty(notes = "关注时间")
    private Long attentionTime;

    // 创建时间
    @ApiModelProperty(notes = "创建时间")
    private Long createTime;

    // 更新时间
    @ApiModelProperty(notes = "更新时间")
    private Long updateTime;

    // 船舶 id
    @ApiModelProperty(notes = "船舶 id")
    private Ship ship;
}