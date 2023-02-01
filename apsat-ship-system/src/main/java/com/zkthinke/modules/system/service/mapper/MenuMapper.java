package com.zkthinke.modules.system.service.mapper;

import com.zkthinke.modules.system.domain.Menu;
import com.zkthinke.mapper.EntityMapper;
import com.zkthinke.modules.system.service.dto.MenuDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * @author Zheng Jie
 * @date 2020-10-17
 */
@Mapper(componentModel = "spring",uses = {},unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MenuMapper extends EntityMapper<MenuDTO, Menu> {

}
