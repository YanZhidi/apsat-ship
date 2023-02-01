package com.zkthinke.modules.apsat.ship.domain;

import lombok.Data;

import java.util.Objects;

@Data
public class ShipAlarmMeritPO {
    private Integer shipId;
    private String merit;
    private String unit;
    private String low;
    private String high;
    private Integer state;
    private String updateTime;
    private String createTime;
    private String createUser;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShipAlarmMeritPO that = (ShipAlarmMeritPO) o;
        return Objects.equals(shipId, that.shipId) &&
                Objects.equals(merit, that.merit) &&
                Objects.equals(unit, that.unit) &&
                Objects.equals(low, that.low) &&
                Objects.equals(high, that.high) &&
                Objects.equals(state, that.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shipId, merit, unit, low, high, state);
    }
}
