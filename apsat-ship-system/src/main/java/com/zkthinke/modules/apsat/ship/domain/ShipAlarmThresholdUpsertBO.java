package com.zkthinke.modules.apsat.ship.domain;

import lombok.Data;

import java.util.List;

@Data
public class ShipAlarmThresholdUpsertBO {
    private Integer shipId;
    private List<ShipAlarmThresholdPO> shipAlarmThresholdList;
}
