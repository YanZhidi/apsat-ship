package com.zkthinke.modules.apsat.ship.domain;

import lombok.Data;

import java.util.Objects;

@Data
public class ShipAlarmMeritDefaultPO {
    private String merit;
    private String unit;
    private String low;
    private String high;

    public ShipAlarmMeritDefaultPO() {
    }

    public ShipAlarmMeritDefaultPO(ShipAlarmMeritPO po) {
        this.merit = po.getMerit();
        this.unit = po.getUnit();
        this.low = po.getLow();
        this.high = po.getHigh();
    }
}
