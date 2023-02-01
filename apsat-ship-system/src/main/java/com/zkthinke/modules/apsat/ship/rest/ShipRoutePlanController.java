package com.zkthinke.modules.apsat.ship.rest;


import cn.hutool.core.io.resource.ClassPathResource;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zkthinke.modules.apsat.ship.domain.DrawShipRoutePlanBO;
import com.zkthinke.modules.apsat.ship.domain.ShipRoutePlan;
import com.zkthinke.modules.apsat.ship.service.ShipRoutePlanService;
import com.zkthinke.modules.apsat.ship.service.dto.ShipRoutePlanDTO;
import com.zkthinke.modules.apsat.ship.service.dto.ShipRoutePlanQueryCriteria;
import com.zkthinke.modules.apsat.ship.service.mapper.ShipRoutePlanMapper;
import com.zkthinke.response.ResponseResult;
import com.zkthinke.utils.PageParam;
import com.zkthinke.utils.PageUtil;
import com.zkthinke.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @auther SONGXF
 * @date 2021/3/25 16:28
 */
@Api(tags = "船舶计划轨迹管理")
@RestController
@RequestMapping("api/shipRoutePlan")
@Slf4j
public class ShipRoutePlanController {

    @Autowired
    private ShipRoutePlanService shipRoutePlanService;

    @Autowired
    private ShipRoutePlanMapper shipRoutePlanMapper;

    //@Log("查询船舶计划轨迹列表")
    @ApiOperation(value = "查询船舶计划轨迹列表")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SHIP_ALL','SHIP_SELECT')")
    public ResponseEntity<List<ShipRoutePlanDTO>> getShipRoutePlan(@RequestBody PageParam<ShipRoutePlanQueryCriteria> param) {
        PageHelper.startPage(param.getPageNum(), param.getPageSize(),param.getOrderBy());

        List<ShipRoutePlan> shipRoutePlanList = shipRoutePlanService.findAll(param.getParam());
        List<ShipRoutePlanDTO> list = shipRoutePlanList.stream().map(s -> {
            ShipRoutePlanDTO shipRoutePlanDTO = shipRoutePlanMapper.toDto(s);
            return shipRoutePlanDTO;
        }).collect(Collectors.toList());

        PageInfo<ShipRoutePlan> pageInfo = new PageInfo<>(shipRoutePlanList);
        return new ResponseEntity(PageUtil.toPage(list, pageInfo.getTotal()), HttpStatus.OK);
    }

    //@Log("船舶计划轨迹状态变更")
    @ApiOperation(value = "船舶计划轨迹状态变更")
    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN','SHIP_ALL','SHIP_SELECT')")
    public ResponseEntity<List<ShipRoutePlanDTO>> updateShipRoutePlanState(@RequestBody ShipRoutePlanQueryCriteria criteria) {
        shipRoutePlanService.updateStateById(criteria);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    //@Log("船舶计划轨迹状态解析")
    @ApiOperation(value = "船舶计划轨迹状态解析")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SHIP_ALL','SHIP_SELECT')")
    public ResponseResult updateShipRoutePlanState(String id,String dataId) {
        Map<String,String> map =new HashMap<>();
        try{
            // 获取当前登录用户名称
            String createUser = SecurityUtils.getUsername();
            String planId = shipRoutePlanService.parseFileById(id, dataId, null,createUser);
            map.put("id",planId);
        }catch (Exception e){
            log.error("计划轨迹解析出错",e);
            return ResponseResult.fail("解析失败，文件内容错误" + e.getMessage());
        }
        return ResponseResult.ok(map);
    }

    @ApiOperation(value = "手绘计划航线")
    @PostMapping("/drawShipRoutePlan")
    @PreAuthorize("hasAnyRole('ADMIN','SHIP_ALL','SHIP_SELECT')")
    public ResponseResult drawShipRoutePlan(@RequestBody DrawShipRoutePlanBO reqBo) {
        Map<String,String> map =new HashMap<>();
        try {
            String planId = shipRoutePlanService.drawShipRoutePlanState(reqBo);
            map.put("id",planId);
        } catch (Exception e) {
            log.error("手绘计划航线 异常",e);
            return ResponseResult.fail(e.getMessage());
        }
        return ResponseResult.ok(map);
    }

    @ApiOperation(value = "删除计划航线")
    @PostMapping("/deleteShipRoutePlan")
    @PreAuthorize("hasAnyRole('ADMIN','SHIP_ALL','SHIP_SELECT')")
    public ResponseResult deleteShipRoutePlan(@RequestBody DrawShipRoutePlanBO reqBo) {
        try {
            shipRoutePlanService.deleteShipRoutePlan(reqBo);
        } catch (Exception e) {
            log.error("删除计划航线 异常",e);
            return ResponseResult.fail(e.getMessage());
        }
        return ResponseResult.ok();
    }

    @ApiOperation(value = "下载计划航线模板")
    @GetMapping("/downloadShipRoutePlanTemplate")
    @PreAuthorize("hasAnyRole('ADMIN','SHIP_ALL','SHIP_SELECT')")
    public void downloadShipRoutePlanTemplate(HttpServletResponse response, HttpServletRequest request) {
        try (InputStream input = new ClassPathResource("template/excel/计划航线模版.xls").getStream();
             OutputStream output = response.getOutputStream()) {
            String fileName = "计划航线模版.xls";
            String userAgent = request.getHeader("User-Agent").toLowerCase();
            if (userAgent.contains("msie") || userAgent.contains("trident")) {
                // 针对IE或者以IE为内核的浏览器：
                fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
            } else {
                // 非IE浏览器的处理：
                fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
            }
            response.setCharacterEncoding("UTF-8");
            response.setContentType("multipart/form-data;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
            response.setHeader("filename", fileName);
            response.setHeader("Access-Control-Expose-Headers", "filename");

            FileCopyUtils.copy(input,output);
        } catch (IOException e) {
            log.error("下载计划航线模板 异常：",e);
        }
    }
}
