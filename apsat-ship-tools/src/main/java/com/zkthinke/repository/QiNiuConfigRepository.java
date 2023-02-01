package com.zkthinke.repository;

import com.zkthinke.domain.QiniuConfig;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Zheng Jie
 * @date 2020-10-31
 */
public interface QiNiuConfigRepository extends JpaRepository<QiniuConfig,Long> {
}
