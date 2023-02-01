package com.zkthinke.modules.apsat.ship.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * @author weicb
 * @date 2020-10-15
 */
@Data
@ApiModel(value = "船舶计划轨迹管理")
public class ShipRoutePlanDTO implements Serializable {

    @ApiModelProperty(notes = "船舶计划轨迹id")
    private Long id;

    // 航次
    @ApiModelProperty(notes = "航次")
    private String voyageNumber;

    // 航段
    @ApiModelProperty(notes = "船名")
    private String voyageName;

    // 航段
    @ApiModelProperty(notes = "计划名称")
    private String planName;

    // 航次起始港
    @ApiModelProperty(notes = "航次起始港")
    private String legBegin;

    // 航次终结港
    @ApiModelProperty(notes = "航次终结港")
    private String legEnd;

    // 预离泊时间(ETD)
    @ApiModelProperty(notes = "预离泊时间(ETD)")
    private String etd;

    // 预抵泊时间(ETA)
    @ApiModelProperty(notes = "预抵泊时间(ETA)")
    private String eta;

    //计划状态 0：禁用 1；启用
    @ApiModelProperty(notes = "计划状态")
    private String state;

    // 创建时间
    @ApiModelProperty(notes = "创建时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(notes = "更新时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(notes = "文件下载地址")
    private String url;
}