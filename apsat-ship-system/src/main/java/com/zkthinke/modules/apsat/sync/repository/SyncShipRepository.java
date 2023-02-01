package com.zkthinke.modules.apsat.sync.repository;

import com.zkthinke.modules.apsat.sync.domain.SyncShip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
* @author weicb
* @date 2020-10-19
*/
public interface SyncShipRepository extends JpaRepository<SyncShip, Long>, JpaSpecificationExecutor {
}