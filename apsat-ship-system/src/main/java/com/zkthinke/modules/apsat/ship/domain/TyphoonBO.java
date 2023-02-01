package com.zkthinke.modules.apsat.ship.domain;

import lombok.Data;

import java.util.List;

@Data
public class TyphoonBO {
    private String code;        //编号
    private String name;        //名称
    private String updateTime;  //更新时间
    private String enName;      //英文名
    private String status;      //状态
    private List<TyphoonTrackBO>  trackList;    //轨迹列表
}
