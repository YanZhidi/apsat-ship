package com.zkthinke.modules.apsat.ship.service.mapper;

import com.zkthinke.mapper.EntityMapper;
import com.zkthinke.modules.apsat.ship.domain.ShipDeviceSimple;
import com.zkthinke.modules.apsat.ship.service.dto.ShipDeviceSimpleDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
* @author weicb
* @date 2020-10-28
*/
@Mapper(componentModel = "spring",uses = {},unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ShipDeviceSimpleMapper extends EntityMapper<ShipDeviceSimpleDTO, ShipDeviceSimple> {

}