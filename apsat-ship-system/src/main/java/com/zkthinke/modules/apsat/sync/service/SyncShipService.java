package com.zkthinke.modules.apsat.sync.service;

import com.zkthinke.modules.apsat.sync.domain.SyncShip;
import com.zkthinke.modules.apsat.sync.service.dto.SyncShipDTO;
import com.zkthinke.modules.apsat.sync.service.dto.SyncShipQueryCriteria;
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
* @author weicb
* @date 2020-10-19
*/
//@CacheConfig(cacheNames = "syncShip")
public interface SyncShipService {

    /**
    * queryAll 分页
    * @param criteria
    * @param pageable
    * @return
    */
    //@Cacheable(keyGenerator = "keyGenerator")
    Object queryAll(SyncShipQueryCriteria criteria, Pageable pageable);

    /**
    * queryAll 不分页
    * @param criteria
    * @return
    */
    //@Cacheable(keyGenerator = "keyGenerator")
    public List<SyncShipDTO> queryAll(SyncShipQueryCriteria criteria);

    /**
     * findById
     * @param id
     * @return
     */
    //@Cacheable(key = "#p0")
    SyncShipDTO findById(Long id);

    /**
     * create
     * @param resources
     * @return
     */
    //@CacheEvict(allEntries = true)
    SyncShipDTO create(SyncShip resources);

    /**
     * update
     * @param resources
     */
    //@CacheEvict(allEntries = true)
    void update(SyncShip resources);

    /**
     * delete
     * @param id
     */
    //@CacheEvict(allEntries = true)
    void delete(Long id);


    void batchInsertOrUpdate(List<SyncShip> ships);
}