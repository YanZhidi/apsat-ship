package com.zkthinke.modules.apsat.ship.service.mapper;

import com.zkthinke.mapper.EntityMapper;
import com.zkthinke.modules.apsat.ship.domain.ShipDetail;
import com.zkthinke.modules.apsat.ship.service.dto.ShipDetailDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
* @author weicb
* @date 2020-10-15
*/
@Mapper(componentModel = "spring",uses = {},unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ShipDetailMapper extends EntityMapper<ShipDetailDTO, ShipDetail> {

}