package com.zkthinke.modules.security.rest;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.IdUtil;
import com.zkthinke.aop.log.Log;
import com.zkthinke.modules.security.security.AuthenticationInfo;
import com.zkthinke.modules.security.security.AuthorizationUser;
import com.zkthinke.modules.security.security.ImgResult;
import com.zkthinke.modules.security.security.JwtUser;
import com.zkthinke.modules.security.utils.JwtTokenUtil;
import com.zkthinke.modules.security.utils.VerifyCodeUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import com.zkthinke.exception.BadRequestException;
import com.zkthinke.modules.monitor.service.RedisService;
import com.zkthinke.utils.EncryptUtils;
import com.zkthinke.utils.SecurityUtils;
import com.zkthinke.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Zheng Jie
 * @date 2020-10-23
 * 授权、根据token获取用户详细信息
 */
@Slf4j
@RestController
@RequestMapping("auth")
@Api(tags = "登录授权")
public class AuthenticationController {

    @Value("${jwt.header}")
    private String tokenHeader;

    @Value("${ase.key}")
    private String decryptKey;

//    @Value("${login.failed.limit-num}")

    final private int failedLimit = 30;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private RedisService redisService;

    @Autowired
    @Qualifier("jwtUserDetailsService")
    private UserDetailsService userDetailsService;

    private static  final String LOGIN_FAIL = "login_fail";

    /**
     * 登录授权
     * @param authorizationUser
     * @return
     */
    //@Log("用户登录")
    @PostMapping(value = "${jwt.auth.path}")
    @ApiOperation(value = "用户登录")
    public ResponseEntity login(@Validated @RequestBody AuthorizationUser authorizationUser) throws Exception {
        // 查询验证码
        String code = redisService.getCodeVal(authorizationUser.getUuid());
        String num = redisService.getCodeVal(LOGIN_FAIL + "#" + authorizationUser.getUsername());
        if (!StringUtils.isEmpty(num) && failedLimit != 0 && Integer.valueOf(num) >= failedLimit) {
            throw new AccountExpiredException("登录失败次数过多，账号已被锁定，请于 30 分钟之后再试");
        }

        // 清除验证码
        redisService.delete(authorizationUser.getUuid());

        if(StringUtils.isBlank(code)) {
            this.errorLimit(authorizationUser.getUsername());
            throw new BadRequestException("验证码已过期");
        }
        if(StringUtils.isBlank(authorizationUser.getCode()) || !authorizationUser.getCode().equalsIgnoreCase(code)) {
            this.errorLimit(authorizationUser.getUsername());
            throw new BadRequestException("验证码错误");
        }

        // 查询用户信息
        final JwtUser jwtUser = (JwtUser) userDetailsService.loadUserByUsername(authorizationUser.getUsername());
        if(!jwtUser.isEnabled()) {
            throw new AccountExpiredException("账号已停用，请联系管理员");
        }

        // 密码错误,虚化提示信息
        String password = EncryptUtils.aesDecrypt(authorizationUser.getPassword(), decryptKey);
        if(!jwtUser.getPassword().equals(EncryptUtils.encryptPassword(password))) {
            this.errorLimit(jwtUser.getUsername());
            throw new AccountExpiredException("用户名或者密码错误");
        }

        // 生成令牌
        final String token = jwtTokenUtil.generateToken(jwtUser);

        // 返回 token
        return ResponseEntity.ok(new AuthenticationInfo(token,jwtUser));
    }

    private void errorLimit(String userName) {
        // 统计登录错误次数,30 分钟内仅限三次
        Long increment = redisService.increment(LOGIN_FAIL + "#" +userName, 1800);
        if (increment > 10L) {
            throw new AccountExpiredException("登录失败次数过多，账号已被锁定，请于 30 分钟之后再试");
        }
    }

    /**
     * 登录授权
     * @param authorizationUser
     * @return
     */
    //@Log("获取登录的鉴权")
    @PostMapping(value = "get-token")
    @ApiOperation(value = "获取登录的鉴权")
    public Object getToken(@Validated @RequestBody AuthorizationUser authorizationUser){
        final JwtUser jwtUser = (JwtUser) userDetailsService.loadUserByUsername(authorizationUser.getUsername());
        Map<String, Object> map = new HashMap<>();
        if(!jwtUser.getPassword().equals(EncryptUtils.encryptPassword(authorizationUser.getPassword()))){
//            throw new AccountExpiredException("密码错误");
            map.put("code", 401);
            map.put("msg", "无权限");
            return ResponseEntity.ok(map);
        }
        if(!jwtUser.isEnabled()){
            map.put("code", 401);
            map.put("msg", "无权限");
//            throw new AccountExpiredException("账号已停用，请联系管理员");
            return ResponseEntity.ok(map);
        }
        // 生成令牌
        final String token = jwtTokenUtil.generateToken(jwtUser);
        // 返回 token
        map.put("code", 200);
        map.put("msg", "成功");
        map.put("token", token);
        return ResponseEntity.ok(map);
    }

    /**
     * 获取用户信息
     * @return
     */
    @GetMapping(value = "${jwt.auth.account}")
    @ApiOperation(value = "获取用户信息")
    public ResponseEntity getUserInfo(){
        JwtUser jwtUser = (JwtUser)userDetailsService.loadUserByUsername(SecurityUtils.getUsername());
        return ResponseEntity.ok(jwtUser);
    }

    /**
     * 获取验证码
     */
    @GetMapping(value = "vCode")
    @ApiOperation(value = "获取验证码")
    public ImgResult getCode(HttpServletResponse response) throws IOException {

        //生成随机字串
        String verifyCode = VerifyCodeUtils.generateVerifyCode(4);
        String uuid = IdUtil.simpleUUID();
        redisService.saveCode(uuid,verifyCode);
        // 生成图片
        int w = 111, h = 36;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        VerifyCodeUtils.outputImage(w, h, stream, verifyCode);
        try {
            return new ImgResult(Base64.encode(stream.toByteArray()),uuid);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            stream.close();
        }
    }
}
