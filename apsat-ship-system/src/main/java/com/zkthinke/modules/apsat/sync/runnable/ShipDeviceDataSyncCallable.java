package com.zkthinke.modules.apsat.sync.runnable;

import com.zkthinke.modules.apsat.ship.domain.Ship;
import com.zkthinke.modules.apsat.sync.service.ShipDeviceDataSyncService;
import com.zkthinke.utils.StringUtils;

import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * @Author: weicb
 * @Date: 2020/10/13 17:34
 */
public class ShipDeviceDataSyncCallable implements Callable<Integer> {

    private ShipDeviceDataSyncService shipDeviceDataSyncService;
    private Ship ship;

    public ShipDeviceDataSyncCallable(ShipDeviceDataSyncService shipDeviceDataSyncService, Ship ship){
        this.shipDeviceDataSyncService = shipDeviceDataSyncService;
        this.ship = ship;
    }

    @Override
    public Integer call() {
        if (StringUtils.isEmpty(ship.getImoNumber()) || StringUtils.isEmpty(ship.getName())) {
            return 1;
        }
        return shipDeviceDataSyncService.syncData(ship.getId(), ship.getImoNumber(), ship.getName(), Optional.ofNullable(ship.getLastDeviceStime()));
    }
}