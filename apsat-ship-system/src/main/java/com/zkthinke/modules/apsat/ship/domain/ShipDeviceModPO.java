package com.zkthinke.modules.apsat.ship.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class ShipDeviceModPO implements Serializable {
    private Integer shipId;//船舶Id
    private Integer id;
    private String glll;    //锅炉(进口)(瞬时)流量
    private String glljll;  //锅炉(进口)累计流量
    private String glmd;    //锅炉(进口)密度
    private String glwd;    //锅炉(进口)温度
    private String glbz1;   //锅炉备注1
    private String glbz2;   //锅炉备注2
    private String glbz3;   //锅炉备注3
    private String glbz4;   //锅炉备注4
    private String glbz5;   //锅炉备注5
    private String glckll;  //锅炉出口(瞬时)流量
    private String glckljll;//锅炉出口累计流量
    private String glckmd;  //锅炉出口密度
    private String glckwd;  //锅炉出口温度
    private String zjjkll;  //主机进口(瞬时)流量
    private String zjjkljll;//主机进口累计流量
    private String zjjkmd;  //主机进口密度
    private String zjjkwd;  //主机进口温度
    private String zjjkbz1; //主机进口备注1
    private String zjjkbz2; //主机进口备注2
    private String zjjkbz3; //主机进口备注3
    private String zjjkbz4; //主机进口备注4
    private String zjjkbz5; //主机进口备注5
    private String zjckll;  //主机出口(瞬时)流量
    private String zjckljll;//主机出口累计流量
    private String zjckmd;  //主机出口密度
    private String zjckwd;  //主机出口温度
    private String zjckbz1; //主机出口备注1
    private String zjckbz2; //主机出口备注2
    private String zjckbz3; //主机出口备注3
    private String zjckbz4; //主机出口备注4
    private String zjckbz5; //主机出口备注5
    private String fdjjkll; //发电机进口(瞬时)流量
    private String fdjjkljll;//发电机进口累计流量
    private String fdjjkmd; //发电机进口密度
    private String fdjjkwd; //发电机进口温度
    private String fdjjkbz1;//发电机进口备注1
    private String fdjjkbz2;//发电机进口备注2
    private String fdjjkbz3;//发电机进口备注3
    private String fdjjkbz4;//发电机进口备注4
    private String fdjjkbz5;//发电机进口备注5
    private String fdjckll; //发电机出口(瞬时)流量
    private String fdjckljll;//发电机出口累计流量
    private String fdjckmd; //发电机出口密度
    private String fdjckwd; //发电机出口温度
    private String fdjckbz1;//发电机出口备注1
    private String fdjckbz2;//发电机出口备注2
    private String fdjckbz3;//发电机出口备注3
    private String fdjckbz4;//发电机出口备注4
    private String fdjckbz5;//发电机出口备注5
    private String fdjglxh1;    //发电机功率信号1
    private String fdjglxh2;    //发电机功率信号2
    private String fdjglxh3;    //发电机功率信号3
    private Long deviceId;//ShipDevice表Id
    private String dataSyncTime;//数据同步时间
    private static final long serialVersionUID = 1L;

}
