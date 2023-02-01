package com.zkthinke.modules.apsat.sync.service;

import java.util.Optional;

/**
 * @Author: weicb
 * @Date: 2020/10/15 10:18
 */
public interface ShipDeviceDataSyncService {

    /**
     * 同步船舶设备信息(能效)
     * @return
     */
    Integer syncData(Long shipId, String imoNumber, String name, Optional<String> lastSuccessTime);


}
