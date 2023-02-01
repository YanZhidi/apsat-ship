package com.zkthinke.modules.apsat.ship.rest;

import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.zkthinke.aop.log.Log;
import com.zkthinke.modules.apsat.ship.domain.Ship;
import com.zkthinke.modules.apsat.ship.domain.ShipDetailBO;
import com.zkthinke.modules.apsat.ship.domain.ShipPO;
import com.zkthinke.modules.apsat.ship.service.ShipService;
import com.zkthinke.modules.apsat.ship.service.dto.ShipDTO;
import com.zkthinke.modules.apsat.ship.service.dto.ShipQueryCriteria;
import com.zkthinke.response.ResponseResult;
import com.zkthinke.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
* @author weicb
* @date 2020-10-15
*/
@Api(tags = "船舶基本信息")
@RestController
@RequestMapping("api/ship")
public class ShipController {

    @Autowired
    private ShipService shipService;

    //@Log("查询船舶基本信息列表")
    @ApiOperation(value = "查询船舶基本信息列表")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SHIP_ALL','SHIP_SELECT')")
    public ResponseEntity<List<ShipDTO>> getShips(ShipQueryCriteria criteria, Pageable pageable) {
        criteria.setUserId(SecurityUtils.getUserId());
        List<Long> shipIds = SecurityUtils.getShipIds();
        //获取用户角色，若用户为超级管理员，则不需要带船舶的id去查询
        boolean isAdminRole = SecurityUtils.getAdminRole();
        if(!isAdminRole) {
            criteria.setShipIds(shipIds);
        }
        if(!isAdminRole && shipIds.size() <= 0 ) {
            List<ShipDTO> shipDTOs = new ArrayList<>();
            return new ResponseEntity<>(shipDTOs,HttpStatus.OK);
        }
        return new ResponseEntity(shipService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    //@Log("新增船舶基本信息")
    @ApiOperation(value = "新增船舶基本信息")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SHIP_ALL','SHIP_CREATE')")
    public ResponseEntity create(@Validated @RequestBody Ship resources){
        return new ResponseEntity(shipService.create(resources),HttpStatus.CREATED);
    }

    //@Log("修改船舶基本信息")
    @ApiOperation(value = "修改船舶基本信息")
    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN','SHIP_ALL','SHIP_EDIT')")
    public ResponseEntity update(@Validated @RequestBody Ship resources){
        shipService.update(resources);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    //@Log("删除船舶基本信息")
    @ApiOperation(value = "删除船舶基本信息")
    @DeleteMapping(value = "/ship/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SHIP_ALL','SHIP_DELETE')")
    public ResponseEntity delete(@PathVariable Long id){
        shipService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    //@Log("船舶基本信息")
    @ApiOperation(value = "船舶基本信息")
    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SHIP_ALL','SHIP_SELECT')")
    public ResponseEntity<ShipDTO> getById(@PathVariable Long id){
        return ResponseEntity.ok(shipService.findById(id));
    }

    //@Log("查询系统更新时间")
    @ApiOperation(value = "查询系统更新时间")
    @GetMapping(value = "/getZdaTime")
    @PreAuthorize("hasAnyRole('ADMIN','SHIP_ALL','SHIP_SELECT')")
    public ResponseResult<String> getZdaTime() {
        String zdaTime = shipService.getZdaTime();
        return ResponseResult.ok(zdaTime);
    }

    /**
     * 返回全部船舶
     * @return
     */
    @ApiOperation(value = "返回全部船舶数据")
    @GetMapping(value = "/tree")
    @PreAuthorize("hasAnyRole('ADMIN','SHIP_ALL','SHIP_CREATE','SHIP_EDIT','ROLES_SELECT','ROLES_ALL')")
    public ResponseEntity getShipTree() {
        return new ResponseEntity(shipService.getMenuTree(),HttpStatus.OK);
    }
}
