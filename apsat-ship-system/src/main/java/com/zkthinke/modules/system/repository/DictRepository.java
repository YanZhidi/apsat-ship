package com.zkthinke.modules.system.repository;

import com.zkthinke.modules.system.domain.Dict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
* @author Zheng Jie
* @date 2019-04-10
*/
public interface DictRepository extends JpaRepository<Dict, Long>, JpaSpecificationExecutor {

    /**
     * 根据ids获取数据
     * @param ids
     * @return
     */
    @Override
    List<Dict> findAllById(Iterable<Long> ids);
}