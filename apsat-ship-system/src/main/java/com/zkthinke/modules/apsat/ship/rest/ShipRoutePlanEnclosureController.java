package com.zkthinke.modules.apsat.ship.rest;


import com.zkthinke.modules.apsat.ship.domain.ShipRoutePlanAlarm;
import com.zkthinke.modules.apsat.ship.domain.ShipRoutePlanDanger;
import com.zkthinke.modules.apsat.ship.domain.ShipRoutePlanDangerVO;
import com.zkthinke.modules.apsat.ship.domain.ShipWeatherDTO;
import com.zkthinke.modules.apsat.ship.service.ShipRoutePlanEnclosureService;
import com.zkthinke.modules.apsat.ship.service.dto.*;
import com.zkthinke.response.ResponseResult;
import com.zkthinke.utils.PageParam;
import com.zkthinke.utils.SecurityUtils;
import com.zkthinke.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @auther SONGXF
 * @date 2021/3/25 16:28
 */
@Api(tags = "船舶计划电子围栏")
@RestController
@RequestMapping("api/shipRoutePlanEnclosure")
@Slf4j
public class ShipRoutePlanEnclosureController {

    @Autowired
    private ShipRoutePlanEnclosureService shipRoutePlanEnclosureService;

    //@Log("查询船舶计划电子围栏")
    @ApiOperation(value = "查询船舶计划电子围栏")
    @GetMapping("enclosure")
    @PreAuthorize("hasAnyRole('ADMIN','SHIP_ALL','SHIP_SELECT')")
    public ResponseEntity<List<ShipRoutePlanDTO>> getShipRoutePlanEnclosure(ShipRoutePlanEnclosureQueryCriteria criteria) {

        List<ShipRoutePlanEnclosureDTO> list = shipRoutePlanEnclosureService.findAll(criteria);

        return new ResponseEntity(list, HttpStatus.OK);
    }

    //@Log("查询船舶计划轨迹明细")
    @ApiOperation(value = "根据船舶ID查询启用的计划轨迹明细")
    @GetMapping("detail")
    @PreAuthorize("hasAnyRole('ADMIN','SHIP_ALL','SHIP_SELECT')")
    public ResponseEntity<List<ShipRoutePlanDetailDTO>> getPlanDetail(ShipRoutePlanEnclosureQueryCriteria criteria) {

        List<ShipRoutePlanDetailDTO> list = shipRoutePlanEnclosureService.findAllDetail(criteria);

        return new ResponseEntity(list, HttpStatus.OK);
    }

    @ApiOperation(value = "根据计划ID查询计划轨迹明细")
    @GetMapping("detailByPlanId")
    @PreAuthorize("hasAnyRole('ADMIN','SHIP_ALL','SHIP_SELECT')")
    public ResponseEntity<List<ShipRoutePlanDetailDTO>> getPlanDetailByPlanId(String planId) {
        List<ShipRoutePlanDetailDTO> list = shipRoutePlanEnclosureService.getPlanDetailByPlanId(planId);
        return new ResponseEntity(list, HttpStatus.OK);
    }

    //@Log("查询预警信息列表")
    @ApiOperation(value = "查询预警信息列表")
    @PostMapping("alarm")
    @PreAuthorize("hasAnyRole('ADMIN','SHIP_ALL','SHIP_SELECT')")
    public ResponseEntity<List<ShipRoutePlanAlarmDTO>> getShipRoutePlanAlarm(@RequestBody PageParam<ShipRoutePlanAlarmQueryCriteria> param) {
        //获取当前用户所拥有的全部船舶id
        List<Long> shipIds = SecurityUtils.getShipIds();
        Map<String, Object> map = shipRoutePlanEnclosureService.findAlarmByParam(param, shipIds);

        return new ResponseEntity(map, HttpStatus.OK);
    }

    //@Log("查询预警前三条列表")
    @ApiOperation(value = "查询预警前三条列表")
    @PostMapping("alarmLimit/{shipId}")
    @PreAuthorize("hasAnyRole('ADMIN','SHIP_ALL','SHIP_SELECT')")
    public ResponseEntity<List<ShipRoutePlanAlarm>> getShipRoutePlanAlarm(@PathVariable("shipId") String shipId) {

        List<ShipRoutePlanAlarm> list = shipRoutePlanEnclosureService.findAlarmLimit(shipId);

        return new ResponseEntity(list, HttpStatus.OK);
    }

