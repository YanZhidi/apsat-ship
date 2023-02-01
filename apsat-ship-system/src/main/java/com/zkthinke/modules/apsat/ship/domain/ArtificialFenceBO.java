package com.zkthinke.modules.apsat.ship.domain;

import lombok.Data;

import java.util.List;

@Data
public class ArtificialFenceBO {
    private String name;
    private List<ArtificialFencePointBO> pointList;
}
