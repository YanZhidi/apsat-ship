package com.zkthinke.modules.apsat.ship.repository;

import com.zkthinke.modules.apsat.ship.domain.ShipDetailSimple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
* @author weicb
* @date 2020-10-15
*/
public interface ShipDetailSimpleRepository extends JpaRepository<ShipDetailSimple, Long>, JpaSpecificationExecutor {

}