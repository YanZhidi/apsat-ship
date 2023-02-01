package com.zkthinke.modules.apsat.ship.service;

import com.zkthinke.modules.apsat.ship.domain.ShipVdmBO;

import java.util.List;

public interface ShipVdmService {
    List<ShipVdmBO> getShipVdmListByShipId(String shipId);
}
