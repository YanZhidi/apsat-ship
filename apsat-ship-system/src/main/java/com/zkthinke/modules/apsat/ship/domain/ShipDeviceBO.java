package com.zkthinke.modules.apsat.ship.domain;

import lombok.Data;

@Data
public class ShipDeviceBO {
//    private Long deviceId;//
    private Long id;  //
    private Long shipId;  //船舶Id
    private String revolutionSpeed;//主机转速
//    private String hostLoad;  //主机负荷
//    private String runningHours;  //主机运行小时
//    private String superchargerSpeed; //增压器转速
    private String startingAirPressure;//起动空气压力
//    private String tbbt;  //推力块轴承温度
    private String meav;  //主机轴向振动
    private String meloip;//主机滑油进口压力
    private String mefc;  //主机燃油刻度
//    private String meloot;//主机滑油出口温度
    private String meloit;//主机滑油进口温度
    private String sloip; //增压器滑油进口压力
    private String sloot; //增压器滑油出口温度
    private String fip;//主机燃油进口压力
    private String fit;//主机燃油进口温度
//    private String pcoip; //活塞冷却油进口压力
    private String cloit; //汽缸滑油进口温度
    private String mejip; //主机缸套冷却水进口压力
    private String mejit; //主机缸套冷却水进口温度
//    private String siep;  //暂时弃用
    private String siet;  //增压器进口排气温度
    private String soet;  //增压器出口排气温度
    private String controlAirPressure;//控制空气压力
//    private String smp;//扫气集管压力
//    private String smt;//扫气集管温度
//    private String runstatus1;//一号机运行状态
//    private String sae1;  //1号辅机转速
//    private String pog1;  //1号发电机功率
//    private String ats1;  //1号辅机增压器转速
    private String alop1; //1号辅机滑油压力
    private String alot1; //1号辅机滑油温度
    private String afp1;  //1号辅机燃油压力
    private String aft1;  //1号辅机燃油温度
//    private String runstatus2;//二号机运行状态
//    private String sae2;  //2号辅机转速
//    private String pog2;  //2号发电机功率
//    private String ats2;  //2号辅机增压器转速
    private String alop2; //2号辅机滑油压力
    private String alot2; //2号辅机滑油温度
    private String afp2;  //2号辅机燃油压力
    private String aft2;  //2号辅机燃油温度
//    private String runstatus3;//三号机运行状态
//    private String sae3;  //3号辅机转速
//    private String pog3;  //3号发电机功率
//    private String ats3;  //3号辅机增压器转速
    private String alop3; //3号辅机滑油压力
    private String alot3; //3号辅机滑油温度
    private String afp3;  //3号辅机燃油压力
    private String aft3;  //3号辅机燃油温度
//    private String boilerSteamPressure;//锅炉蒸汽压力
//    private String boilerWaterLevel;  //锅炉水位
//    private String bot;//锅炉燃油温度
    private String sternDraught;  //艉部吃水
    private String stemDraft; //艏部吃水
    private String starboardDraft;//右舷吃水
    private String portDraft; //左舷吃水
    private String trim;  //纵倾
    private String heel;  //横倾
//    private String mefit; //暂时弃用
//    private String mefif; //主机燃油进口流量
//    private String mefof; //主机燃油出口流量
//    private String gfif;  //辅机燃油进口流量
//    private String gfof;  //辅机燃油出口流量
//    private String bfoif; //锅炉燃油进口流量
//    private String bfoof; //锅炉燃油出口流量
//    private String sefif; //辅机燃油进口流量
//    private String sefof; //辅机燃油出口流量
//    private String deviceName;//设备名
//    private Long sourceId;//对应同步数据id
//    private Long createTime;  //创建时间
//    private Long updateTime;  //更新时间
    private Long collectTime; //数据采集时间
    private String shaftBearingTempFore;  //艉部前轴承温度
    private String shaftBearingTempAft;//   艉部后轴承温度
    private String shaftBearingTempInter; //中间轴承温度
    private String coolSeaWPress; //冷却海水压力
    private String thrustBearingTemp; //推力轴承温度
    private String oilBunkerTempL;//左燃油舱温度
    private String oilBunkerTempR;//右燃油舱温度
    private String oilBunkerLevL; //左燃油舱液位
    private String oilBunkerLevR; //右燃油舱液位
    private String dieselFuelTankLev; //柴油舱液位
    private String generatorRunning1; //1号辅机运行
    private String generatorRunning2; //2号辅机运行
    private String generatorRunning3; //3号辅机运行
//    private String imoNumber; //imo编号
    private String name;  //船名
    private String dataSyncTime;//数据同步时间
//    private String fcontrolairpressure;//1号辅机控制空气压力
//    private String fstartingairpressure;  //1号辅机启动空气压力
    private String gasOutletTemp1;//1号辅机排气出口温度
    private String airReceiverTemp;//主机扫气储气罐温度
    private String airMainFoldPress;  //主机扫气集管压力
    private String fstartingAirPressure1; //1号辅机启动空气压力
    private String fstartingAirPressure2; //2号辅机启动空气压力
    private String fstartingAirPressure3; //3号辅机启动空气压力
    private String fcontrolAirPressure1;  //1号辅机控制空气压力
    private String fcontrolAirPressure2;  //2号辅机控制空气压力
    private String fcontrolAirPressure3;  //3号辅机控制空气压力
    private String gasOutletTemp2;//2号辅机排气出口温度
    private String gasOutletTemp3;//3号辅机排气出口温度
//    private List<ShipDeviceModPO> shipDeviceModPOList;
    private String oneGwdit;//      1号辅机高温淡水进口温度
    private String oneGwdot;//      1号辅机高温淡水出口温度
    private String oneDwdit;//      1号辅机低温淡水进口温度
    private String oneGwdip;//      1号辅机高温淡水进口压力
    private String oneDwdip;//      1号辅机低温淡水进口压力
    private String oneoneOpot;//	1号辅机1号气缸排气温度
    private String onetwoOpot;//	1号辅机2号气缸排气温度
    private String onethreeOpot;//	1号辅机3号气缸排气温度
    private String onefourOpot;//	1号辅机4号气缸排气温度
    private String twofourTpot;//	2号辅机4号气缸排气温度
    private String twothreeTpot;//	2号辅机3号气缸排气温度
    private String twotwoTpot;//	2号辅机2号气缸排气温度
    private String twooneTpot;//	2号辅机1号气缸排气温度
    private String twoGwdip;//	    2号辅机高温淡水进口压力
    private String twoDwdip;//	    2号辅机低温淡水进口压力
    private String threefourTpot;//	3号辅机4号气缸排气温度
    private String threethreeTpot;//3号辅机3号气缸排气温度
    private String threetwoTpot;//	3号辅机2号气缸排气温度
    private String threeoneTpot;//	3号辅机1号气缸排气温度
    private String threeGwdip;//	3号辅机高温淡水进口压力
    private String threeDwdip;//	3号辅机低温淡水进口压力
    private String oneZpt;//	    主机1号气缸排气温度
    private String twoZpt;//	    主机2号气缸排气温度
    private String threeZpt;//	    主机3号气缸排气温度
    private String fourZpt;//	    主机4号气缸排气温度
    private String fiveZpt;//	    主机5号气缸排气温度
    private String sixZpt;//	    主机6号气缸排气温度
    private String oneQot;//	    主机1号气缸缸套冷却水出口温度
    private String twoQot;//	    主机2号气缸缸套冷却水出口温度
    private String threeQot;//	    主机3号气缸缸套冷却水出口温度
    private String fourQot;//	    主机4号气缸缸套冷却水出口温度
    private String fiveQot;//	    主机5号气缸缸套冷却水出口温度
    private String sixQot;//	    主机6号气缸缸套冷却水出口温度
    private String zjlqSt;//	    主机空冷器前扫气温度
    private String zjlhSt;//	    主机空冷器后扫气温度
    private String zjlqsJit;// 	    主机空冷器冷却水进口温度
    private String zjlqsJot;//	    主机空冷器冷却水出口温度
    private String zjpAip;//	    主机排气阀弹簧空气压力
    private String zjlqJip;//	    主机空冷器冷却水进口压力
    private String oneQhlot;// 	    主机1号气缸活塞冷却油出口温度
    private String twoQhlot;//	    主机2号气缸活塞冷却油出口温度
    private String threeQhlot;//    主机3号气缸活塞冷却油出口温度
    private String fourQhlot;//	    主机4号气缸活塞冷却油出口温度
    private String fiveQhlot;//	    主机5号气缸活塞冷却油出口温度
    private String sixQhlot;//	    主机6号气缸活塞冷却油出口温度
    private String oneRct;//	    1号燃油沉淀舱温度
    private String oneRrt;//	    1号燃油日用舱温度
    private String twoRct;//	    2号燃油沉淀舱温度
    private String twoRrt;//	    2号燃油日用舱温度
    private String oneRcl;//	    1号燃油沉淀舱液位
    private String oneRrl;//	    1号燃油日用舱液位
    private String twoRcl;//	    2号燃油沉淀舱液位
    private String twoRrl;//	    2号燃油日用舱液位

    private static final long serialVersionUID = 1L;
}
