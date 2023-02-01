package com.zkthinke.modules.apsat.sync.service.mapper;

import com.zkthinke.mapper.EntityMapper;
import com.zkthinke.modules.apsat.sync.domain.SyncShipDevice;
import com.zkthinke.modules.apsat.sync.service.dto.SyncShipDeviceDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
* @author weicb
* @date 2020-10-28
*/
@Mapper(componentModel = "spring",uses = {},unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SyncShipDeviceMapper extends EntityMapper<SyncShipDeviceDTO, SyncShipDevice> {

}