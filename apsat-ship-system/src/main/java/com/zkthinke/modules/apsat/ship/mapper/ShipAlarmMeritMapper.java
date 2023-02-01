package com.zkthinke.modules.apsat.ship.mapper;

import com.zkthinke.modules.apsat.ship.domain.ShipAlarmMeritPO;

import java.util.List;

public interface ShipAlarmMeritMapper {
    List<ShipAlarmMeritPO> queryShipAlarmMerit(Integer shipId);

    List<ShipAlarmMeritPO> queryShipAlarmMeritDefault();

    void batchInsertShipAlarmMerit(List<ShipAlarmMeritPO> shipAlarmMeritList);

    void updateShipAlarmMerit(ShipAlarmMeritPO po);
}
