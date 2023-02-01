package com.zkthinke.modules.apsat.ship.device.service;

import com.zkthinke.modules.apsat.ship.device.domain.*;
import com.zkthinke.modules.apsat.ship.domain.ShipDeviceBO;
import com.zkthinke.modules.apsat.ship.domain.ShipDeviceModBO;
import com.zkthinke.modules.apsat.ship.domain.ShipDeviceModPO;
import com.zkthinke.modules.apsat.ship.domain.ShipDevicePO;

import java.util.List;
import java.util.Map;

public interface DeviceService {
    Map<Long,Map<String, Object>> findShipFuelData(Long shipId, Long collectTimeBegin, Long collectTimeEnd);

    DeviceRealTimeBO realTimeFuelConsumptionData(Long shipId);

    ShipDevicePO realTimeEquipmentStatus(Long shipId);

    SmartCabinBO smartCabin(Long shipId, Long collectTimeBegin, Long collectTimeEnd);

    List<RevolutionGroundSpeedBO> findRotationGroundSpeedList(Long shipId, Long collectTimeBegin, Long collectTimeEnd);

    AlopBO findAlopList(Long shipId, Long collectTimeBegin, Long collectTimeEnd);

    HostDetailsBO hostDetails(Long shipId);

    List<ShipDeviceBO> findShipDeviceList(Long shipId, Long collectTimeBegin, Long collectTimeEnd);

    List<ShipDeviceModBO> findShipDeviceModList(Long shipId, Long collectTimeBegin, Long collectTimeEnd);
}
