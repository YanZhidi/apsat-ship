package com.zkthinke.modules.apsat.ship.service.dto;

import lombok.Data;
import com.zkthinke.annotation.Query;

import java.util.List;

/**
* @author weicb
* @date 2020-10-15
*/
@Data
public class ShipQueryCriteria{

    private String name;

    private Long userId;

    @Query
    private String imoNumber;

    @Query
    private String mmsiNumber;

    private List<Long> shipIds;
}
