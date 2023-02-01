package com.zkthinke.modules.apsat.ship.device.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class HostDetailsBO {
    @ApiModelProperty(notes = "主机启动空气压力")
    private String startingAirPressure;
    @ApiModelProperty(notes = "主机控制空气压力")
    private String controlAirPressure;
    @ApiModelProperty(notes = "主机扫气集管压力")
    private String airMainFoldPress;
    @ApiModelProperty(notes = "主机扫气集管温度")
    private String smt;
    @ApiModelProperty(notes = "主机燃油刻度")
    private String mefc;
    @ApiModelProperty(notes = "主机燃油进口温度")
    private String fit;
    @ApiModelProperty(notes = "主机滑油进口温度")
    private String meloit;
    @ApiModelProperty(notes = "缸套冷却水进口温度")
    private String mejit;
    @ApiModelProperty(notes = "主机燃油进口压力")
    private String fip;
    @ApiModelProperty(notes = "主机滑油进口压力")
    private String meloip;
    @ApiModelProperty(notes = "缸套冷却水进口压力")
    private String mejip;
    @ApiModelProperty(notes = "冷却海水压力")
    private String coolSeaWPress;
    @ApiModelProperty(notes = "增压器进口排气温度")
    private String siet;
    @ApiModelProperty(notes = "增压器出口排气温度")
    private String soet;
    @ApiModelProperty(notes = "增压器滑油出口温度")
    private String sloot;
    @ApiModelProperty(notes = "推力块轴承温度")
    private String thrustBearingTemp;
    @ApiModelProperty(notes = "中间轴承温度")
    private String shaftBearingTempInter;
    @ApiModelProperty(notes = "艉部前轴承温度")
    private String shaftBearingTempFore;
    @ApiModelProperty(notes = "艉部后轴承温度")
    private String shaftBearingTempAft;
    @ApiModelProperty(notes = "主机轴功率")
    private String pstPower;
    @ApiModelProperty(notes = "主机转速")
    private String revolutionSpeed;

}
