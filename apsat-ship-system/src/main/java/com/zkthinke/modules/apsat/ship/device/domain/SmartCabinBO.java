package com.zkthinke.modules.apsat.ship.device.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SmartCabinBO {

    @ApiModelProperty(notes = "主机燃油刻度")
    private String mefc;

    @ApiModelProperty(notes = "主机转速")
    private String revolutionSpeed;

    @ApiModelProperty(notes = "冷却海水压力")
    private String coolSeaWPress;

    @ApiModelProperty(notes = "缸套冷却水进口压力")
    private String mejip;

    @ApiModelProperty(notes = "缸套冷却水进口温度")
    private String mejit;

    @ApiModelProperty(notes = "1号辅机运行")
    private String generatorRunning1;
    @ApiModelProperty(notes = "2号辅机运行")
    private String generatorRunning2;
    @ApiModelProperty(notes = "3号辅机运行")
    private String generatorRunning3;

    @ApiModelProperty(notes = "智能机舱各折线图状态")
    private List<SmartCabinStatusBO> smartCabinStatusBOList;

}
