package com.zkthinke.modules.system.rest;

import cn.hutool.core.lang.Dict;
import com.zkthinke.aop.log.Log;
import com.zkthinke.modules.system.domain.Role;
import com.zkthinke.modules.system.service.RoleService;
import com.zkthinke.modules.system.service.dto.RoleQueryCriteria;
import com.zkthinke.modules.system.service.dto.RoleSmallDTO;
import com.zkthinke.exception.BadRequestException;
import com.zkthinke.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Zheng Jie
 * @date 2020-10-03
 */
@RestController
@RequestMapping("api")
@Api(tags = "角色管理")
public class RoleController {

    @Autowired
    private RoleService roleService;

    private static final String ENTITY_NAME = "role";

    /**
     * 获取单个role
     * @param id
     * @return
     */
    @GetMapping(value = "/roles/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ROLES_ALL','ROLES_SELECT')")
    @ApiOperation(value = "获取单个角色信息")
    public ResponseEntity getRoles(@PathVariable Long id){
        return new ResponseEntity(roleService.findById(id), HttpStatus.OK);
    }

    /**
     * 返回全部的角色，新增用户时下拉选择
     * @return
     */
    @GetMapping(value = "/roles/all")
    @PreAuthorize("hasAnyRole('ADMIN','ROLES_ALL','USER_ALL','USER_CREATE','USER_EDIT')")
    @ApiOperation(value = "返回全部的角色，新增用户时下拉选择")
    public ResponseEntity getAll(@PageableDefault(value = 2000, sort = {"level"}, direction = Sort.Direction.ASC) Pageable pageable){
        return new ResponseEntity(roleService.queryAll(pageable),HttpStatus.OK);
    }

    //@Log("查询角色")
    @GetMapping(value = "/roles")
    @PreAuthorize("hasAnyRole('ADMIN','ROLES_ALL','ROLES_SELECT')")
    @ApiOperation(value = "查询角色")
    public ResponseEntity getRoles(RoleQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity(roleService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @GetMapping(value = "/roles/level")
    public ResponseEntity getLevel(){
        List<Integer> levels = roleService.findByUsers_Id(SecurityUtils.getUserId()).stream().map(
            RoleSmallDTO::getLevel).collect(Collectors.toList());
        return new ResponseEntity(Dict.create().set("level", Collections.min(levels)),HttpStatus.OK);
    }

    //@Log("新增角色")
    @PostMapping(value = "/roles")
    @PreAuthorize("hasAnyRole('ADMIN','ROLES_ALL','ROLES_CREATE')")
    @ApiOperation(value = "新增角色")
    public ResponseEntity create(@Validated @RequestBody Role resources){
        if (resources.getId() != null) {
            throw new BadRequestException("A new "+ ENTITY_NAME +" cannot already have an ID");
        }
        return new ResponseEntity(roleService.create(resources),HttpStatus.CREATED);
    }

    //@Log("修改角色")
    @PutMapping(value = "/roles")
    @PreAuthorize("hasAnyRole('ADMIN','ROLES_ALL','ROLES_EDIT')")
    @ApiOperation(value = "修改角色")
    public ResponseEntity update(@Validated(Role.Update.class) @RequestBody Role resources){
        roleService.update(resources);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    //@Log("修改角色权限")
    @PutMapping(value = "/roles/permission")
    @PreAuthorize("hasAnyRole('ADMIN','ROLES_ALL','ROLES_EDIT')")
    @ApiOperation(value = "修改角色权限")
    public ResponseEntity updatePermission(@RequestBody Role resources){
        roleService.updatePermission(resources,roleService.findById(resources.getId()));
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    //@Log("修改角色菜单")
    @PutMapping(value = "/roles/menu")
    @PreAuthorize("hasAnyRole('ADMIN','ROLES_ALL','ROLES_EDIT')")
    @ApiOperation(value = "修改角色菜单")
    public ResponseEntity updateMenu(@RequestBody Role resources){
        roleService.updateMenu(resources,roleService.findById(resources.getId()));
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    //@Log("修改角色船舶")
    @PutMapping(value = "/roles/ship")
    @PreAuthorize("hasAnyRole('ADMIN','ROLES_ALL','ROLES_EDIT')")
    @ApiOperation(value = "修改角色船舶")
    public ResponseEntity updateShip(@RequestBody Role resources){
        roleService.updateShip(resources,roleService.findById(resources.getId()));
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }


    //@Log("删除角色")
    @DeleteMapping(value = "/roles/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ROLES_ALL','ROLES_DELETE')")
    @ApiOperation(value = "删除角色")
    public ResponseEntity delete(@PathVariable Long id){
        roleService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
