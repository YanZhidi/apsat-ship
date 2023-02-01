package com.zkthinke.modules.apsat.ship.domain;

import lombok.Data;

@Data
public class CustomTagPointPO {
    private Long id;
    private Long tagId;
    private String longitude;
    private String latitude;
}
