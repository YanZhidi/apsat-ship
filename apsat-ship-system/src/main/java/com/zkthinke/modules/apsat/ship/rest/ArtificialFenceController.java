package com.zkthinke.modules.apsat.ship.rest;

import com.zkthinke.modules.apsat.ship.domain.ArtificialFenceBO;
import com.zkthinke.modules.apsat.ship.service.ArtificialFenceService;
import com.zkthinke.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "人工围栏")
@RestController
@RequestMapping("api/artificialFence")
public class ArtificialFenceController {

    @Autowired
    ArtificialFenceService artificialFenceService;

    @ApiOperation(value = "查询人工围栏列表")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SHIP_ALL','SHIP_SELECT')")
    public ResponseResult getArtificialFenceList() {
        List<ArtificialFenceBO> list = artificialFenceService.getArtificialFenceList();
        return ResponseResult.ok(list);
    }

}
