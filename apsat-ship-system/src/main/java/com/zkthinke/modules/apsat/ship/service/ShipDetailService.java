package com.zkthinke.modules.apsat.ship.service;

import com.zkthinke.modules.apsat.ship.domain.ShipDetail;
import com.zkthinke.modules.apsat.ship.domain.ShipDetailSimple;
import com.zkthinke.modules.apsat.ship.service.dto.ShipDetailDTO;
import com.zkthinke.modules.apsat.ship.service.dto.ShipDetailQueryCriteria;
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;
import com.zkthinke.modules.apsat.ship.service.dto.ShipDetailSimpleDTO;
import com.zkthinke.modules.apsat.sync.domain.SyncShip;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
* @author weicb
* @date 2020-10-15
*/
//@CacheConfig(cacheNames = "shipDetail")
public interface ShipDetailService {

    /**
    * queryAll 分页
    * @param criteria
    * @param pageable
    * @return
    */
    //@Cacheable(keyGenerator = "keyGenerator")
    Object queryAll(ShipDetailQueryCriteria criteria, Pageable pageable);

    /**
    * queryAll 不分页
    * @param criteria
    * @return
    */
    //@Cacheable(keyGenerator = "keyGenerator")
    public Object queryAll(ShipDetailQueryCriteria criteria);

    /**
     * findById
     * @param id
     * @return
     */
    //@Cacheable(key = "#p0")
    ShipDetailDTO findById(Long id);

    /**
     * create
     * @param resources
     * @return
     */
    //@CacheEvict(allEntries = true)
    ShipDetailDTO create(ShipDetail resources);

    /**
     * update
     * @param resources
     */
    //@CacheEvict(allEntries = true)
    void update(ShipDetail resources);

    /**
     * delete
     * @param id
     */
    //@CacheEvict(allEntries = true)
    void delete(Long id);

    /**
     * 查询船舶详细信息
     * @param shipId 船舶 id
     * @return
     */
    ShipDetailDTO findLastByShipId(Long shipId);

    void batchInsertOrUpdate(List<ShipDetail> shipDetails);

    List<ShipDetailSimpleDTO> findSimpleByShipId(Long shipId, Optional<Long> collectTimeBegin, Optional<Long> collectTimeEnd);

    List<ShipDetail> findByIdOrLike(Long id, String groundSpeed);
}