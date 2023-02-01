package com.zkthinke.modules.apsat.ship.service.dto;

import lombok.Data;

/**
 * @auther: SONGXF
 * @date: 2021/3/26 13:33
 */
@Data
public class ShipRoutePlanQueryCriteria {

    private String id;

    private String shipId;

    private String voyageNumber;

    private String state;

    private String createUser;

}