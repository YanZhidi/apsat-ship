package com.zkthinke.modules.apsat.ship.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class ShipVdmPO implements Serializable {

    private String messageId;

    private Long shipId;//船舶Id
    private String mmsiNumber;//mmsi编号
    private String navigationalStatus;//航行状态
    private String steeringSpeed;//转向速度
    private String sog;//地面航速
    private String positionAccuracy;//位置准确度
    private String longitude;//经度
    private String latitude;//纬度
    private String cog;//地面航线
    private String trueHeading;//实际航向
    private String utcTimeStamp;//UTC时戳(只有秒)
    private String specificManoeuvreIndicator;//特定操作指示符：0(默认值):不可用、1:未进行特定操作、2:进行特定操作
    private String raimFlag;//电子定位装置的接收机自主整体检测标志：0(默认值):RAIM未使用、1:RAIM正在使用
    private String communicationStatus;//通信状态、不用

    private String statusOfCurrentGnssPosition;//位置等待时间：0:报告的位置等待时间小于5秒、1(默认):报告的位置等待时间大于5秒

    private String aisVersionIndicator;//AIS版本指示符、不用
    private String imoNumber;//imo编号
    private String callSign;//呼号
    private String name;//名称
    private String shipType;//船舶和货物类型
    private String shipDimensions;//总体尺寸位置参考
    private String epfdType;//电子定位装置类型
    private String eta;//估计到达时间
    private String maxStaticDraft;//目前最大静态吃水
    private String destination;//目的地
    private String dte;//数据终端就绪

    private Long vdm1Time;
    private Long vdm2Time;
    private Long vdm3Time;
    private Long vdm5Time;
    private Long vdm18Time;
    private Long vdm19Time;
    private Long vdm24Time;
    private Long vdm27Time;
    private Long positionUpdateTime;//位置数据更新时间 1、2、3、18、19、27
    private Long staticUpdateTime;//静态数据和航行数据更新时间 5、19、24
    private Long updateTime;//更新时间
}