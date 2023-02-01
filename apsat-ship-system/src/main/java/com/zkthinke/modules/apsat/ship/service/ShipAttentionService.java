package com.zkthinke.modules.apsat.ship.service;

import com.zkthinke.modules.apsat.ship.domain.ShipAttention;
import com.zkthinke.modules.apsat.ship.service.dto.ShipAttentionDTO;
import com.zkthinke.modules.apsat.ship.service.dto.ShipAttentionQueryCriteria;
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
* @author weicb
* @date 2020-11-01
*/
//@CacheConfig(cacheNames = "shipAttention")
public interface ShipAttentionService {

    /**
    * queryAll 分页
    * @param criteria
    * @param pageable
    * @return
    */
    //@Cacheable(keyGenerator = "keyGenerator")
    Object queryAll(ShipAttentionQueryCriteria criteria, Pageable pageable);

    /**
    * queryAll 不分页
    * @param criteria
    * @return
    */
    //@Cacheable(keyGenerator = "keyGenerator")
    public Object queryAll(ShipAttentionQueryCriteria criteria);

    /**
     * findById
     * @param id
     * @return
     */
    //@Cacheable(key = "#p0")
    ShipAttentionDTO findById(Long id);

    /**
     * create
     * @param resources
     * @return
     */
    //@CacheEvict(allEntries = true)
    ShipAttentionDTO create(ShipAttention resources);

    /**
     * update
     * @param resources
     */
    //@CacheEvict(allEntries = true)
    void update(ShipAttention resources);

    /**
     * delete
     * @param id
     */
    //@CacheEvict(allEntries = true)
    void delete(Long id);

    void deleteByShipId(Long shipId, Long userId);

    List<ShipAttentionDTO> findAttentions(List<Long> ids, Long userId);
}