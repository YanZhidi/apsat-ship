package com.zkthinke.modules.system.service.dto;

import lombok.Data;
import com.zkthinke.annotation.Query;
import java.io.Serializable;
import java.util.Set;

/**
 * @author Zheng Jie
 * @date 2020-10-23
 */
@Data
public class UserQueryCriteria implements Serializable {

    @Query
    private Long id;

    // 多字段模糊
    @Query(blurry = "email,username")
    private String blurry;

    @Query(type = Query.Type.INNER_LIKE)
    private String username;

    @Query(type = Query.Type.INNER_LIKE)
    private String email;

    @Query
    private Boolean enabled;

}
