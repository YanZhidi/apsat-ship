package com.zkthinke.modules.system.service.mapper;

import com.zkthinke.modules.system.domain.Permission;
import com.zkthinke.mapper.EntityMapper;
import com.zkthinke.modules.system.service.dto.PermissionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * @author Zheng Jie
 * @date 2020-10-23
 */
@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PermissionMapper extends EntityMapper<PermissionDTO, Permission> {

}
