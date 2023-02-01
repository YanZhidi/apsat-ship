package com.zkthinke.modules.apsat.ship.detail.controller;

import com.zkthinke.modules.apsat.ship.constant.ShipConstant;
import com.zkthinke.modules.apsat.ship.detail.BO.DetailVO;
import com.zkthinke.modules.apsat.ship.detail.service.DetailService;
import com.zkthinke.modules.apsat.ship.domain.ShipDetailBO;
import com.zkthinke.modules.apsat.ship.domain.ShipDetailPO;
import com.zkthinke.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Api(tags = "新船舶详情")
@RestController
@RequestMapping("api/new/shipDetail")
public class DetailController {

    @Autowired
    private DetailService detailService;

    //shipDetail表全部字段
    @GetMapping(value = "/{shipId}")
    public ResponseResult shipDetailList(@PathVariable("shipId") Long shipId,
                                         @RequestParam(value = "collectTimeBegin") Long collectTimeBegin,
                                         @RequestParam(value = "collectTimeEnd") Long collectTimeEnd){
        if (collectTimeBegin==null || collectTimeEnd == null){
            return ResponseResult.fail(ShipConstant.MISSSTARTENDTIMEFAILMSG);
        }
        List<ShipDetailBO> shipDetailList = detailService.findShipDetailList(shipId, collectTimeBegin, collectTimeEnd);
        return ResponseResult.ok(shipDetailList);
    }

    //@Log("船速")
    @ApiOperation(value = "能效管理-船速")
    @GetMapping(value = "/speed/{shipId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shipId", value = "船舶 id"),
            @ApiImplicitParam(name = "collectTimeBegin", value = "起始时间"),
            @ApiImplicitParam(name = "collectTimeEnd", value = "截止时间")
    })
    public ResponseResult shipFio(@PathVariable Long shipId,
                                  @RequestParam(value = "collectTimeBegin", required = false) Long collectTimeBegin,
                                  @RequestParam(value = "collectTimeEnd", required = false) Long collectTimeEnd){
        List<DetailVO> speedByIdAndTime = detailService.findSpeedByIdAndTime(shipId, collectTimeBegin, collectTimeEnd);
        List<Map<String, Object>> collect = speedByIdAndTime.stream().map(d -> {
            Map<String, Object> map = new HashMap<>();
            map.put("collectionTime", d.getCollectTime());
            map.put("value", Optional.ofNullable(d.getGroundSpeed()).orElse("0"));
            return map;
        }).collect(Collectors.toList());
        return ResponseResult.ok(collect);
    }

    //@Log("百海里燃油消耗")
    @ApiOperation(value = "能效管理-百海里燃油消耗")
    @GetMapping(value = "/spend/{shipId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shipId", value = "船舶 id"),
            @ApiImplicitParam(name = "collectTimeBegin", value = "起始时间"),
            @ApiImplicitParam(name = "collectTimeEnd", value = "截止时间")
    })
    public ResponseResult shipSpend(@PathVariable Long shipId,
                                  @RequestParam(value = "collectTimeBegin", required = false) Long collectTimeBegin,
                                  @RequestParam(value = "collectTimeEnd", required = false) Long collectTimeEnd){
        List<Map<String, Object>> spendByIdAndTime = detailService.findSpendByIdAndTime(shipId, collectTimeBegin, collectTimeEnd);

        return ResponseResult.ok(spendByIdAndTime);
    }

    //@Log("主机轴功率")
    @ApiOperation(value = "能效管理-主机轴功率")
    @GetMapping(value = "/pstPower/{shipId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shipId", value = "船舶 id"),
            @ApiImplicitParam(name = "collectTimeBegin", value = "起始时间"),
            @ApiImplicitParam(name = "collectTimeEnd", value = "截止时间")
    })
    public ResponseResult shipPstPower(@PathVariable Long shipId,
                                    @RequestParam(value = "collectTimeBegin", required = false) Long collectTimeBegin,
                                    @RequestParam(value = "collectTimeEnd", required = false) Long collectTimeEnd){
        List<Map<String, Object>> pstPower = detailService.findPstPower(shipId, collectTimeBegin, collectTimeEnd);

        return ResponseResult.ok(pstPower);
    }
}
