package com.zkthinke.modules.apsat.ship.detail.service;

import com.zkthinke.modules.apsat.ship.detail.BO.DetailVO;
import com.zkthinke.modules.apsat.ship.domain.ShipDetailBO;

import java.util.List;
import java.util.Map;

public interface DetailService {
    List<DetailVO> findSpeedByIdAndTime(Long shipId, Long collectTimeBegin, Long collectTimeEnd);

    List<Map<String,Object>> findSpendByIdAndTime(Long shipId, Long collectTimeBegin, Long collectTimeEnd);

    List<Map<String, Object>> findPstPower(Long shipId, Long collectTimeBegin, Long collectTimeEnd);

    List<ShipDetailBO> findShipDetailList(Long shipId, Long collectTimeBegin, Long collectTimeEnd );

}
