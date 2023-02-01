package com.zkthinke.modules.apsat.ship.repository;

import com.zkthinke.modules.apsat.ship.domain.ShipDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
* @author weicb
* @date 2020-10-28
*/
public interface ShipDeviceRepository extends JpaRepository<ShipDevice, Long>, JpaSpecificationExecutor {

    @Query(value = "select * from t_ship_device where ship_id=?1 order by collect_time desc limit 1", nativeQuery = true)
    Optional<ShipDevice> findLastByShipId(Long shipId);

    ShipDevice findByIdAndShipId(Long id, Long shipId);
}