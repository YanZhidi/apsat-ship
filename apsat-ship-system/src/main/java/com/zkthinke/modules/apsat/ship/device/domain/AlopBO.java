package com.zkthinke.modules.apsat.ship.device.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class AlopBO {

    @ApiModelProperty(notes = "1号辅机运行")
    private String generatorRunning1;
    @ApiModelProperty(notes = "2号辅机运行")
    private String generatorRunning2;
    @ApiModelProperty(notes = "3号辅机运行")
    private String generatorRunning3;

    @ApiModelProperty(notes = "1号辅机滑油压力")
    private List<String> alop1List;
    @ApiModelProperty(notes = "2号辅机滑油压力")
    private List<String> alop2List;
    @ApiModelProperty(notes = "3号辅机滑油压力")
    private List<String> alop3List;
    @ApiModelProperty(notes = "数据采集时间")
    private List<Long> collectTime;


}
