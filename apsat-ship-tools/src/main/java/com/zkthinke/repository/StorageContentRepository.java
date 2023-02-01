package com.zkthinke.repository;

import com.zkthinke.domain.StorageContent;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
* Created by cjj on 2019-08-31.
*/
public interface StorageContentRepository extends JpaRepository<StorageContent, Long>, JpaSpecificationExecutor {

    Optional<StorageContent> findByFileKey(String key);
}