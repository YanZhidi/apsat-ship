package com.zkthinke.modules.apsat.sync.runnable;

import com.zkthinke.modules.apsat.ship.domain.Ship;
import com.zkthinke.modules.apsat.sync.service.ShipDataSyncService;
import com.zkthinke.utils.StringUtils;

import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * @Author: weicb
 * @Date: 2020/10/13 17:34
 */
public class ShipDataSyncCallable implements Callable<Integer> {

    private ShipDataSyncService shipDataSyncService;
    private Ship ship;

    public ShipDataSyncCallable(ShipDataSyncService shipDataSyncService, Ship ship){
        this.shipDataSyncService = shipDataSyncService;
        this.ship = ship;
    }

    @Override
    public Integer call() {
        if (StringUtils.isEmpty(ship.getImoNumber()) || StringUtils.isEmpty(ship.getName())) {
            return 1;
        }
        return shipDataSyncService.syncShipData(ship, Optional.ofNullable(ship.getLastDetailStime()));
    }
}