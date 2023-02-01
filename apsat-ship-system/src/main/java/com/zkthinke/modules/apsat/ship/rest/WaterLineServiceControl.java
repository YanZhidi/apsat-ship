package com.zkthinke.modules.apsat.ship.rest;

import com.alibaba.fastjson.JSONObject;
import com.zkthinke.modules.apsat.ship.domain.WaterDeepLineBO;
import com.zkthinke.modules.apsat.ship.domain.WaterLineBo;
import com.zkthinke.modules.apsat.ship.service.WaterLineService;
import com.zkthinke.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@Api(tags = "船舶水深等位线")
@RestController
@RequestMapping("api/waterLine")
public class WaterLineServiceControl {
    @Autowired
    WaterLineService waterLineService;

    @ApiOperation(value = "查询船舶计划轨迹列表")
    @PostMapping("getWaterLine")
    @PreAuthorize("hasAnyRole('ADMIN','SHIP_ALL','SHIP_SELECT')")
    public ResponseResult getwaterLine(@RequestBody WaterLineBo waterLineBo) {
        JSONObject result = waterLineService.waterService(waterLineBo.getWaterLine());
//        JsonArray jsonArray = new JsonArray();
//        stringListMap.forEach(x->{
//            JsonObject jsonObject = new JsonObject();
//            jsonArray.add();
//        });
//        jsonArray.add(stringListMap);
        return ResponseResult.ok(result);
    }

    @ApiOperation(value = "查询水深等深线")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SHIP_ALL','SHIP_SELECT')")
    public ResponseResult getWaterDeepLineList() {
        List<WaterDeepLineBO> list = waterLineService.getWaterDeepLineList();
        return ResponseResult.ok(list);
    }

}
