package com.zkthinke.modules.apsat.ship.service.mapper;

import com.zkthinke.mapper.EntityMapper;
import com.zkthinke.modules.apsat.ship.domain.ShipRoutePlan;
import com.zkthinke.modules.apsat.ship.domain.ShipRoutePlanAlarm;
import com.zkthinke.modules.apsat.ship.service.dto.ShipRoutePlanAlarmDTO;
import com.zkthinke.modules.apsat.ship.service.dto.ShipRoutePlanDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * @author weicb
 * @date 2020-10-15
 */
@Mapper(componentModel = "spring", uses = {}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ShipRoutePlanAlarmMapper extends EntityMapper<ShipRoutePlanAlarmDTO, ShipRoutePlanAlarm> {

}