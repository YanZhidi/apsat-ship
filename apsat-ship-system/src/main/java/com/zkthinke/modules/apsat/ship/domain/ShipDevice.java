package com.zkthinke.modules.apsat.ship.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
* @author weicb
* @date 2020-10-28
*/
@Entity
@Data
@Table(name="t_ship_device")
@ApiModel(value="船舶设备(能效)信息")
public class ShipDevice implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 船舶 id
    @ApiModelProperty(notes = "船舶 id")
    @Column(name = "ship_id",nullable = false)
    private Long shipId;

    // 主机转速
    @ApiModelProperty(notes = "主机转速")
    @Column(name = "revolution_speed")
    private String revolutionSpeed;

    // 主机负荷
    @ApiModelProperty(notes = "主机负荷")
    @Column(name = "host_load")
    private String hostLoad;

    // 主机运行小时
    @ApiModelProperty(notes = "主机运行小时")
    @Column(name = "running_hours")
    private String runningHours;

    // 增压器转速
    @ApiModelProperty(notes = "增压器转速")
    @Column(name = "supercharger_speed")
    private String superchargerSpeed;

    // 起动空气压力
    @ApiModelProperty(notes = "起动空气压力")
    @Column(name = "starting_air_pressure")
    private String startingAirPressure;

    // 推力块轴承温度
    @ApiModelProperty(notes = "推力块轴承温度")
    @Column(name = "tbbt")
    private String tbbt;

    // 主机轴向振动
    @ApiModelProperty(notes = "主机轴向振动")
    @Column(name = "meav")
    private String meav;

    // 主机滑油进口压力
    @ApiModelProperty(notes = "主机滑油进口压力")
    @Column(name = "meloip")
    private String meloip;

    // 主机燃油刻度
    @ApiModelProperty(notes = "主机燃油刻度")
    @Column(name = "mefc")
    private String mefc;

    // 主机滑油出口温度
    @ApiModelProperty(notes = "主机滑油出口温度")
    @Column(name = "meloot")
    private String meloot;

    // 主机滑油进口温度
    @ApiModelProperty(notes = "主机滑油进口温度")
    @Column(name = "meloit")
    private String meloit;

    // 增压器滑油进口压力
    @ApiModelProperty(notes = "增压器滑油进口压力")
    @Column(name = "sloip")
    private String sloip;

    // 增压器滑油出口温度
    @ApiModelProperty(notes = "增压器滑油出口温度")
    @Column(name = "sloot")
    private String sloot;

    // 主机燃油进口压力
    @ApiModelProperty(notes = "主机燃油进口压力")
    @Column(name = "fip")
    private String fip;

    // 主机燃油进口温度
    @ApiModelProperty(notes = "主机燃油进口温度")
    @Column(name = "fit")
    private String fit;

    // 活塞冷却油进口压力
    @ApiModelProperty(notes = "活塞冷却油进口压力")
    @Column(name = "pcoip")
    private String pcoip;

    // 汽缸滑油进口温度
    @ApiModelProperty(notes = "汽缸滑油进口温度")
    @Column(name = "cloit")
    private String cloit;

    // 主机缸套冷却水进口压力
    @ApiModelProperty(notes = "主机缸套冷却水进口压力")
    @Column(name = "mejip")
    private String mejip;

    // 主机缸套冷却水进口温度
    @ApiModelProperty(notes = "主机缸套冷却水进口温度")
    @Column(name = "mejit")
    private String mejit;

    // 暂时弃用
    @ApiModelProperty(notes = "暂时弃用")
    @Column(name = "siep")
    private String siep;

    // 增压器进口排气温度
    @ApiModelProperty(notes = "增压器进口排气温度")
    @Column(name = "siet")
    private String siet;

    // 增压器出口排气温度
    @ApiModelProperty(notes = "增压器出口排气温度")
    @Column(name = "soet")
    private String soet;

    // 控制空气压力
    @ApiModelProperty(notes = "控制空气压力")
    @Column(name = "control_air_pressure")
    private String controlAirPressure;

    // 扫气集管压力
    @ApiModelProperty(notes = "扫气集管压力")
    @Column(name = "smp")
    private String smp;

    // 扫气集管温度
    @ApiModelProperty(notes = "扫气集管温度")
    @Column(name = "smt")
    private String smt;

    // 一号机运行状态
    @ApiModelProperty(notes = "一号机运行状态")
    @Column(name = "runstatus1")
    private String runstatus1;

    // 1号辅机转速
    @ApiModelProperty(notes = "1号辅机转速")
    @Column(name = "sae1")
    private String sae1;

    // 1号发电机功率
    @ApiModelProperty(notes = "1号发电机功率")
    @Column(name = "pog1")
    private String pog1;

    // 1号辅机增压器转速
    @ApiModelProperty(notes = "1号辅机增压器转速")
    @Column(name = "ats1")
    private String ats1;

    // 1号辅机滑油压力
    @ApiModelProperty(notes = "1号辅机滑油压力")
    @Column(name = "alop1")
    private String alop1;

    // 1号辅机滑油温度
    @ApiModelProperty(notes = "1号辅机滑油温度")
    @Column(name = "alot1")
    private String alot1;

    // 1号辅机燃油压力
    @ApiModelProperty(notes = "1号辅机燃油压力")
    @Column(name = "afp1")
    private String afp1;

    // 1号辅机燃油温度
    @ApiModelProperty(notes = "1号辅机燃油温度")
    @Column(name = "aft1")
    private String aft1;

    // 二号机运行状态
    @ApiModelProperty(notes = "二号机运行状态")
    @Column(name = "runstatus2")
    private String runstatus2;

    // 2号辅机转速
    @ApiModelProperty(notes = "2号辅机转速")
    @Column(name = "sae2")
    private String sae2;

    // 2号发电机功率
    @ApiModelProperty(notes = "2号发电机功率")
    @Column(name = "pog2")
    private String pog2;

    // 2号辅机增压器转速
    @ApiModelProperty(notes = "2号辅机增压器转速")
    @Column(name = "ats2")
    private String ats2;

    // 2号辅机滑油压力
    @ApiModelProperty(notes = "2号辅机滑油压力")
    @Column(name = "alop2")
    private String alop2;

    // 2号辅机滑油温度
    @ApiModelProperty(notes = "2号辅机滑油温度")
    @Column(name = "alot2")
    private String alot2;

    // 2号辅机燃油压力
    @ApiModelProperty(notes = "2号辅机燃油压力")
    @Column(name = "afp2")
    private String afp2;

    // 2号辅机燃油温度
    @ApiModelProperty(notes = "2号辅机燃油温度")
    @Column(name = "aft2")
    private String aft2;

    // 三号机运行状态
    @ApiModelProperty(notes = "三号机运行状态")
    @Column(name = "runstatus3")
    private String runstatus3;

    // 3号辅机转速
    @ApiModelProperty(notes = "3号辅机转速")
    @Column(name = "sae3")
    private String sae3;

    // 3号发电机功率
    @ApiModelProperty(notes = "3号发电机功率")
    @Column(name = "pog3")
    private String pog3;

    // 3号辅机增压器转速
    @ApiModelProperty(notes = "3号辅机增压器转速")
    @Column(name = "ats3")
    private String ats3;

    // 3号辅机滑油压力
    @ApiModelProperty(notes = "3号辅机滑油压力")
    @Column(name = "alop3")
    private String alop3;

    // 3号辅机滑油温度
    @ApiModelProperty(notes = "3号辅机滑油温度")
    @Column(name = "alot3")
    private String alot3;

    // 3号辅机燃油压力
    @ApiModelProperty(notes = "3号辅机燃油压力")
    @Column(name = "afp3")
    private String afp3;

    // 3号辅机燃油温度
    @ApiModelProperty(notes = "3号辅机燃油温度")
    @Column(name = "aft3")
    private String aft3;

    // 锅炉蒸汽压力
    @ApiModelProperty(notes = "锅炉蒸汽压力")
    @Column(name = "boiler_steam_pressure")
    private String boilerSteamPressure;

    // 锅炉水位
    @ApiModelProperty(notes = "锅炉水位")
    @Column(name = "boiler_water_level")
    private String boilerWaterLevel;

    // 锅炉燃油温度
    @ApiModelProperty(notes = "锅炉燃油温度")
    @Column(name = "bot")
    private String bot;

    // 艉部吃水
    @ApiModelProperty(notes = "艉部吃水")
    @Column(name = "stern_draught")
    private String sternDraught;

    // 艏部吃水
    @ApiModelProperty(notes = "艏部吃水")
    @Column(name = "stem_draft")
    private String stemDraft;

    // 右舷吃水
    @ApiModelProperty(notes = "右舷吃水")
    @Column(name = "starboard_draft")
    private String starboardDraft;

    // 左舷吃水
    @ApiModelProperty(notes = "左舷吃水")
    @Column(name = "port_draft")
    private String portDraft;

    // 纵倾
    @ApiModelProperty(notes = "纵倾")
    @Column(name = "trim")
    private String trim;

    // 横倾
    @ApiModelProperty(notes = "横倾")
    @Column(name = "heel")
    private String heel;

    // 暂时弃用
    @ApiModelProperty(notes = "暂时弃用")
    @Column(name = "mefit")
    private String mefit;

    // 主机燃油进口流量
    @ApiModelProperty(notes = "主机燃油进口流量")
    @Column(name = "mefif")
    private String mefif;

    // 主机燃油出口流量
    @ApiModelProperty(notes = "主机燃油出口流量")
    @Column(name = "mefof")
    private String mefof;

    // 辅机燃油进口流量
    @ApiModelProperty(notes = "辅机燃油进口流量")
    @Column(name = "gfif")
    private String gfif;

    // 辅机燃油出口流量
    @ApiModelProperty(notes = "辅机燃油出口流量")
    @Column(name = "gfof")
    private String gfof;

    // 锅炉燃油进口流量
    @ApiModelProperty(notes = "锅炉燃油进口流量")
    @Column(name = "bfoif")
    private String bfoif;

    // 锅炉燃油出口流量
    @ApiModelProperty(notes = "锅炉燃油出口流量")
    @Column(name = "bfoof")
    private String bfoof;

    // 辅机燃油进口流量
    @ApiModelProperty(notes = "辅机燃油进口流量")
    @Column(name = "sefif")
    private String sefif;

    // 辅机燃油出口流量
    @ApiModelProperty(notes = "辅机燃油出口流量")
    @Column(name = "sefof")
    private String sefof;

    // 设备名
    @ApiModelProperty(notes = "设备名")
    @Column(name = "device_name")
    private String deviceName;

    // 对应同步数据 id
    @ApiModelProperty(notes = "对应同步数据 id")
    @Column(name = "source_id")
    private Long sourceId;

    // 创建时间
    @ApiModelProperty(notes = "创建时间")
    @Column(name = "create_time")
    private Long createTime;

    // 更新时间
    @ApiModelProperty(notes = "更新时间")
    @Column(name = "update_time")
    private Long updateTime;

    // 数据采集时间
    @ApiModelProperty(notes = "数据采集时间")
    @Column(name = "collect_time")
    private Long collectTime;

    public void copy(ShipDevice source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}