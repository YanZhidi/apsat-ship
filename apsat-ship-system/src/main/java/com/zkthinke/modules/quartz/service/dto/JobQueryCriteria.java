package com.zkthinke.modules.quartz.service.dto;

import lombok.Data;
import com.zkthinke.annotation.Query;

/**
 * @author Zheng Jie
 * @date 2019-6-4 10:33:02
 */
@Data
public class JobQueryCriteria {

    @Query(type = Query.Type.INNER_LIKE)
    private String jobName;

    @Query
    private Boolean isSuccess;
}
