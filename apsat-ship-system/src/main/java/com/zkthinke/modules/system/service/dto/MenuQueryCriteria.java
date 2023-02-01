package com.zkthinke.modules.system.service.dto;

import com.zkthinke.annotation.Query;
import lombok.Data;

/**
 * 公共查询类
 */
@Data
public class MenuQueryCriteria {

    // 多字段模糊
    @Query(type = Query.Type.INNER_LIKE)
    private String name;
}
