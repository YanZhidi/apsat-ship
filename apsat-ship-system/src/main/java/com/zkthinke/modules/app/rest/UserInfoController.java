package com.zkthinke.modules.app.rest;

import com.zkthinke.aop.log.Log;
import com.zkthinke.modules.system.domain.User;
import com.zkthinke.modules.system.service.UserService;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "pc前端用户服务", tags = "pc前端用户服务")
@RestController
@RequestMapping("/app/user-info")
@Slf4j
@AllArgsConstructor
public class UserInfoController {

    @Autowired
    private UserService userService;

    //@Log("用户注册")
    @PostMapping(value = "/register")
    public ResponseEntity register(@Validated @RequestBody User user) {
        return new ResponseEntity(userService.create(user), HttpStatus.CREATED);
    }
}
