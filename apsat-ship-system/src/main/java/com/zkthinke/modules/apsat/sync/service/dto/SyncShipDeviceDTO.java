package com.zkthinke.modules.apsat.sync.service.dto;

import lombok.Data;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
* @author weicb
* @date 2020-10-28
*/
@Data
@ApiModel(value="SyncShipDevice")
public class SyncShipDeviceDTO implements Serializable {

    private Long id;

    // 船舶 IMO 唯一编号
    @ApiModelProperty(notes = "船舶 IMO 唯一编号")
    private String imoNumber;

    // 船名
    @ApiModelProperty(notes = "船名")
    private String name;

    // 主机转速
    @ApiModelProperty(notes = "主机转速")
    private String revolutionSpeed;

    // 主机负荷
    @ApiModelProperty(notes = "主机负荷")
    private String hostLoad;

    // 主机运行小时
    @ApiModelProperty(notes = "主机运行小时")
    private String runningHours;

    // 增压器转速
    @ApiModelProperty(notes = "增压器转速")
    private String superchargerSpeed;

    // 起动空气压力
    @ApiModelProperty(notes = "起动空气压力")
    private String startingAirPressure;

    // 推力块轴承温度
    @ApiModelProperty(notes = "推力块轴承温度")
    private String tbbt;

    // 主机轴向振动
    @ApiModelProperty(notes = "主机轴向振动")
    private String meav;

    // 主机滑油进口压力
    @ApiModelProperty(notes = "主机滑油进口压力")
    private String meloip;

    // 主机燃油刻度
    @ApiModelProperty(notes = "主机燃油刻度")
    private String mefc;

    // 主机滑油出口温度
    @ApiModelProperty(notes = "主机滑油出口温度")
    private String meloot;

    // 主机滑油进口温度
    @ApiModelProperty(notes = "主机滑油进口温度")
    private String meloit;

    // 增压器滑油进口压力
    @ApiModelProperty(notes = "增压器滑油进口压力")
    private String sloip;

    // 增压器滑油出口温度
    @ApiModelProperty(notes = "增压器滑油出口温度")
    private String sloot;

    // 主机燃油进口压力
    @ApiModelProperty(notes = "主机燃油进口压力")
    private String fip;

    // 主机燃油进口温度
    @ApiModelProperty(notes = "主机燃油进口温度")
    private String fit;

    // 活塞冷却油进口压力
    @ApiModelProperty(notes = "活塞冷却油进口压力")
    private String pcoip;

    // 汽缸滑油进口温度
    @ApiModelProperty(notes = "汽缸滑油进口温度")
    private String cloit;

    // 主机缸套冷却水进口压力
    @ApiModelProperty(notes = "主机缸套冷却水进口压力")
    private String mejip;

    // 主机缸套冷却水进口温度
    @ApiModelProperty(notes = "主机缸套冷却水进口温度")
    private String mejit;

    // 增压器出口排气温度
    @ApiModelProperty(notes = "增压器出口排气温度")
    private String siep;

    // 增压器进口排气温度
    @ApiModelProperty(notes = "增压器进口排气温度")
    private String siet;

    // 控制空气压力
    @ApiModelProperty(notes = "控制空气压力")
    private String controlAirPressure;

    // 扫气集管压力
    @ApiModelProperty(notes = "扫气集管压力")
    private String smp;

    // 扫气集管温度
    @ApiModelProperty(notes = "扫气集管温度")
    private String smt;

    // 一号机运行状态
    @ApiModelProperty(notes = "一号机运行状态")
    private String runstatus1;

    // 1号辅机转速
    @ApiModelProperty(notes = "1号辅机转速")
    private String sae1;

    // 1号发电机功率
    @ApiModelProperty(notes = "1号发电机功率")
    private String pog1;

    // 1号辅机增压器转速
    @ApiModelProperty(notes = "1号辅机增压器转速")
    private String ats1;

    // 1号辅机滑油压力
    @ApiModelProperty(notes = "1号辅机滑油压力")
    private String alop1;

    // 1号辅机滑油温度
    @ApiModelProperty(notes = "1号辅机滑油温度")
    private String alot1;

    // 1号辅机燃油压力
    @ApiModelProperty(notes = "1号辅机燃油压力")
    private String afp1;

    // 1号辅机燃油温度
    @ApiModelProperty(notes = "1号辅机燃油温度")
    private String aft1;

