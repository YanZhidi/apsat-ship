package com.zkthinke.modules.apsat.ship.mapper;

import com.zkthinke.modules.apsat.ship.domain.ShipRoutePlanDanger;
import com.zkthinke.modules.apsat.ship.domain.ShipRoutePlanDetail;
import com.zkthinke.modules.apsat.ship.domain.ShipRoutePlanEnclosure;
import com.zkthinke.modules.apsat.ship.service.dto.ShipRoutePlanEnclosureQueryCriteria;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @auther SONGXF
 * @date 2021/3/26 11:03
 */
@Mapper
public interface ShipRoutePlanEnclosureDao {

    List<ShipRoutePlanEnclosure> findAll(ShipRoutePlanEnclosureQueryCriteria criteria);

    List<ShipRoutePlanDetail> findAllDetail(ShipRoutePlanEnclosureQueryCriteria criteria);

    List<ShipRoutePlanDetail> getPlanDetailByPlanId(String planId);

    List<ShipRoutePlanDanger> findAllDanger(String shipId);
}
