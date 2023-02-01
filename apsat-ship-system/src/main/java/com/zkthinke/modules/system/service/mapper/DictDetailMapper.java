package com.zkthinke.modules.system.service.mapper;

import com.zkthinke.mapper.EntityMapper;
import com.zkthinke.modules.system.domain.DictDetail;
import com.zkthinke.modules.system.service.dto.DictDetailDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
* @author Zheng Jie
* @date 2019-04-10
*/
@Mapper(componentModel = "spring",uses = {},unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DictDetailMapper extends EntityMapper<DictDetailDTO, DictDetail> {

}