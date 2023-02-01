package com.zkthinke.modules.apsat.ship.rest;

import com.zkthinke.modules.apsat.ship.domain.TyphoonBO;
import com.zkthinke.modules.apsat.ship.domain.TyphoonTrackBO;
import com.zkthinke.modules.apsat.ship.service.TyphoonService;
import com.zkthinke.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "台风信息")
@RestController
@RequestMapping("api/typhoon")
public class TyphoonController {

    @Autowired
    private TyphoonService typhoonService;

    @ApiOperation(value = "查询台风列表")
    @GetMapping("/{year}")
    @PreAuthorize("hasAnyRole('ADMIN','SHIP_ALL','SHIP_SELECT')")
    public ResponseResult getTyphoonList(@PathVariable("year") String year) {
        List<TyphoonBO> list = typhoonService.getTyphoonList(year);
        return ResponseResult.ok(list);
    }

    @ApiOperation(value = "查询台风轨迹及预测轨迹")
    @GetMapping("/track/{code}")
    @PreAuthorize("hasAnyRole('ADMIN','SHIP_ALL','SHIP_SELECT')")
    public ResponseResult getTyphoonTrackList(@PathVariable("code") String code) {
        List<TyphoonTrackBO> list = typhoonService.getTyphoonTrackList(code);
        return ResponseResult.ok(list);
    }


    @ApiOperation(value = "查询已激活的台风及轨迹列表")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SHIP_ALL','SHIP_SELECT')")
    public ResponseResult getActivatedTyphoonDetailList() {
        List<TyphoonBO> list = typhoonService.getActivatedTyphoonDetailList();
        return ResponseResult.ok(list);
    }

}
