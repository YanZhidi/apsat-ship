package com.zkthinke.modules.apsat.sync.task;

import com.zkthinke.modules.apsat.ship.domain.Ship;
import com.zkthinke.modules.apsat.ship.service.ShipService;
import com.zkthinke.modules.apsat.sync.runnable.ShipDataSyncCallable;
import com.zkthinke.modules.apsat.sync.service.ShipDataSyncService;
import com.zkthinke.utils.CommonThreadPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.Future;

/**
 * @author weicb
 * @date 2020-10-13
 */
@Component
@Slf4j
public class ShipDataTask {

    private static final Integer PAGE_SIZE = 1000;

    @Autowired
    private ShipDataSyncService shipDataSyncService;

    @Autowired
    private ShipService shipService;


    public void run(){
        CommonThreadPool pool = CommonThreadPool.getInstance();
        List<Ship> list = shipService.findAll();
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        // 同步船舶详细信息
        list.forEach(s -> syncShipData(pool, s));

    }

    /**
     * 同步船舶详细信息
     * @param pool
     */
    private void syncShipData(CommonThreadPool pool, Ship ship){
//        Integer dataSize = 1;
//        int i = 1;
        log.debug("同步船舶详细信息数据开始");
//        do {
        try {
            Future<Integer> future = pool.addExecuteTask(new ShipDataSyncCallable(shipDataSyncService, ship));
//                dataSize = future.get(1 * 60 * 60L, TimeUnit.SECONDS);
        } catch (Exception e){
            log.error("同步船舶详细信息数据超时");
        }
//            i ++;
//        } while (PAGE_SIZE.equals(dataSize));

        log.debug("同步船舶详细信息结束");
    }
}
