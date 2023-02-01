package com.zkthinke.modules.apsat.ship.service.mapper;

import com.zkthinke.mapper.EntityMapper;
import com.zkthinke.modules.apsat.ship.domain.ShipRoutePlanDetail;
import com.zkthinke.modules.apsat.ship.domain.ShipRoutePlanEnclosure;
import com.zkthinke.modules.apsat.ship.service.dto.ShipRoutePlanDetailDTO;
import com.zkthinke.modules.apsat.ship.service.dto.ShipRoutePlanEnclosureDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * @author weicb
 * @date 2020-10-15
 */
@Mapper(componentModel = "spring", uses = {}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ShipRoutePlanDetailMapper extends EntityMapper<ShipRoutePlanDetailDTO, ShipRoutePlanDetail> {

}