package com.zkthinke.modules.apsat.ship.repository;

import com.zkthinke.modules.apsat.ship.domain.ShipLogError;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ShipLogErrorRepository extends JpaRepository<ShipLogError, Long>, JpaSpecificationExecutor {
}
