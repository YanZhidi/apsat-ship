package com.zkthinke.modules.apsat.ship.rest;

import com.zkthinke.aop.log.Log;
import com.zkthinke.modules.apsat.ship.service.ShipDeviceService;
import com.zkthinke.modules.apsat.ship.service.dto.ShipDeviceDTO;
import com.zkthinke.modules.apsat.ship.service.dto.ShipDeviceQueryCriteria;
import com.zkthinke.modules.apsat.ship.service.dto.ShipDeviceSimpleDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
* @author weicb
* @date 2020-10-28
*/
@Api(tags = "船舶设备(能效)信息")
@RestController
@RequestMapping("api/shipDevice")
public class ShipDeviceController {

    @Autowired
    private ShipDeviceService shipDeviceService;

    //@Log("查询船舶设备(能效)信息")
    @ApiOperation(value = "查询船舶设备(能效)信息")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SHIP_ALL','SHIP_SELECT')")
    public ResponseEntity getShipDevices(ShipDeviceQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity(shipDeviceService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    //@Log("船舶当前设备(能效)信息")
    @ApiOperation(value = "船舶当前设备(能效)信息")
    @GetMapping(value = "/{shipId}")
    public ResponseEntity<ShipDeviceDTO> getById(@PathVariable Long shipId){
        return ResponseEntity.ok(shipDeviceService.findLastByShipId(shipId));
    }

    //@Log("能效管理-燃油消耗情况")
    @ApiOperation(value = "能效管理-燃油消耗情况")
    @GetMapping(value = "/ship-fio/{shipId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shipId", value = "船舶 id"),
            @ApiImplicitParam(name = "collectTimeBegin", value = "起始时间"),
            @ApiImplicitParam(name = "collectTimeEnd", value = "截止时间")
    })
    public ResponseEntity shipFio(@PathVariable Long shipId,
                                  @RequestParam(value = "collectTimeBegin", required = false) Long collectTimeBegin,
                                  @RequestParam(value = "collectTimeEnd", required = false) Long collectTimeEnd){
        ShipDeviceDTO sd = shipDeviceService.findLastByShipId(shipId);
        // 保留 7 位
        DecimalFormat df = new DecimalFormat("0.00");
        // 主机燃油消耗=主机燃油进口流量-主机燃油出口流量
        Double mefio = getSub(sd.getMefif(), "Ton/h", sd.getMefof());
        // 辅机燃油消耗=辅机燃油进口流量-辅机燃油出口流量
        Double sefio = getSub(sd.getSefif(), "M3/H", sd.getSefof());
        // TODO 锅炉燃油消耗=锅炉燃油进口流量-锅炉燃油出口流量
//        Double bfoio = getSub(sd.getBfoif(), "Ton/h", sd.getBfoof());
        Double bfoio = getVal("Ton/h", sd.getBfoof());
//        for (ShipDeviceDTO sd : shipDevices) {
//            // 主机燃油能耗
//            mefio += getSub(sd.getMefit(), "Deg", sd.getMefof());
//            // 辅机燃油能耗
//            sefio += getSub(sd.getSefif(), "M3/h", sd.getSefof());
//            // 锅炉燃油能耗
//            bfoio +=  getSub(sd.getBfoif(), "M3/h", sd.getBfoof());
//        }

        Map<String, Object> map =new HashMap();
        map.put("mefio", df.format(mefio));
        map.put("sefio", df.format(sefio));
        map.put("bfoio", df.format(bfoio));
//        map.put("totalio", df.format(mefio + sefio + bfoio));
        map.put("totalio", df.format(new Double(0)));
        return ResponseEntity.ok(map);
    }