    // 二号机运行状态
    @ApiModelProperty(notes = "二号机运行状态")
    private String runstatus2;

    // 2号辅机转速
    @ApiModelProperty(notes = "2号辅机转速")
    private String sae2;

    // 2号发电机功率
    @ApiModelProperty(notes = "2号发电机功率")
    private String pog2;

    // 2号辅机增压器转速
    @ApiModelProperty(notes = "2号辅机增压器转速")
    private String ats2;

    // 2号辅机滑油压力
    @ApiModelProperty(notes = "2号辅机滑油压力")
    private String alop2;

    // 2号辅机滑油温度
    @ApiModelProperty(notes = "2号辅机滑油温度")
    private String alot2;

    // 2号辅机燃油压力
    @ApiModelProperty(notes = "2号辅机燃油压力")
    private String afp2;

    // 2号辅机燃油温度
    @ApiModelProperty(notes = "2号辅机燃油温度")
    private String aft2;

    // 三号机运行状态
    @ApiModelProperty(notes = "三号机运行状态")
    private String runstatus3;

    // 3号辅机转速
    @ApiModelProperty(notes = "3号辅机转速")
    private String sae3;

    // 3号发电机功率
    @ApiModelProperty(notes = "3号发电机功率")
    private String pog3;

    // 3号辅机增压器转速
    @ApiModelProperty(notes = "3号辅机增压器转速")
    private String ats3;

    // 3号辅机滑油压力
    @ApiModelProperty(notes = "3号辅机滑油压力")
    private String alop3;

    // 3号辅机滑油温度
    @ApiModelProperty(notes = "3号辅机滑油温度")
    private String alot3;

    // 3号辅机燃油压力
    @ApiModelProperty(notes = "3号辅机燃油压力")
    private String afp3;

    // 3号辅机燃油温度
    @ApiModelProperty(notes = "3号辅机燃油温度")
    private String aft3;

    // 锅炉蒸汽压力
    @ApiModelProperty(notes = "锅炉蒸汽压力")
    private String boilerSteamPressure;

    // 锅炉水位
    @ApiModelProperty(notes = "锅炉水位")
    private String boilerWaterLevel;

    // 锅炉燃油温度
    @ApiModelProperty(notes = "锅炉燃油温度")
    private String bot;

    // 艉部吃水
    @ApiModelProperty(notes = "艉部吃水")
    private String sternDraught;

    // 艏部吃水
    @ApiModelProperty(notes = "艏部吃水")
    private String stemDraft;

    // 右舷吃水
    @ApiModelProperty(notes = "右舷吃水")
    private String starboardDraft;

    // 左舷吃水
    @ApiModelProperty(notes = "左舷吃水")
    private String portDraft;

    // 纵倾
    @ApiModelProperty(notes = "纵倾")
    private String trim;

    // 横倾
    @ApiModelProperty(notes = "横倾")
    private String heel;

    // 暂时弃用
    @ApiModelProperty(notes = "暂时弃用")
    private String mefit;

    // 主机燃油进口流量
    @ApiModelProperty(notes = "主机燃油进口流量")
    private String mefif;

    // 主机燃油出口流量
    @ApiModelProperty(notes = "主机燃油出口流量")
    private String mefof;

    // 辅机燃油进口流量
    @ApiModelProperty(notes = "辅机燃油进口流量")
    private String gfif;

    // 辅机燃油出口流量
    @ApiModelProperty(notes = "辅机燃油出口流量")
    private String gfof;

    // 锅炉燃油进口流量
    @ApiModelProperty(notes = "锅炉燃油进口流量")
    private String bfoif;

    // 锅炉燃油出口流量
    @ApiModelProperty(notes = "锅炉燃油出口流量")
    private String bfoof;

    // 增压器出口排气温度
    @ApiModelProperty(notes = "增压器出口排气温度")
    private String soet;

    // 辅机燃油进口流量
    @ApiModelProperty(notes = "辅机燃油进口流量")
    private String sefif;

    // 辅机燃油出口流量
    @ApiModelProperty(notes = "辅机燃油出口流量")
    private String sefof;

    // 设备名
    @ApiModelProperty(notes = "设备名")
    private String deviceName;

    // 同步时间
    @ApiModelProperty(notes = "同步时间")
    private Long syncTime;

    // 源 id
    @ApiModelProperty(notes = "源 id")
    private String sourceId;
}