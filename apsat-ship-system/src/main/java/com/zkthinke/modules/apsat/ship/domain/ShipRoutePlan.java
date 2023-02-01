package com.zkthinke.modules.apsat.ship.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @auther SONGXF
 * @date 2021/3/25 16:39
 */
@Data
@ApiModel(value = "船舶计划轨迹管理")
public class ShipRoutePlan {

    @ApiModelProperty(notes = "船舶计划轨迹id")
    private Long id;

    // 船舶id
    @ApiModelProperty(notes = "船舶id")
    private String shipId;

    // 航次
    @ApiModelProperty(notes = "航次")
    private String voyageNumber;

    // 航段
    @ApiModelProperty(notes = "船名")
    private String voyageName;

    // 计划名称
    @ApiModelProperty(notes = "计划名称")
    private String planName;

    // 航次起始港
    @ApiModelProperty(notes = "航次起始港")
    private String legBegin;

    // 航次终结港
    @ApiModelProperty(notes = "航次终结港")
    private String legEnd;

    //电子围栏半径
    @ApiModelProperty(notes = "电子围栏半径")
    private String radius;

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
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    // 更新时间
    @ApiModelProperty(notes = "更新时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    // 文件下载地址
    @ApiModelProperty(notes = "文件下载地址")
    private String url;

    // 创建人
    @ApiModelProperty(notes = "创建人")
    private String createUser;

    List<ShipRoutePlanDetail> list;

    public void copy(ShipRoutePlan source) {
        BeanUtil.copyProperties(source, this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
