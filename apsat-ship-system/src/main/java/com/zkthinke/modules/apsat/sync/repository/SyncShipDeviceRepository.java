package com.zkthinke.modules.apsat.sync.repository;

import com.zkthinke.modules.apsat.sync.domain.SyncShipDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
* @author weicb
* @date 2020-10-28
*/
public interface SyncShipDeviceRepository extends JpaRepository<SyncShipDevice, Long>, JpaSpecificationExecutor {
}