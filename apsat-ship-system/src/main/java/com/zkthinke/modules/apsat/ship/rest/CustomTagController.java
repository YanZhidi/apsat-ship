package com.zkthinke.modules.apsat.ship.rest;

import com.zkthinke.modules.apsat.ship.domain.CustomTagPO;
import com.zkthinke.modules.apsat.ship.service.CustomTagService;
import com.zkthinke.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Api(tags = "自定义标注")
@RestController
@RequestMapping("api/tag")
public class CustomTagController {

    @Autowired
    private CustomTagService customTagService;

    @ApiOperation(value = "查询自定义标注列表")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SHIP_ALL','SHIP_SELECT')")
    public ResponseResult getCustomTagList() {
        List<CustomTagPO> list = customTagService.getCustomTagList();
        return ResponseResult.ok(list);
    }

    @ApiOperation(value = "新增自定义标注")
    @PostMapping("/addCustomTag")
    @PreAuthorize("hasAnyRole('ADMIN','SHIP_ALL','SHIP_SELECT')")
    public ResponseResult addCustomTag(@RequestBody CustomTagPO customTagPO) {
        try {
            CustomTagPO rsp = customTagService.addCustomTag(customTagPO);
            return ResponseResult.ok(rsp);
        } catch (Exception e) {
            log.info("addCustomTag 异常：", e);
            return ResponseResult.fail(e.getMessage());
        }
    }

    @ApiOperation(value = "修改自定义标注")
    @PostMapping("/updateCustomTag")
    @PreAuthorize("hasAnyRole('ADMIN','SHIP_ALL','SHIP_SELECT')")
    public ResponseResult updateCustomTag(@RequestBody CustomTagPO customTagPO) {
        try {
            customTagService.updateCustomTag(customTagPO);
        } catch (Exception e) {
            log.info("updateCustomTag 异常：", e);
            return ResponseResult.fail(e.getMessage());
        }
        return ResponseResult.ok();
    }

    @ApiOperation(value = "批量修改自定义标注")
    @PostMapping("/batchUpdateCustomTag")
    @PreAuthorize("hasAnyRole('ADMIN','SHIP_ALL','SHIP_SELECT')")
    public ResponseResult batchUpdateCustomTag(@RequestBody List<CustomTagPO> customTagPOList) {
        try {
            for (CustomTagPO customTagPO : customTagPOList) {
                customTagService.updateCustomTag(customTagPO);
            }
        } catch (Exception e) {
            log.error("batchUpdateCustomTag 异常:{}",e.getMessage(),e);
            return ResponseResult.fail(e.getMessage());
        }
        return ResponseResult.ok();
    }

    @ApiOperation(value = "删除自定义标注")
    @PostMapping("/deleteCustomTag")
    @PreAuthorize("hasAnyRole('ADMIN','SHIP_ALL','SHIP_SELECT')")
    public ResponseResult deleteCustomTag(@RequestBody CustomTagPO customTagPO) {
        try {
            customTagService.deleteCustomTag(customTagPO);
        } catch (Exception e) {
            log.info("deleteCustomTag 异常：", e);
            return ResponseResult.fail(e.getMessage());
        }
        return ResponseResult.ok();
    }
}
