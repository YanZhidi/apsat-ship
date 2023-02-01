package com.zkthinke.modules.apsat.ship.domain;

import lombok.Data;

import java.util.List;

@Data
public class CustomTagPO {
    private Long id;
    private Long userId;
    private String name;
    private String type;
    private String radius;
    private List<CustomTagPointPO> pointList;
}
