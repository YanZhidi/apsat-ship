package com.zkthinke.service.dto;

import com.zkthinke.annotation.Query;
import lombok.Data;

/**
* Created by cjj on 2019-08-31.
*/
@Data
public class StorageContentQueryCriteria{

    // 模糊
    @Query(type = Query.Type.INNER_LIKE)
    private String name;
}