package com.zkthinke.modules.apsat.ship.service.mapper;

import com.zkthinke.mapper.EntityMapper;
import com.zkthinke.modules.apsat.ship.domain.ShipAttention;
import com.zkthinke.modules.apsat.ship.service.dto.ShipAttentionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
* @author weicb
* @date 2020-11-01
*/
@Mapper(componentModel = "spring",uses = {},unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ShipAttentionMapper extends EntityMapper<ShipAttentionDTO, ShipAttention> {

}