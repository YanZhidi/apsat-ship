package com.zkthinke.modules.apsat.ship.domain;

import lombok.Data;

@Data
public class ShipVdmBO {

    private String name;//名称

    private String mmsiNumber;//mmsi编号
    private String callSign;//呼号
    private String imoNumber;//imo编号
    private String shipType;//船舶和货物类型
    private String navigationalStatus;//航行状态
    private String maxStaticDraft;//目前最大静态吃水


    private String trueHeading;//实际航向
    private String cog;//地面航线
    private String sog;//地面航速
    private String longitude;//经度
    private String latitude;//纬度

    private String destination;//目的地
    private String eta;//估计到达时间

    private String shipDimensions;//总体尺寸位置参考

    private String shipLength;//船长
    private String shipWidth;//船宽

    public ShipVdmBO() {
    }

    public ShipVdmBO(ShipVdmPO po) {
        name = po.getName();
        mmsiNumber = po.getMmsiNumber();
        callSign = po.getCallSign();
        imoNumber = po.getImoNumber();
        shipType = po.getShipType();
        navigationalStatus = po.getNavigationalStatus();
        maxStaticDraft = po.getMaxStaticDraft();
        trueHeading = po.getTrueHeading();
        cog = po.getCog();
        sog = po.getSog();
        longitude = po.getLongitude();
        latitude = po.getLatitude();
        destination = po.getDestination();
        eta = po.getEta();
        shipDimensions = po.getShipDimensions();
    }
}

