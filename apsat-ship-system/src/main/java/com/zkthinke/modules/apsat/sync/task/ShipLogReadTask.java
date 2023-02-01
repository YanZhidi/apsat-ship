package com.zkthinke.modules.apsat.sync.task;

import com.zkthinke.modules.apsat.ship.domain.Ship;
import com.zkthinke.modules.apsat.sync.runnable.ShipDataSyncCallable;
import com.zkthinke.modules.apsat.sync.runnable.ShipLogReadCallable;
import com.zkthinke.modules.apsat.sync.service.ShipLogReadService;
import com.zkthinke.utils.CommonThreadPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.Future;

/**
 * 解析神华日志文件定时任务
 */
@Component
@Slf4j
public class ShipLogReadTask {
    @Autowired
    ShipLogReadService shipLogReadService;

    public void run(){
        CommonThreadPool pool = CommonThreadPool.getInstance();
        readShipLogShip(pool);

    }

    /**
     * 同步船舶详细信息
     * @param pool
     */
    private void readShipLogShip(CommonThreadPool pool){
        log.debug("解析神华日志文件开始");
        try {
            Future<Integer> future = pool.addExecuteTask(new ShipLogReadCallable(shipLogReadService));
        } catch (Exception e){
            log.error("解析神华日志文件失败");
        }
        log.debug("解析神华日志文件结束");
    }
}
