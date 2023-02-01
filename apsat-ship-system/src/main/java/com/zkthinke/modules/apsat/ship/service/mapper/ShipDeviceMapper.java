package com.zkthinke.modules.apsat.ship.service.mapper;

import com.zkthinke.mapper.EntityMapper;
import com.zkthinke.modules.apsat.ship.domain.ShipDevice;
import com.zkthinke.modules.apsat.ship.service.dto.ShipDeviceDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
* @author weicb
* @date 2020-10-28
*/
@Mapper(componentModel = "spring",uses = {},unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ShipDeviceMapper extends EntityMapper<ShipDeviceDTO, ShipDevice> {

}