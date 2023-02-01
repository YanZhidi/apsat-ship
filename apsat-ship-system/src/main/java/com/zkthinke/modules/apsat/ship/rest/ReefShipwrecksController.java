package com.zkthinke.modules.apsat.ship.rest;

import com.zkthinke.modules.apsat.ship.domain.PointBO;
import com.zkthinke.modules.apsat.ship.service.ReefShipwrecksService;
import com.zkthinke.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "岛屿暗礁、沉船信息")
@RestController
@RequestMapping("api/reefShipwrecks")
public class ReefShipwrecksController {

    @Autowired
    private ReefShipwrecksService reefShipwrecksService;

    @ApiOperation(value = "查询岛屿暗礁点列表")
    @GetMapping("/reef")
    @PreAuthorize("hasAnyRole('ADMIN','SHIP_ALL','SHIP_SELECT')")
    public ResponseResult getReefList() {
        List<PointBO> list = reefShipwrecksService.getReefList();
        return ResponseResult.ok(list);
    }

    @ApiOperation(value = "查询沉船点列表")
    @GetMapping("/shipwrecks")
    @PreAuthorize("hasAnyRole('ADMIN','SHIP_ALL','SHIP_SELECT')")
    public ResponseResult getShipwrecksList() {
        List<PointBO> list = reefShipwrecksService.getShipwrecksList();
        return ResponseResult.ok(list);
    }

}
