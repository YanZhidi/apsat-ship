package com.zkthinke.modules.apsat.ship.rest;

import com.alibaba.fastjson.JSON;
import com.google.gson.JsonArray;
import com.zkthinke.aop.log.Log;
import com.zkthinke.modules.apsat.ship.domain.ShipDetail;
import com.zkthinke.modules.apsat.ship.service.ShipDetailService;
import com.zkthinke.modules.apsat.ship.service.dto.ShipDetailDTO;
import com.zkthinke.modules.apsat.ship.service.dto.ShipDetailQueryCriteria;
import com.zkthinke.modules.apsat.ship.service.dto.ShipDetailSimpleDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
* @author weicb
* @date 2020-10-15
*/
@Api(tags = "船舶详情")
@RestController
@RequestMapping("api/shipDetail")
public class ShipDetailController {

    @Autowired
    private ShipDetailService shipDetailService;

    //@Log("查询船舶航行信息(轨迹)列表")
    @ApiOperation(value = "查询船舶航行信息(轨迹)列表")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SHIP_ALL','SHIP_SELECT')")
    public ResponseEntity getShipDetails(ShipDetailQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity(shipDetailService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    //@Log("船舶详情信息")
    @ApiOperation(value = "船舶详情信息(含当前最新航行信息)")
    @GetMapping(value = "/{shipId}")
    public ResponseEntity<ShipDetailDTO> getById(@PathVariable Long shipId){
        return ResponseEntity.ok(shipDetailService.findLastByShipId(shipId));
    }

    //@Log("船速")
    @ApiOperation(value = "能效管理-船速")
    @GetMapping(value = "/speed/{shipId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shipId", value = "船舶 id"),
            @ApiImplicitParam(name = "collectTimeBegin", value = "起始时间"),
            @ApiImplicitParam(name = "collectTimeEnd", value = "截止时间")
    })
    public ResponseEntity shipFio(@PathVariable Long shipId,
                                  @RequestParam(value = "collectTimeBegin", required = false) Long collectTimeBegin,
                                  @RequestParam(value = "collectTimeEnd", required = false) Long collectTimeEnd){
        List<ShipDetailSimpleDTO> shipDetails = shipDetailService.findSimpleByShipId(shipId,
                Optional.ofNullable(collectTimeBegin),
                Optional.ofNullable(collectTimeEnd)
        );

        List<Map<String, Object>> list = shipDetails.stream().map(d -> {
            Map<String, Object> map = new HashMap();
            map.put("collectTime", d.getCollectTime());
            map.put("value", Optional.ofNullable(d.getGroundSpeed()).orElse("0"));
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @ApiOperation(value = "test")
    @GetMapping(value = "/test/{id}")
    public ResponseEntity findByIdAndLike( @PathVariable(value = "id") Long id,
                                           @RequestParam(value = "groundSpeed")String groundSpeed){
        List<ShipDetail> shipDetails = shipDetailService.findByIdOrLike(id, groundSpeed);

        //String jsonString = JSON.toJSONString(shipDetails);
        //return ResponseEntity.ok(jsonString);

        return ResponseEntity.ok(shipDetails);
    }

}