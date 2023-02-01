package com.zkthinke.modules.apsat.ship.rest;


import com.zkthinke.modules.apsat.ship.domain.ShipAlarmThresholdPO;
import com.zkthinke.modules.apsat.ship.domain.ShipAlarmThresholdUpsertBO;
import com.zkthinke.modules.apsat.ship.service.ShipAlarmThresholdService;
import com.zkthinke.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(tags = "船舶预警阈值")
@RestController
@RequestMapping("api/shipAlarmThreshold")
public class ShipAlarmThresholdController {

    @Autowired
    private ShipAlarmThresholdService shipAlarmThresholdService;

    @ApiOperation(value = "获取预警阈值配置列表")
    @GetMapping("/{shipId}")
    @PreAuthorize("hasAnyRole('ADMIN','SHIP_ALL','SHIP_SELECT')")
    public ResponseResult queryShipAlarmThreshold(@PathVariable Integer shipId) {
        List<ShipAlarmThresholdPO> list = shipAlarmThresholdService.queryShipAlarmThreshold(shipId);
        return ResponseResult.ok(list);
    }

    @ApiOperation(value = "修改预警阈值配置列表")
    @PostMapping("/upsertShipAlarmThreshold")
    @PreAuthorize("hasAnyRole('ADMIN','SHIP_ALL','SHIP_SELECT')")
    public ResponseResult upsertShipAlarmThreshold(@RequestBody ShipAlarmThresholdUpsertBO shipAlarmThresholdUpsertBO) {
        shipAlarmThresholdService.upsertShipAlarmThreshold(shipAlarmThresholdUpsertBO);
        return ResponseResult.ok();
    }

}
