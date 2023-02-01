package com.zkthinke.modules.apsat.ship.repository;

import com.zkthinke.modules.apsat.ship.domain.Ship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
* @author weicb
* @date 2020-10-15
*/
public interface ShipRepository extends JpaRepository<Ship, Long>, JpaSpecificationExecutor {

    @Modifying
    @Transactional
    @Query(value = "update Ship set lastDeviceStime=?2 where id=?1")
    int updateLastDeviceStime(Long shipId, String requestTime);

}