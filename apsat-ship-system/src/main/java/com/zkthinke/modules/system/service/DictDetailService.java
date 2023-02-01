package com.zkthinke.modules.system.service;

import com.zkthinke.modules.system.domain.DictDetail;
import com.zkthinke.modules.system.service.dto.DictDetailDTO;
import com.zkthinke.modules.system.service.dto.DictDetailQueryCriteria;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
* @author Zheng Jie
* @date 2019-04-10
*/
public interface DictDetailService {

    /**
     * findById
     * @param id
     * @return
     */
    DictDetailDTO findById(Long id);

    /**
     * create
     * @param resources
     * @return
     */
    DictDetailDTO create(DictDetail resources);

    /**
     * update
     * @param resources
     */
    void update(DictDetail resources);

    /**
     * delete
     * @param id
     */
    void delete(Long id);


    //    @Cacheable(keyGenerator = "keyGenerator")
    Map queryAll(DictDetailQueryCriteria criteria, Pageable pageable);

}
