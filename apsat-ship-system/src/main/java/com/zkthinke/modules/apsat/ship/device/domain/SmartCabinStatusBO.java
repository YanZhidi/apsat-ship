package com.zkthinke.modules.apsat.ship.device.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SmartCabinStatusBO {

    @ApiModelProperty(notes = "主机转速")
    private String revolutionSpeed;
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
    @ApiModelProperty(notes = "2号辅机滑油压力")
    private String alop2;
    @ApiModelProperty(notes = "3号辅机滑油压力")
    private String alop3;

    @ApiModelProperty(notes = "1号辅机滑油温度")
    private String alot1;
    @ApiModelProperty(notes = "2号辅机滑油温度")
    private String alot2;
    @ApiModelProperty(notes = "3号辅机滑油温度")
    private String alot3;

    @ApiModelProperty(notes = "数据采集时间")
    private Long collectTime;

    public SmartCabinStatusBO() {
    }

    public SmartCabinStatusBO(SmartCabinStatusPO po) {
        this.revolutionSpeed = po.getRevolutionSpeed();
        this.fip = po.getFip();
        this.fit = po.getFit();
        this.meloip = po.getMeloip();
        this.meloit = po.getMeloit();
        this.startingAirPressure = po.getStartingAirPressure();
        this.controlAirPressure = po.getControlAirPressure();
        this.airMainFoldPress = po.getAirMainFoldPress();
        this.sloot = po.getSloot();
        this.soet = po.getSoet();
        this.siet = po.getSiet();
        this.shaftBearingTempInter = po.getShaftBearingTempInter();
        this.thrustBearingTemp = po.getThrustBearingTemp();
        this.shaftBearingTempFore = po.getShaftBearingTempFore();
        this.shaftBearingTempAft = po.getShaftBearingTempAft();
        this.gasOutletTemp1 = po.getGasOutletTemp1();
        this.gasOutletTemp2 = po.getGasOutletTemp2();
        this.gasOutletTemp3 = po.getGasOutletTemp3();
        this.alop1 = po.getAlop1();
        this.alop2 = po.getAlop2();
        this.alop3 = po.getAlop3();
        this.alot1 = po.getAlot1();
        this.alot2 = po.getAlot2();
        this.alot3 = po.getAlot3();
        this.collectTime = po.getCollectTime();
    }
}
