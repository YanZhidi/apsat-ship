package com.zkthinke.modules.system.service.mapper;

import com.zkthinke.modules.system.domain.User;
import com.zkthinke.mapper.EntityMapper;
import com.zkthinke.modules.system.service.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * @author Zheng Jie
 * @date 2020-10-23
 */
@Mapper(componentModel = "spring",uses = {RoleMapper.class},unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper extends EntityMapper<UserDTO, User> {

}
