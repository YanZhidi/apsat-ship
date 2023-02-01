package com.zkthinke.modules.apsat.sync.service;

import com.zkthinke.modules.apsat.ship.domain.Ship;

import java.util.Optional;

/**
 * @Author: weicb
 * @Date: 2020/10/13 13:16
 */
public interface ShipDataSyncService {

    /**
     * 同步船舶详细信息(航行状态)
     * @return
     */
    Integer syncShipData(Ship ship, Optional<String> lastSuccessTime);

}
