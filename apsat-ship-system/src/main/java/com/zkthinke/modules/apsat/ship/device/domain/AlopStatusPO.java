package com.zkthinke.modules.apsat.ship.device.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AlopStatusPO {

    @ApiModelProperty(notes = "1号辅机运行")
    private String generatorRunning1;
    @ApiModelProperty(notes = "2号辅机运行")
    private String generatorRunning2;
    @ApiModelProperty(notes = "3号辅机运行")
    private String generatorRunning3;

    @ApiModelProperty(notes = "1号辅机滑油压力")
    private String alop1;
    @ApiModelProperty(notes = "2号辅机滑油压力")
    private String alop2;
    @ApiModelProperty(notes = "3号辅机滑油压力")
    private String alop3;
    @ApiModelProperty(notes = "数据采集时间")
    private Long collectTime;

}
