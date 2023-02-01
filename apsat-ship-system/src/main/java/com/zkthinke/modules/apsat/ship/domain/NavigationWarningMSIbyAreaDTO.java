package com.zkthinke.modules.apsat.ship.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


/**
 * 航行警告 3.3.	按区域获取航行警告/航行通告信息数据传输对象
 *
 * @author dww
 * @since 1.0
 */
@Data
@ApiModel(value = "航行警告 3.3. 按区域获取航行警告/航行通告信息数据传输对象")
public class NavigationWarningMSIbyAreaDTO implements Serializable {

    // 区域,WKT格式
    @ApiModelProperty(notes = "区域,WKT格式")
    private String area;

    // 0:全部 1:航行警告 2：航行通告
    @ApiModelProperty(notes = "全部")
    private Integer type;

    // 版本 zho(中文)  eng(英文)
    @ApiModelProperty(notes = "版本 zho(中文)  eng(英文)")
    private String language;
}