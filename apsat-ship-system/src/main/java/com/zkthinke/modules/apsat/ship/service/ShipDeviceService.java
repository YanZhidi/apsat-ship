package com.zkthinke.modules.apsat.ship.service;

import com.zkthinke.modules.apsat.ship.domain.ShipDevice;
import com.zkthinke.modules.apsat.ship.service.dto.ShipDeviceDTO;
import com.zkthinke.modules.apsat.ship.service.dto.ShipDeviceQueryCriteria;
import com.zkthinke.modules.apsat.ship.service.dto.ShipDeviceSimpleDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;

/**
* @author weicb
* @date 2020-10-28
*/
//@CacheConfig(cacheNames = "shipDevice")
public interface ShipDeviceService {

    /**
    * queryAll 分页
    * @param criteria
    * @param pageable
    * @return
    */
    //@Cacheable(keyGenerator = "keyGenerator")
    Object queryAll(ShipDeviceQueryCriteria criteria, Pageable pageable);

    /**
    * queryAll 不分页
    * @param criteria
    * @return
    */
    //@Cacheable(keyGenerator = "keyGenerator")
    public Object queryAll(ShipDeviceQueryCriteria criteria);

    /**
     * findById
     * @param id
     * @return
     */
    //@Cacheable(key = "#p0")
    ShipDeviceDTO findById(Long id);

    /**
     * create
     * @param resources
     * @return
     */
    //@CacheEvict(allEntries = true)
    ShipDeviceDTO create(ShipDevice resources);

    /**
     * update
     * @param resources
     */
    //@CacheEvict(allEntries = true)
    void update(ShipDevice resources);

    /**
     * delete
     * @param id
     */
    //@CacheEvict(allEntries = true)
    void delete(Long id);

    void batchInsertOrUpdate(List<ShipDevice> shipDetails);

    ShipDeviceDTO findLastByShipId(Long shipId);

    List<ShipDeviceSimpleDTO> findSimpleByShipId(Long shipId, Optional<Long> collectTimeBegin, Optional<Long> collectTimeEnd);
}