    //@Log("查询船舶计划危险区域")
    @ApiOperation(value = "查询船舶计划危险区域")
    @GetMapping("danger")
    @PreAuthorize("hasAnyRole('ADMIN','SHIP_ALL','SHIP_SELECT')")
    public ResponseEntity<List<ShipRoutePlanDanger>> getShipRoutePlanDanger(String shipId) {

        List<List<ShipRoutePlanDangerVO>> list_return = new ArrayList<>();

        List<ShipRoutePlanDanger> list = shipRoutePlanEnclosureService.findAllDanger(shipId);
        for (int i = 0; i < list.size(); i++) {
            List<ShipRoutePlanDangerVO> list_vo = new ArrayList<>();
            ShipRoutePlanDanger shipRoutePlanDanger = list.get(i);

            ShipRoutePlanDangerVO shipRoutePlanDangerVO_A = new ShipRoutePlanDangerVO();
            String longA = shipRoutePlanDanger.getLongA();
            String latA = shipRoutePlanDanger.getLatA();
            String count = shipRoutePlanDanger.getCount();
            shipRoutePlanDangerVO_A.setLongitude(longA);
            shipRoutePlanDangerVO_A.setLatitude(latA);
            shipRoutePlanDangerVO_A.setLevel(count);
            list_vo.add(shipRoutePlanDangerVO_A);

            ShipRoutePlanDangerVO shipRoutePlanDangerVO_B = new ShipRoutePlanDangerVO();
            String longB = shipRoutePlanDanger.getLongB();
            String latB = shipRoutePlanDanger.getLatB();
            shipRoutePlanDangerVO_B.setLongitude(longB);
            shipRoutePlanDangerVO_B.setLatitude(latB);
            shipRoutePlanDangerVO_B.setLevel(count);
            list_vo.add(shipRoutePlanDangerVO_B);

            ShipRoutePlanDangerVO shipRoutePlanDangerVO_C = new ShipRoutePlanDangerVO();
            String longC = shipRoutePlanDanger.getLongC();
            String latC = shipRoutePlanDanger.getLatC();
            shipRoutePlanDangerVO_C.setLongitude(longC);
            shipRoutePlanDangerVO_C.setLatitude(latC);
            shipRoutePlanDangerVO_C.setLevel(count);
            list_vo.add(shipRoutePlanDangerVO_C);

            ShipRoutePlanDangerVO shipRoutePlanDangerVO_D = new ShipRoutePlanDangerVO();
            String longD = shipRoutePlanDanger.getLongD();
            String latD = shipRoutePlanDanger.getLatD();
            shipRoutePlanDangerVO_D.setLongitude(longD);
            shipRoutePlanDangerVO_D.setLatitude(latD);
            shipRoutePlanDangerVO_D.setLevel(count);
            list_vo.add(shipRoutePlanDangerVO_D);

            list_return.add(list_vo);
        }

        return new ResponseEntity(list_return, HttpStatus.OK);
    }

    /**
     * 获取气象信息
     *
     * @param shipWeatherDto 获取气象信息数据传输对象
     * @return 统一返回数据对象
     */
    @ApiOperation(value = "获取气象信息")
    @PostMapping("/getWeather")
    @PreAuthorize("hasAnyRole('ADMIN','SHIP_ALL','SHIP_SELECT')")
    public ResponseResult getWeather(@RequestBody ShipWeatherDTO shipWeatherDto) {
        if (shipWeatherDto == null
                || StringUtils.isBlank(shipWeatherDto.getTime())
                || StringUtils.isBlank(shipWeatherDto.getLongitude())
                || StringUtils.isBlank(shipWeatherDto.getLatitude())) {
            return ResponseResult.fail("必填参数不允许为空");
        }
        return shipRoutePlanEnclosureService.getWeather(shipWeatherDto);
    }
}
