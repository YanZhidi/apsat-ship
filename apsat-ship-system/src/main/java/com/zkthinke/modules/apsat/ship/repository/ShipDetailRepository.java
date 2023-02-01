package com.zkthinke.modules.apsat.ship.repository;

import com.zkthinke.modules.apsat.ship.domain.ShipDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
* @author weicb
* @date 2020-10-15
*/
public interface ShipDetailRepository extends JpaRepository<ShipDetail, Long>, JpaSpecificationExecutor {

}