    //@Log("主机负荷")
    @ApiOperation(value = "能效管理-主机负荷、主机燃油指数")
    @GetMapping(value = "/host-load/{shipId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shipId", value = "船舶 id"),
            @ApiImplicitParam(name = "collectTimeBegin", value = "起始时间"),
            @ApiImplicitParam(name = "collectTimeEnd", value = "截止时间")
    })
    public ResponseEntity hostLoad(@PathVariable Long shipId,
                                  @RequestParam(value = "collectTimeBegin", required = false) Long collectTimeBegin,
                                  @RequestParam(value = "collectTimeEnd", required = false) Long collectTimeEnd){
        List<ShipDeviceSimpleDTO> shipDevices = shipDeviceService.findSimpleByShipId(shipId,
                Optional.ofNullable(collectTimeBegin),
                Optional.ofNullable(collectTimeEnd)
        );

        List<Map<String, Object>> list = shipDevices.stream().map(d -> {
            Map<String, Object> map = new HashMap();
            map.put("collectTime", d.getCollectTime());
            map.put("hostLoad", Optional.ofNullable(d.getHostLoad()).orElse("0%"));
            map.put("mefc", Optional.ofNullable(d.getMefc()).orElse("0%"));
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    //@Log("主机燃油指数")
    @ApiOperation(value = "能效管理-主机、辅机、锅炉燃油耗能、总燃油耗能态势")
    @GetMapping(value = "/mefc/{shipId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shipId", value = "船舶 id"),
            @ApiImplicitParam(name = "collectTimeBegin", value = "起始时间"),
            @ApiImplicitParam(name = "collectTimeEnd", value = "截止时间")
    })
    public ResponseEntity mefc(@PathVariable Long shipId,
                                   @RequestParam(value = "collectTimeBegin", required = false) Long collectTimeBegin,
                                   @RequestParam(value = "collectTimeEnd", required = false) Long collectTimeEnd){
        List<ShipDeviceSimpleDTO> shipDevices = shipDeviceService.findSimpleByShipId(shipId,
                Optional.ofNullable(collectTimeBegin),
                Optional.ofNullable(collectTimeEnd)
        );
        // 保留 7 位
        DecimalFormat df = new DecimalFormat("0.00");
        List<Map<String, Object>> list = shipDevices.stream().map(sd -> {
            Map<String, Object> map = new HashMap();
            map.put("collectTime", sd.getCollectTime());
            // 主机燃油能耗
            Double mefio = getSub(sd.getMefif(), "Ton/h", sd.getMefof());
            map.put("mefio", df.format(mefio));
            // 辅机燃油能耗
            Double sefio = getSub(sd.getSefif(), "M3/H", sd.getSefof());
            map.put("sefio", df.format(sefio));
            // TODO 锅炉燃油能耗
//            Double bfoio = getSub(sd.getBfoif(), "Ton/h", sd.getBfoof());
            Double bfoio = getVal("Ton/h", sd.getBfoof());
            map.put("bfoio", df.format(bfoio));
//            map.put("totalio", df.format(mefio + sefio + bfoio));
            map.put("totalio", df.format(new Double(0)));
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    /**
     * 带单位字符串求差
     * @param v1 减数
     * @param unit 单位
     * @param v2 被减数
     * @return 减数或者被减数为空当成 0 处理,返回计算结果不带单位
     */
    private Double getSub(String v1, String unit, String v2) {
        int length = unit.length();
        String defaultVal = "0" + unit;
        String subtrahendStr = v1 == null || "".equals(v1) ? defaultVal : v1;
        Double subtrahend = Double.valueOf(subtrahendStr.substring(0, subtrahendStr.length() - length));
        String minuendStr = v2 == null || "".equals(v2) ? defaultVal : v2;
        Double minuend = Double.valueOf(minuendStr.substring(0, minuendStr.length() - length));
        return subtrahend - minuend;
    }

    /**
     * 带单位字符串求差
     * @param unit 单位
     * @param v2 被减数
     * @return 减数或者被减数为空当成 0 处理,返回计算结果不带单位
     */
    private static Double getVal(String unit, String v2) {
        int length = unit.length();
        String defaultVal = "0" + unit;
        String minuendStr = v2 == null || "".equals(v2) ? defaultVal : v2;
        return Double.valueOf(minuendStr.substring(0, minuendStr.length() - length));
    }

}