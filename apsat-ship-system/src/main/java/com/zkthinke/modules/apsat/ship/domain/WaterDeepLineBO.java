package com.zkthinke.modules.apsat.ship.domain;

import lombok.Data;

import java.util.List;

@Data
public class WaterDeepLineBO {
    private String name;
    private String depth;
    private List<WaterDeepPointBO> pointList;
}
