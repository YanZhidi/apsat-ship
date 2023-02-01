package com.zkthinke.modules.apsat.ship.device.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RevolutionGroundSpeedBO {

    @ApiModelProperty(notes = "对地航速")
    private String groundSpeed;

    @ApiModelProperty(notes = "主机转速")
    private String revolutionSpeed;

    @ApiModelProperty(notes = "数据采集时间")
    private Long collectTime;


}
