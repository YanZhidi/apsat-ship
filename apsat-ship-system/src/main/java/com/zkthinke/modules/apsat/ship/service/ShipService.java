package com.zkthinke.modules.apsat.ship.service;

import com.zkthinke.modules.apsat.ship.domain.Ship;
import com.zkthinke.modules.apsat.ship.service.dto.ShipDTO;
import com.zkthinke.modules.apsat.ship.service.dto.ShipQueryCriteria;
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
* @author weicb
* @date 2020-10-15
*/
@CacheConfig(cacheNames = "ship")
public interface ShipService {

    /**
    * queryAll 分页
    * @param criteria
    * @param pageable
    * @return
    */
    //@Cacheable(keyGenerator = "keyGenerator")
    Object queryAll(ShipQueryCriteria criteria, Pageable pageable);

    /**
    * queryAll 不分页
    * @param criteria
    * @return
    */
    //@Cacheable(keyGenerator = "keyGenerator")
    public Object queryAll(ShipQueryCriteria criteria);

    /**
     * findById
     * @param id
     * @return
     */
    //@Cacheable(key = "#p0")
    ShipDTO findById(Long id);

    /**
     * create
     * @param resources
     * @return
     */
    @CacheEvict(value = "ships",key = "'tree'")
    ShipDTO create(Ship resources);

    /**
     * update
     * @param resources
     */
    //@CacheEvict(allEntries = true)
    void update(Ship resources);

    /**
     * updateSync
     * @param resources
     */
    //@CacheEvict(allEntries = true)
    void updateSync(Ship resources);

    /**
     * delete
     * @param id
     */
    @CacheEvict(value = "ships",key = "'tree'")
    void delete(Long id);

    Optional<Ship> findOne(ShipQueryCriteria criteria);

    List<Ship> findAll();

    /**
     * 更新最新设备详情同步时间
     */
    int updateLastDeviceStime(Long shipId, String requestTime);

    String getZdaTime();

    @Cacheable(value = "ships",key = "'tree'")
    Object getMenuTree();

}
