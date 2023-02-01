package com.zkthinke.modules.apsat.ship.mapper;

import com.zkthinke.modules.apsat.ship.domain.PointBO;

import java.util.List;

public interface ReefShipwrecksMapper {
    List<PointBO> getReefList();

    List<PointBO> getShipwrecksList();

}
