package com.zkthinke.modules.apsat.ship.repository;

import com.zkthinke.modules.apsat.ship.domain.ShipAttention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
* @author weicb
* @date 2020-11-01
*/
public interface ShipAttentionRepository extends JpaRepository<ShipAttention, Long>, JpaSpecificationExecutor {

    @Query(value = "select * from t_ship_attention where ship_id in (:ids) and user_id=:userId" ,nativeQuery = true)
    List<ShipAttention> findAttentions(@Param(value = "ids")List<Long> ids,@Param(value = "userId") Long userId);
}