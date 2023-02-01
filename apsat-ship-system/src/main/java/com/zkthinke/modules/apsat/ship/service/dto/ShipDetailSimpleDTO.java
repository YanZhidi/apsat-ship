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
* @date 2020-10-15
*/
@Data
@ApiModel(value="船舶详细信息简单数据,供能效管理查询使用")
public class ShipDetailSimpleDTO implements Serializable {
    private Long id;

    // 船舶 id
    @ApiModelProperty(notes = "船舶 id")
    private Long shipId;

    // 对地航速
    @ApiModelProperty(notes = "对地航速")
    private String groundSpeed;

    // 数据采集时间
    @ApiModelProperty(notes = "数据采集时间")
    private Long collectTime;

}