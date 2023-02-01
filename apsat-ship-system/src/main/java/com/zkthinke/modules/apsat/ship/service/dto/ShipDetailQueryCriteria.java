package com.zkthinke.modules.apsat.ship.service.dto;

import lombok.Data;
import com.zkthinke.annotation.Query;

/**
* @author weicb
* @date 2020-10-15
*/
@Data
public class ShipDetailQueryCriteria{

    @Query
    private Long shipId;

    @Query(propName = "collectTime", type = Query.Type.GREATER_THAN)
    private Long collectTimeBegin;

    @Query(propName = "collectTime", type = Query.Type.LESS_THAN)
    private Long collectTimeEnd;
}