package com.zkthinke.service;

import com.zkthinke.domain.StorageContent;
import com.zkthinke.service.dto.StorageContentDTO;
import com.zkthinke.service.dto.StorageContentQueryCriteria;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;

/**
* Created by cjj on 2019-08-31.
*/
//@CacheConfig(cacheNames = "storageContent")
public interface StorageContentService {

    /**
    * queryAll 分页
    * @param criteria
    * @param pageable
    * @return
    */
    //@Cacheable(keyGenerator = "keyGenerator")
    Object queryAll(StorageContentQueryCriteria criteria, Pageable pageable);

    /**
    * queryAll 不分页
    * @param criteria
    * @return
    */
    //@Cacheable(keyGenerator = "keyGenerator")
    public Object queryAll(StorageContentQueryCriteria criteria);

    /**
     * findById
     * @param id
     * @return
     */
    //@Cacheable(key = "#p0")
    StorageContentDTO findById(Long id);

    /**
     * create
     * @param resources
     * @return
     */
    //@CacheEvict(allEntries = true)
    StorageContentDTO create(StorageContent resources);

    /**
     * update
     * @param resources
     */
    //@CacheEvict(allEntries = true)
    void update(StorageContent resources);

    /**
     * delete
     * @param id
     */
    //@CacheEvict(allEntries = true)
    void delete(Long id);

    Optional<StorageContent> findByFileKey(String key);
}