package com.zkthinke.modules.apsat.ship.rest;


import com.zkthinke.modules.apsat.ship.domain.ShipAlarmMeritDefaultPO;
import com.zkthinke.modules.apsat.ship.domain.ShipAlarmMeritUpsertBO;
import com.zkthinke.modules.apsat.ship.domain.ShipAlarmMeritPO;
import com.zkthinke.modules.apsat.ship.service.ShipAlarmMeritService;
import com.zkthinke.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(tags = "船舶预警参数")
@RestController
@RequestMapping("api/shipAlarmMerit")
public class ShipAlarmMeritController {

    @Autowired
    private ShipAlarmMeritService shipAlarmMeritService;

    @ApiOperation(value = "查询预警参数配置列表")
    @GetMapping("/{shipId}")
    @PreAuthorize("hasAnyRole('ADMIN','SHIP_ALL','SHIP_SELECT')")
    public ResponseResult queryShipAlarmMerit(@PathVariable Integer shipId) {
        List<ShipAlarmMeritPO> list = shipAlarmMeritService.queryShipAlarmMerit(shipId);
        return ResponseResult.ok(list);
    }

    @ApiOperation(value = "修改预警参数配置列表")
    @PostMapping("/upsertShipAlarmMerit")
    @PreAuthorize("hasAnyRole('ADMIN','SHIP_ALL','SHIP_SELECT')")
    public ResponseResult upsertShipAlarmMerit(@RequestBody ShipAlarmMeritUpsertBO shipAlarmMeritUpsertBO) {
        shipAlarmMeritService.upsertShipAlarmMerit(shipAlarmMeritUpsertBO);
        return ResponseResult.ok();
    }

    @ApiOperation(value = "查询有效预警参数配置列表")
    @GetMapping("/queryValidShipAlarmMerit/{shipId}")
    @PreAuthorize("hasAnyRole('ADMIN','SHIP_ALL','SHIP_SELECT')")
    public ResponseResult queryValidShipAlarmMerit(@PathVariable Integer shipId) {
        List<ShipAlarmMeritDefaultPO> list = shipAlarmMeritService.queryValidShipAlarmMerit(shipId);
        return ResponseResult.ok(list);
    }

}
