package com.zkthinke.modules.apsat.ship.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ShipAlarmMeritUpsertBO implements Serializable {
    private Integer shipId;
    private List<ShipAlarmMeritPO> shipAlarmMeritList;
}
