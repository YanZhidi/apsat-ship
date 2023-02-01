package com.zkthinke.modules.apsat.sync.service;

import com.zkthinke.modules.apsat.sync.domain.SyncShip;
import com.zkthinke.modules.apsat.sync.domain.SyncShipDevice;
import com.zkthinke.modules.apsat.sync.service.dto.SyncShipDeviceDTO;
import com.zkthinke.modules.apsat.sync.service.dto.SyncShipDeviceQueryCriteria;
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
* @author weicb
* @date 2020-10-28
*/
//@CacheConfig(cacheNames = "syncShipDevice")
public interface SyncShipDeviceService {

    /**
    * queryAll 分页
    * @param criteria
    * @param pageable
    * @return
    */
    //@Cacheable(keyGenerator = "keyGenerator")
    Object queryAll(SyncShipDeviceQueryCriteria criteria, Pageable pageable);

    /**
    * queryAll 不分页
    * @param criteria
    * @return
    */
    //@Cacheable(keyGenerator = "keyGenerator")
    public List<SyncShipDeviceDTO> queryAll(SyncShipDeviceQueryCriteria criteria);

    /**
     * findById
     * @param id
     * @return
     */
    //@Cacheable(key = "#p0")
    SyncShipDeviceDTO findById(Long id);

    /**
     * create
     * @param resources
     * @return
     */
    //@CacheEvict(allEntries = true)
    SyncShipDeviceDTO create(SyncShipDevice resources);

    /**
     * update
     * @param resources
     */
    //@CacheEvict(allEntries = true)
    void update(SyncShipDevice resources);

    /**
     * delete
     * @param id
     */
    //@CacheEvict(allEntries = true)
    void delete(Long id);

    void batchInsertOrUpdate(List<SyncShipDevice> ships);
}