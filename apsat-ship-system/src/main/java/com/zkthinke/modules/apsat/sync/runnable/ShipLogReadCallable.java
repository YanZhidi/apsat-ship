package com.zkthinke.modules.apsat.sync.runnable;

import com.zkthinke.modules.apsat.ship.domain.Ship;
import com.zkthinke.modules.apsat.sync.service.ShipDataSyncService;
import com.zkthinke.modules.apsat.sync.service.ShipLogReadService;
import com.zkthinke.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.concurrent.Callable;

public class ShipLogReadCallable implements Callable<Integer> {

    ShipLogReadService shipLogReadService;


    public ShipLogReadCallable(ShipLogReadService shipLogReadService){
        this.shipLogReadService = shipLogReadService;
    }

    @Override
    public Integer call() {
        return shipLogReadService.readLogService();
    }
}
