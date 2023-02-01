package com.zkthinke.modules.apsat.ship.device.domain;

import lombok.Data;

@Data
public class DeviceRealTimeBO {

    //主机进口流量
    public String zjjkll;
    //主机进口密度
    public String zjjkmd;
    //主机进口温度
    public String zjjkwd;
    //主机出口流量
    public String zjckll;
    //主机出口密度
    public String zjckmd;
    //主机出口温度
    public String zjckwd;
    //发电机进口流量
    public String fdjjkll;
    //发电机进口密度
    public String fdjjkmd;
    //发电机进口温度
    public String fdjjkwd;
    //发电机出口流量
    public String fdjckll;
    //发电机出口密度
    public String fdjckmd;
    //发电机出口温度
    public String fdjckwd;
    //锅炉流量
    public String glll;
    //锅炉密度
    public String glmd;
    //锅炉温度
    public String glwd;
    //锅炉出口流量
    public String glckll;
    //锅炉出口密度
    public String glckmd;
    //锅炉出口温度
    public String glckwd;

    //主机燃油消耗
    private String mefio;
    //辅机燃油消耗
    private String sefio;
    //锅炉燃油消耗
    private String bfoio;
    //船舶总燃油消耗
    private String totalio;

    public DeviceRealTimeBO() {
        zjjkll = "0";
        zjjkmd = "0";
        zjjkwd = "0";
        zjckll = "0";
        zjckmd = "0";
        zjckwd = "0";
        fdjjkll = "0";
        fdjjkmd = "0";
        fdjjkwd = "0";
        fdjckll = "0";
        fdjckmd = "0";
        fdjckwd = "0";
        glll = "0";
        glmd = "0";
        glwd = "0";
        glckll = "0";
        glckmd = "0";
        glckwd = "0";
    }


}
