package com.zkthinke.modules.apsat.ship.domain;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @packageName：com.apstar.bireport.po
 * @className:DeviceInformationPO
 * @description:
 * @author:yeyadan
 * @date:2020/9/17 17:52
 */
@Data
@ToString
public class DeviceInformationPO {
    private Long id;

    private String imoNumber;

    private String name;

    private String deviceName;

    private String revolutionSpeed;

    private String hostLoad;

    private String runningHours;

    private String sindSpeed;

    private String superchargerSpeed;

    private String mefc;

    private String meloip;

    private String meloot;

    private String meloit;

    private String sloip;

    private String sloot;

    private String fip;

    private String fit;

    private String pcoip;

    private String cloit;

    private String mejip;

    private String mejit;

    private String startingAirPressure;

    private String siep;

    private String siet;

    private String controlAirPressure;

    private String smp;

    private String smt;

    private String tbbt;

    private String meav;

    private String sae1;

    private String pog1;

    private String ats1;

    private String afp1;

    private String aft1;

    private String alop1;

    private String alot1;

    private String sae2;

    private String pog2;

    private String ats2;

    private String afp2;

    private String aft2;

    private String alop2;

    private String alot2;

    private String sae3;

    private String pog3;

    private String ats3;

    private String afp3;

    private String aft3;

    private String alop3;

    private String alot3;

    private String boilerSteamPressure;

    private String boilerWaterLevel;

    private String bot;

    private String sternDraught;

    private String portDraft;

    private String starboardDraft;

    private String stemDraft;

    private String trim;

    private String heel;

    private String mefif;

    private String mefof;

    private String gfif;

    private String gfof;

    private String bfoif;

    private String bfoof;

    private String soet;//增压器出口排气温度
    private String sefif;//辅机燃油进口流量
    private String sefof;//辅机燃油出口流量
    private String runstatus1;//一号机运行状态
    private String runstatus2;//二号机运行状态
    private String runstatus3;//三号机运行状态

    private String airReceiverTemp;//主机扫气储气罐温度
    private String airMainFoldPress;//主机扫气集管压力
    private String shaftBearingTempFore;//艉部前轴承温度
    private String shaftBearingTempAft;//艉部后轴承温度
    private String shaftBearingTempInter;//中间轴承温度
    private String coolSeaWPress;//冷却海水温度
    private String thrustBearingTemp;//推力轴承温度
    private String oilBunkerTempL;//左燃油舱温度
    private String oilBunkerTempR;//右燃油舱温度
    private String oilBunkerLevL;//左燃油舱液位
    private String oilBunkerLevR;//右燃油舱液位
    private String dieselFuelTankLev;//柴油舱液位
    private String generatorRunning1;//1号辅机运行
    private String generatorRunning2;//2号辅机运行
    private String generatorRunning3;//3号辅机运行
    private Date dataSyncTime;

}