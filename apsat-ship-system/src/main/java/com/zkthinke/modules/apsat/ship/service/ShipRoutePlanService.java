package com.zkthinke.modules.apsat.ship.service;

import com.zkthinke.modules.apsat.ship.domain.DrawShipRoutePlanBO;
import com.zkthinke.modules.apsat.ship.domain.ShipRoutePlan;
import com.zkthinke.modules.apsat.ship.service.dto.ShipRoutePlanQueryCriteria;

import java.util.List;

/**
 * @auther: SONGXF
 * @date: 2021/3/25 16:32
 */
public interface ShipRoutePlanService {


    List<ShipRoutePlan> findAll(ShipRoutePlanQueryCriteria criteria);

    void updateStateById(ShipRoutePlanQueryCriteria criteria);

    String parseFileById(String id,String dataId,String planId,String createUser) throws Exception;

    String drawShipRoutePlanState(DrawShipRoutePlanBO reqBo) throws Exception;

    void deleteShipRoutePlan(DrawShipRoutePlanBO reqBo);
}