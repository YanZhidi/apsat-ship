package com.zkthinke.modules.system.service.dto;

import lombok.Data;
import com.zkthinke.annotation.Query;

/**
 * 公共查询类
 */
@Data
public class PermissionQueryCriteria {

    // 多字段模糊
    @Query(blurry = "name,alias")
    private String blurry;


    @Query(type = Query.Type.INNER_LIKE)
    private String name;
}
