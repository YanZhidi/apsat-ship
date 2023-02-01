package com.zkthinke.modules.apsat.ship.rest;

import com.zkthinke.modules.apsat.ship.domain.ShipVdmBO;
import com.zkthinke.modules.apsat.ship.service.ShipVdmService;
import com.zkthinke.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/shipVdm")
public class ShipVdmController {

    @Autowired
    private ShipVdmService shipVdmService;

    @GetMapping(value = "/{shipId}")
    public ResponseResult getShipVdmListByShipId(@PathVariable String shipId) {

        List<ShipVdmBO> list = shipVdmService.getShipVdmListByShipId(shipId);

        return ResponseResult.ok(list);
    }


}
