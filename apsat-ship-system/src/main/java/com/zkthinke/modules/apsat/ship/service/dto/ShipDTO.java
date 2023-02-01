package com.zkthinke.modules.apsat.ship.service.dto;

import com.zkthinke.modules.apsat.ship.domain.ShipDetail;
import com.zkthinke.modules.apsat.ship.domain.ShipDevice;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.io.Serializable;


/**
* @author weicb
* @date 2020-10-15
*/
@Data
@ApiModel(value="船舶基本信息")
public class ShipDTO implements Serializable {

    @ApiModelProperty(notes = "船舶 id")
    private Long id;

    // 名称
    @ApiModelProperty(notes = "船舶名称")
    private String name;

    // MMSI编号
    @ApiModelProperty(notes = "MMSI编号")
    private String mmsiNumber;

    // 呼号
    @ApiModelProperty(notes = "呼号")
    private String callSign;

    // IMO,唯一编号
    @ApiModelProperty(notes = "IMO,唯一编号")
    private String imoNumber;

    // 类型
    @ApiModelProperty(notes = "类型")
    private String type;

    // 创建时间
    private Long createTime;

    // 更新时间
    private Long updateTime;

    // 最新航行信息 id
    @ApiModelProperty(notes = "最新航行信息 id")
    private Long lastDetailId;

    // 最新航行信息
    @ApiModelProperty(notes = "最新航行信息")
    private ShipDetailDTO lastShipDetail;

//    // 最新设备信息
//    @ApiModelProperty(notes = "最新设备信息")
//    private ShipDevice lastShipDevice;

    // 拼音名称
    @ApiModelProperty(notes = "拼音名称")
    private String namePinyin;

    // 是否关注 0 否 1 是
    @ApiModelProperty(notes = "是否关注 0 否 1 是")
    private Integer attention;

    // 最新设备同步时间
    @ApiModelProperty(notes = "最新设备同步时间")
    private String lastDeviceStime;

    // 最新航行数据同步时间
    @ApiModelProperty(notes = "最新航行数据同步时间")
    private String lastDetailStime;
}