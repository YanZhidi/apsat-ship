package com.zkthinke.modules.apsat.ship.service;

import com.zkthinke.modules.apsat.ship.domain.PointBO;

import java.util.List;

public interface ReefShipwrecksService {

    List<PointBO> getReefList();

    List<PointBO> getShipwrecksList();

}
