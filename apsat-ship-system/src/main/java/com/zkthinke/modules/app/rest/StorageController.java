package com.zkthinke.modules.app.rest;

import com.zkthinke.domain.StorageContent;
import com.zkthinke.response.ResponseResult;
import com.zkthinke.service.impl.StorageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * create by cjj
 */
@Api(value = "文件存储", tags = "文件存储")
@RestController
@RequestMapping("/app/storage")
@Slf4j
@AllArgsConstructor
public class StorageController {

    private StorageService storageService;

    @ApiOperation(value = "文件上传")
    @ApiImplicitParam(name = "file", value = "二进制文件流")
    @PostMapping("/upload")
    public ResponseResult upload(@RequestParam("file") MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        StorageContent store = storageService.store(file.getInputStream(), file.getSize(), file.getContentType(), originalFilename);
        return ResponseResult.ok(store);
    }
}
