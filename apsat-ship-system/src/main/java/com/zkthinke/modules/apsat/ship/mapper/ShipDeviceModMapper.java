package com.zkthinke.modules.apsat.ship.mapper;

import com.zkthinke.modules.apsat.ship.domain.ShipDeviceModBO;
import com.zkthinke.modules.apsat.ship.domain.ShipDeviceModPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShipDeviceModMapper {

    int insert(List<ShipDeviceModPO> record);

    List<ShipDeviceModBO> findShipDeviceModList(@Param("shipId") Long shipId, @Param("begin") Long collectTimeBegin, @Param("end") Long collectTimeEnd);
}