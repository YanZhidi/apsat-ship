package com.zkthinke.modules.apsat.ship.service;

import com.zkthinke.modules.apsat.ship.domain.ShipAlarmThresholdPO;
import com.zkthinke.modules.apsat.ship.domain.ShipAlarmThresholdUpsertBO;

import java.util.List;

public interface ShipAlarmThresholdService {
    List<ShipAlarmThresholdPO> queryShipAlarmThreshold(Integer shipId);

    void upsertShipAlarmThreshold(ShipAlarmThresholdUpsertBO shipAlarmThresholdUpsertBO);

    String getThresholdLimit(String shipId,String merit);
}
