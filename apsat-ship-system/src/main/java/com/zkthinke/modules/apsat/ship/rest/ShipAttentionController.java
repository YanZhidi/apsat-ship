package com.zkthinke.modules.apsat.ship.rest;

import com.zkthinke.aop.log.Log;
import com.zkthinke.modules.apsat.ship.domain.Ship;
import com.zkthinke.modules.apsat.ship.domain.ShipAttention;
import com.zkthinke.modules.apsat.ship.service.ShipAttentionService;
import com.zkthinke.modules.apsat.ship.service.dto.ShipAttentionQueryCriteria;
import com.zkthinke.modules.apsat.ship.service.dto.ShipDTO;
import com.zkthinke.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @author weicb
* @date 2020-11-01
*/
@Api(tags = "船舶关注管理")
@RestController
@RequestMapping("api/shipAttention")
public class ShipAttentionController {

    @Autowired
    private ShipAttentionService shipAttentionService;

    //@Log("查询船舶关注(我的船队)")
    @ApiOperation(value = "查询船舶关注(我的船队)")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SHIPATTENTION_ALL','SHIPATTENTION_SELECT')")
    public ResponseEntity<List<ShipDTO>> getShipAttentions(ShipAttentionQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity(shipAttentionService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    //@Log("新增船舶关注")
    @ApiOperation(value = "新增船舶关注")
    @PutMapping(value = "/{shipId}")
    @PreAuthorize("hasAnyRole('ADMIN','SHIPATTENTION_ALL','SHIPATTENTION_CREATE')")
    public ResponseEntity create(@PathVariable Long shipId){
        ShipAttention shipAttention = new ShipAttention();
        Ship ship = new Ship();
        ship.setId(shipId);
        shipAttention.setShip(ship);
        shipAttention.setUserId(SecurityUtils.getUserId());
        return new ResponseEntity(shipAttentionService.create(shipAttention),HttpStatus.CREATED);
    }

    //@Log("船舶取消关注")
    @ApiOperation(value = "船舶取消关注")
    @DeleteMapping(value = "/cancel/{shipId}")
    @PreAuthorize("hasAnyRole('ADMIN','SHIPATTENTION_ALL','SHIPATTENTION_DELETE')")
    public ResponseEntity delete(@PathVariable Long shipId){
        shipAttentionService.deleteByShipId(shipId, SecurityUtils.getUserId());
        return new ResponseEntity(HttpStatus.OK);
    }
}