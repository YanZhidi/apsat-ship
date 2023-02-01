package com.zkthinke.modules.apsat.ship.repository;

import com.zkthinke.modules.apsat.ship.domain.ShipDeviceSimple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
* @author weicb
* @date 2020-10-28
*/
public interface ShipDeviceSimpleRepository extends JpaRepository<ShipDeviceSimple, Long>, JpaSpecificationExecutor {

}