package com.zkthinke.modules.apsat.ship.service.mapper;

import com.zkthinke.mapper.EntityMapper;
import com.zkthinke.modules.apsat.ship.domain.ShipDetailSimple;
import com.zkthinke.modules.apsat.ship.service.dto.ShipDetailSimpleDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
* @author weicb
* @date 2020-10-15
*/
@Mapper(componentModel = "spring",uses = {},unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ShipDetailSimpleMapper extends EntityMapper<ShipDetailSimpleDTO, ShipDetailSimple> {

}