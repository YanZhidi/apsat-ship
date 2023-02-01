package com.zkthinke.modules.apsat.ship.domain;

import lombok.Data;

import java.util.Objects;

@Data
public class ShipAlarmThresholdPO {
    private Integer shipId;
    private String merit;
    private String threshold;
    private String unit;
    private Integer state;
    private String updateTime;
    private String createTime;
    private String createUser;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShipAlarmThresholdPO that = (ShipAlarmThresholdPO) o;
        return Objects.equals(shipId, that.shipId) &&
                Objects.equals(merit, that.merit) &&
                Objects.equals(threshold, that.threshold) &&
                Objects.equals(unit, that.unit) &&
                Objects.equals(state, that.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shipId, merit, threshold, unit, state);
    }
}
