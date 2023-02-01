package com.zkthinke.modules.apsat.ship.domain;

import lombok.Data;

import java.util.List;

@Data
public class TyphoonTrackBO {
    private String bizDate; //轨迹点时间
    private String lng;     //经度
    private String lat;     //纬度
    private String centerSpeed;     //中心点风速 米/秒
    private String centerPressure;  //中心点气压 百帕
    private String moveSpeed;       //移动速度 公里/小时
    private String moveDirection;   //移动方向
    private String r7Se;        //七级风圈-东南
    private String r7Sw;        //七级风圈-西南
    private String r7Ne;        //七级风圈-东北
    private String r7Nw;        //七级风圈-西北
    private List<TyphoonTrackBO> forecastList; //预测列表

    public TyphoonTrackBO() {
        this.r7Se = "";
        this.r7Sw = "";
        this.r7Ne = "";
        this.r7Nw = "";
    }
}
