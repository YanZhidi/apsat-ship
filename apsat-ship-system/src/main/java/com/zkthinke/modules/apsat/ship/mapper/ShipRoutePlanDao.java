package com.zkthinke.modules.apsat.ship.mapper;

import com.zkthinke.modules.apsat.ship.domain.ShipRoutePlan;
import com.zkthinke.modules.apsat.ship.domain.ShipRoutePlanDetail;
import com.zkthinke.modules.apsat.ship.domain.ShipRoutePlanEnclosure;
import com.zkthinke.modules.apsat.ship.service.dto.ShipRoutePlanQueryCriteria;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @auther SONGXF
 * @date 2021/3/26 11:03
 */
@Mapper
public interface ShipRoutePlanDao {

    List<ShipRoutePlan> findAll(ShipRoutePlanQueryCriteria criteria);

    void updateStateById(ShipRoutePlanQueryCriteria criteria);

    String queryUrlById(String dataId);

    int addPlan(ShipRoutePlan shipRoutePlan);

    void addPlanDetail(List<ShipRoutePlanDetail> list);

    void addPlanEnclosure(List<ShipRoutePlanEnclosure> shipRoutePlanEnclosureList);

    void updateStateToClose(ShipRoutePlanQueryCriteria criteria);

    String queryShipNameById(String shipId);

    String queryShipVoyageNumberById(String shipId);

    List<String> queryPhoneByRoleLevel(String shipId);

    int updatePlan(ShipRoutePlan shipRoutePlan);

    void deletePlan(Long id);

    void deletePlanDetail(Long id);

    void deletePlanEnclosure(Long id);
}
