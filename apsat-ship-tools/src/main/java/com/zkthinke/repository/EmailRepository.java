package com.zkthinke.repository;

import com.zkthinke.domain.EmailConfig;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Zheng Jie
 * @date 2020-10-26
 */
public interface EmailRepository extends JpaRepository<EmailConfig,Long> {
}
