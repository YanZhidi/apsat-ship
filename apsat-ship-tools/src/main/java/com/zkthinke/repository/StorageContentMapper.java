package com.zkthinke.repository;

import com.zkthinke.domain.StorageContent;
import com.zkthinke.mapper.EntityMapper;
import com.zkthinke.service.dto.StorageContentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
* Created by cjj on 2019-08-31.
*/
@Mapper(componentModel = "spring",uses = {},unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StorageContentMapper extends EntityMapper<StorageContentDTO, StorageContent> {

}