package com.zkthinke.modules.apsat.ship.service;

import com.zkthinke.modules.apsat.ship.domain.ShipAlarmMeritDefaultPO;
import com.zkthinke.modules.apsat.ship.domain.ShipAlarmMeritPO;
import com.zkthinke.modules.apsat.ship.domain.ShipAlarmMeritUpsertBO;

import java.util.List;

public interface ShipAlarmMeritService {
    List<ShipAlarmMeritPO> queryShipAlarmMerit(Integer shipId);

    void upsertShipAlarmMerit(ShipAlarmMeritUpsertBO shipAlarmMeritUpsertBO);

    List<ShipAlarmMeritDefaultPO> queryValidShipAlarmMerit(Integer shipId);
}
