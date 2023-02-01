package com.zkthinke.modules.system.rest;

import com.zkthinke.aop.log.Log;
import com.zkthinke.exception.BadRequestException;
import com.zkthinke.modules.system.domain.DictDetail;
import com.zkthinke.modules.system.service.DictDetailService;
import com.zkthinke.modules.system.service.DictService;
import com.zkthinke.modules.system.service.dto.DictDetailQueryCriteria;
import com.zkthinke.response.ResponseResult;
import com.zkthinke.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Zheng Jie
 * @date 2019-04-10
 */
@RestController
@RequestMapping("api/dictDetail")
@Api(tags = "管理端-数据字典")
public class DictDetailController {

    @Autowired
    private DictDetailService dictDetailService;

    @Autowired
    private DictService dictService;

    private static final String ENTITY_NAME = "dictDetail";

    //@Log("查询字典项详情列表")
    @ApiOperation(value = "查询字典项详情列表")
    @GetMapping
    public ResponseEntity getDictDetails(DictDetailQueryCriteria criteria,
                                         @PageableDefault(value = 10, sort = {"sort"}, direction = Sort.Direction.ASC) Pageable pageable){
        return new ResponseEntity(dictDetailService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    //@Log("查询多个字典详情")
    @GetMapping(value = "/map")
    public ResponseEntity getDictDetailMaps(DictDetailQueryCriteria criteria,
                                            @PageableDefault(value = 10, sort = {"sort"}, direction = Sort.Direction.ASC) Pageable pageable){
        String[] names = criteria.getDictName().split(",");
        Map map = new HashMap(names.length);
        for (String name : names) {
            criteria.setDictName(name);
            map.put(name,dictDetailService.queryAll(criteria,pageable).get("content"));
        }
        return new ResponseEntity(map,HttpStatus.OK);
    }

    //@Log("新增字典详情")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','DICT_ALL','DICT_CREATE')")
    @ApiOperation(value = "新增字典详情")
    public ResponseEntity create(@Validated @RequestBody DictDetail resources){
        if (resources.getId() != null) {
            throw new BadRequestException("A new "+ ENTITY_NAME +" cannot already have an ID");
        }
        return new ResponseEntity(dictDetailService.create(resources),HttpStatus.CREATED);
    }

    //@Log("修改字典详情")
    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN','DICT_ALL','DICT_EDIT')")
    @ApiOperation(value = "修改字典详情")
    public ResponseEntity update(@Validated(DictDetail.Update.class) @RequestBody DictDetail resources){
        dictDetailService.update(resources);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    //@Log("删除字典详情")
    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DICT_ALL','DICT_DELETE')")
    @ApiOperation(value = "删除字典详情")
    public ResponseEntity delete(@PathVariable Long id){
        dictDetailService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }



}