package com.zkthinke.modules.apsat.ship.device.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SmartCabinStatusPO {

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

    @ApiModelProperty(notes = "主机燃油进口压力")
    private String fip;
    @ApiModelProperty(notes = "主机燃油进口温度")
    private String fit;
    @ApiModelProperty(notes = "主机滑油进口压力")
    private String meloip;
    @ApiModelProperty(notes = "主机滑油进口温度")
    private String meloit;
    @ApiModelProperty(notes = "主机启动空气压力")
    private String startingAirPressure;
    @ApiModelProperty(notes = "主机控制空气压力")
    private String controlAirPressure;
    @ApiModelProperty(notes = "主机扫气集管空气压力")
    private String airMainFoldPress;

    @ApiModelProperty(notes = "增压器滑油出口温度")
    private String sloot;
    @ApiModelProperty(notes = "增压器出口排气温度")
    private String soet;
    @ApiModelProperty(notes = "增压器进口排气温度")
    private String siet;

    @ApiModelProperty(notes = "中间轴承温度")
    private String shaftBearingTempInter;
    @ApiModelProperty(notes = "推力块轴承温度")
    private String thrustBearingTemp;
    @ApiModelProperty(notes = "艉部前轴承温度")
    private String shaftBearingTempFore;
    @ApiModelProperty(notes = "艉部后轴承温度")
    private String shaftBearingTempAft;

    @ApiModelProperty(notes = "1号辅机排气出口温度")
    private String gasOutletTemp1;
    @ApiModelProperty(notes = "2号辅机排气出口温度")
    private String gasOutletTemp2;
    @ApiModelProperty(notes = "3号辅机排气出口温度")
    private String gasOutletTemp3;

    @ApiModelProperty(notes = "1号辅机滑油压力")
    private String alop1;
    @ApiModelProperty(notes = "2号辅机滑油温度")
    private String alop2;
    @ApiModelProperty(notes = "3号辅机滑油压力")
    private String alop3;

    @ApiModelProperty(notes = "1号辅机滑油温度")
    private String alot1;
    @ApiModelProperty(notes = "2号辅机滑油压力")
    private String alot2;
    @ApiModelProperty(notes = "3号辅机滑油温度")
    private String alot3;

    @ApiModelProperty(notes = "数据采集时间")
    private Long collectTime;
}