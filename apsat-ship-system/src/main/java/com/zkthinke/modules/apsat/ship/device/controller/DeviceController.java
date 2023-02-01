package com.zkthinke.modules.apsat.ship.device.controller;

import com.zkthinke.modules.apsat.ship.constant.ShipConstant;
import com.zkthinke.modules.apsat.ship.device.domain.*;
import com.zkthinke.modules.apsat.ship.device.service.DeviceService;
import com.zkthinke.modules.apsat.ship.domain.*;
import com.zkthinke.modules.apsat.ship.repository.ShipDeviceRepository;
import com.zkthinke.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Api(tags = "新设备详情")
@RestController
@RequestMapping("api/new/shipDevice")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private ShipDeviceRepository shipDeviceRepository;

    //shipDevice表全部字段
    @GetMapping(value = "/{shipId}")
    public ResponseResult shipDeviceList(@PathVariable Long shipId,
                                         @RequestParam(value = "collectTimeBegin") Long collectTimeBegin,
                                         @RequestParam(value = "collectTimeEnd") Long collectTimeEnd){
        if (collectTimeBegin==null || collectTimeEnd == null){
            return ResponseResult.fail(ShipConstant.MISSSTARTENDTIMEFAILMSG);
        }
        List<ShipDeviceBO> list = deviceService.findShipDeviceList(shipId, collectTimeBegin, collectTimeEnd);
        return ResponseResult.ok(list);
    }

    //shipDeviceMod表全部字段
    @GetMapping(value = "/shipDeviceMod/{shipId}")
    public ResponseResult shipDeviceModList(@PathVariable Long shipId,
                                         @RequestParam(value = "collectTimeBegin") Long collectTimeBegin,
                                         @RequestParam(value = "collectTimeEnd") Long collectTimeEnd){
        if (collectTimeBegin==null || collectTimeEnd == null){
            return ResponseResult.fail(ShipConstant.MISSSTARTENDTIMEFAILMSG);
        }
        List<ShipDeviceModBO> list = deviceService.findShipDeviceModList(shipId, collectTimeBegin, collectTimeEnd);
        return ResponseResult.ok(list);
    }

    //@Log("船舶燃油耗能")
    @ApiOperation(value = "主机、辅机、锅炉燃油耗能、总燃油耗能")
    @GetMapping(value = "/findShipFuelData/{shipId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shipId", value = "船舶 id"),
            @ApiImplicitParam(name = "collectTimeBegin", value = "起始时间"),
            @ApiImplicitParam(name = "collectTimeEnd", value = "截止时间")
    })
    public ResponseEntity shipFuelConsumeData(@PathVariable Long shipId,
                                              @RequestParam(value = "collectTimeBegin", required = false) Long collectTimeBegin,
                                              @RequestParam(value = "collectTimeEnd", required = false) Long collectTimeEnd) {

        Map<Long, Map<String, Object>> map = deviceService.findShipFuelData(shipId, collectTimeBegin, collectTimeEnd);
        List<Map<String, Object>> list = new ArrayList<>();
        map.forEach((k, v) -> list.add(v));
        list.sort((o1, o2) -> {
            Long o1Time = Long.parseLong(o1.get("collectTime").toString());
            Long o2Time = Long.parseLong(o2.get("collectTime").toString());
            if (!o1Time.equals(o2Time)) {
                return o1Time.compareTo(o2Time);
            }
            return 0;
        });

        return ResponseEntity.ok(list);
    }

    //@Log("实时船舶燃油消耗")
    @ApiOperation(value = "实时船舶燃油消耗")
    @GetMapping(value = "/realTimeFuelData/{shipId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shipId", value = "船舶 id")
    })
    public ResponseEntity realTimeFuelConsumptionData(@PathVariable Long shipId) {
        DeviceRealTimeBO deviceRealTimeBO = deviceService.realTimeFuelConsumptionData(shipId);
        return ResponseEntity.ok(deviceRealTimeBO);
    }

    //@Log("实时船舶设备状态")
    @ApiOperation(value = "实时船舶设备状态")
    @GetMapping(value = "/realTimeEquipmentStatus/{shipId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shipId", value = "船舶 id")
    })
    public ResponseEntity realTimeEquipmentStatus(@PathVariable Long shipId) {
        ShipDevicePO shipDevicePO = deviceService.realTimeEquipmentStatus(shipId);
        return ResponseEntity.ok(shipDevicePO);
    }

    //@Log("智能机舱")
    @ApiOperation(value = "智能机舱")
    @GetMapping(value = "/smartCabin/{shipId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shipId", value = "船舶 id"),
            @ApiImplicitParam(name = "collectTimeBegin", value = "起始时间"),
            @ApiImplicitParam(name = "collectTimeEnd", value = "截止时间")
    })
    public ResponseEntity smartCabin(@PathVariable Long shipId,
                                     @RequestParam(value = "collectTimeBegin", required = false) Long collectTimeBegin,
                                     @RequestParam(value = "collectTimeEnd", required = false) Long collectTimeEnd) {
        SmartCabinBO smartCabinBO = deviceService.smartCabin(shipId, collectTimeBegin, collectTimeEnd);

        return ResponseEntity.ok(smartCabinBO);
    }

    @ApiOperation(value = "主机详情")
    @GetMapping(value = "/hostDetails/{shipId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shipId", value = "船舶 id")
    })
    public ResponseEntity hostDetails(@PathVariable Long shipId) {
        HostDetailsBO hostDetailsBO = deviceService.hostDetails(shipId);

        return ResponseEntity.ok(hostDetailsBO);
    }


    //@Log("主机状态-转速、航速")
    @ApiOperation(value = "主机状态-转速、航速")
    @GetMapping(value = "/rotationSpeed/{shipId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shipId", value = "船舶 id"),
            @ApiImplicitParam(name = "collectTimeBegin", value = "起始时间"),
            @ApiImplicitParam(name = "collectTimeEnd", value = "截止时间")
    })
    public ResponseEntity rotationSpeed(@PathVariable Long shipId,
                                        @RequestParam(value = "collectTimeBegin", required = false) Long collectTimeBegin,
                                        @RequestParam(value = "collectTimeEnd", required = false) Long collectTimeEnd) {
        List<RevolutionGroundSpeedBO> list = deviceService.findRotationGroundSpeedList(shipId, collectTimeBegin, collectTimeEnd);

        return ResponseEntity.ok(list);
    }

    //@Log("辅机状态-滑油压力")
    @ApiOperation(value = "辅机状态-滑油压力")
    @GetMapping(value = "/alop/{shipId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shipId", value = "船舶 id"),
            @ApiImplicitParam(name = "collectTimeBegin", value = "起始时间"),
            @ApiImplicitParam(name = "collectTimeEnd", value = "截止时间")
    })
    public ResponseEntity alop(@PathVariable Long shipId,
                               @RequestParam(value = "collectTimeBegin", required = false) Long collectTimeBegin,
                               @RequestParam(value = "collectTimeEnd", required = false) Long collectTimeEnd) {
        AlopBO alopBO = deviceService.findAlopList(shipId, collectTimeBegin, collectTimeEnd);

        return ResponseEntity.ok(alopBO);
    }
}
