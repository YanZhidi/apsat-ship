package com.zkthinke.modules.apsat.ship.service.dto;

import lombok.Data;
import com.zkthinke.annotation.Query;

/**
* @author weicb
* @date 2020-11-01
*/
@Data
public class ShipAttentionQueryCriteria{

    private String name;

    @Query(propName = "id",joinName = "ship")
    private Long shipId;

    @Query
    private Long userId;
}