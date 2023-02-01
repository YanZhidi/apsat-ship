package com.zkthinke.modules.system.rest;

import com.zkthinke.aop.log.Log;
import com.zkthinke.domain.StorageContent;
import com.zkthinke.domain.VerificationCode;
import com.zkthinke.exception.BadRequestException;
import com.zkthinke.modules.security.utils.CheckPwdUtils;
import com.zkthinke.modules.system.domain.User;
import com.zkthinke.modules.system.domain.vo.UserPassVo;
import com.zkthinke.modules.system.service.RoleService;
import com.zkthinke.modules.system.service.UserService;
import com.zkthinke.modules.system.service.dto.RoleSmallDTO;
import com.zkthinke.modules.system.service.dto.UserDTO;
import com.zkthinke.modules.system.service.dto.UserQueryCriteria;
import com.zkthinke.service.VerificationCodeService;
import com.zkthinke.service.impl.StorageService;
import com.zkthinke.utils.ElAdminConstant;
import com.zkthinke.utils.EncryptUtils;
import com.zkthinke.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * @author huqijun
 * @date 2019-11-16
 */
@RestController
@RequestMapping("api")
@Api(tags = "用户接口")
public class UserController {

  @Value("${ase.key}")
  private String decryptKey;

  @Autowired
  private UserService userService;

  @Autowired
  private RoleService roleService;

  @Autowired
  private VerificationCodeService verificationCodeService;

  @Autowired
  private StorageService storageService;

  //@Log("查询用户")
  @GetMapping(value = "/users")
  @PreAuthorize("hasAnyRole('ADMIN','USER_ALL','USER_SELECT')")
  @ApiOperation(value = "查询用户")
  public ResponseEntity getUsers(UserQueryCriteria criteria, Pageable pageable) {
    return new ResponseEntity(userService.queryAll(criteria, pageable), HttpStatus.OK);
  }

  //@Log("新增用户")
  @PostMapping(value = "/users")
  @PreAuthorize("hasAnyRole('ADMIN','USER_ALL','USER_CREATE')")
  @ApiOperation(value = "新增用户")
  public ResponseEntity create(@Validated @RequestBody UserDTO userDTO) {
    checkLevel(userDTO);
    UserDTO user = userService.findByName(SecurityUtils.getUsername());
    return new ResponseEntity(userService.insert(userDTO, user.getId()), HttpStatus.CREATED);
  }

  //@Log("修改用户")
  @PutMapping(value = "/users")
  @PreAuthorize("hasAnyRole('ADMIN','USER_ALL','USER_EDIT')")
  @ApiOperation(value = "修改用户")
  public ResponseEntity update(@Validated(User.Update.class) @RequestBody UserDTO userDTO) {
    checkLevel(userDTO);
    UserDTO user = userService.findByName(SecurityUtils.getUsername());
    userService.update(userDTO, user.getId());
    return new ResponseEntity(HttpStatus.NO_CONTENT);
  }

  //@Log("删除用户")
  @DeleteMapping(value = "/users/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','USER_ALL','USER_DELETE')")
  @ApiOperation(value = "删除用户")
  public ResponseEntity delete(@PathVariable Long id) {
    Integer currentLevel = Collections
        .min(roleService.findByUsers_Id(SecurityUtils.getUserId()).stream().map(
            RoleSmallDTO::getLevel).collect(Collectors.toList()));
    Integer optLevel = Collections.min(
        roleService.findByUsers_Id(id).stream().map(RoleSmallDTO::getLevel)
            .collect(Collectors.toList()));

    if (currentLevel > optLevel) {
      throw new BadRequestException("角色权限不足");
    }
    userService.delete(id);
    return new ResponseEntity(HttpStatus.OK);
  }

  /**
   * 修改密码
   */
  @PostMapping(value = "/users/updatePass")
  @ApiOperation(value = "修改密码")
  public ResponseEntity updatePass(@RequestBody UserPassVo user) throws Exception {
    UserDetails userDetails = SecurityUtils.getUserDetails();
    String oldPass = EncryptUtils.aesDecrypt(user.getOldPass(), decryptKey);
    String newPass = EncryptUtils.aesDecrypt(user.getNewPass(), decryptKey);
    if (!userDetails.getPassword().equals(EncryptUtils.encryptPassword(oldPass))) {
      throw new BadRequestException("修改失败，旧密码错误");
    }
    if (userDetails.getPassword().equals(EncryptUtils.encryptPassword(newPass))) {
      throw new BadRequestException("新密码不能与旧密码相同");
    }
    if (!CheckPwdUtils.EvalPWD(newPass)) {
      throw new BadRequestException("修改失败，密码不符合要求");
    }
    userService
        .updatePass(userDetails.getUsername(), EncryptUtils.encryptPassword(newPass));
    return new ResponseEntity(HttpStatus.OK);
  }

  /**
   * 修改头像
   */
  @PostMapping(value = "/users/updateAvatar")
  @ApiOperation(value = "修改头像")
  public ResponseEntity updateAvatar(@RequestParam MultipartFile file) throws IOException {
    String originalFilename = file.getOriginalFilename();
    StorageContent store = storageService.store(file.getInputStream(), file.getSize(), file.getContentType(), originalFilename);
    userService.updateAvatar(SecurityUtils.getUsername(), store.getUrl());
    return new ResponseEntity(HttpStatus.OK);
  }

  /**
   * 修改邮箱
   */
  //@Log("修改邮箱")
  @PostMapping(value = "/users/updateEmail/{code}")
  @ApiOperation(value = "修改邮箱")
  public ResponseEntity updateEmail(@PathVariable String code, @RequestBody User user) {
    UserDetails userDetails = SecurityUtils.getUserDetails();
    if (!userDetails.getPassword().equals(EncryptUtils.encryptPassword(user.getPassword()))) {
      throw new BadRequestException("密码错误");
    }
    VerificationCode verificationCode = new VerificationCode(code, ElAdminConstant.RESET_MAIL,
        "email", user.getEmail());
    verificationCodeService.validated(verificationCode);
    userService.updateEmail(userDetails.getUsername(), user.getEmail());
    return new ResponseEntity(HttpStatus.OK);
  }

  //@Log("查询用户详细信息")
  @GetMapping(value = "/find-by-id")
  @PreAuthorize("hasAnyRole('ADMIN','USER_ALL','USER_SELECT')")
  @ApiOperation(value = "查询用户详细信息")
  @ApiImplicitParam(name = "id", value = "用户id", required = true)
  public ResponseEntity getUserById(Long id) {
    UserDTO userDTO = userService.findById(id);
    return new ResponseEntity(userDTO, HttpStatus.OK);
  }

  /**
   * 如果当前用户的角色级别低于创建用户的角色级别，则抛出权限不足的错误
   */
  private void checkLevel(UserDTO resources) {
    Integer currentLevel = Collections.min(
        roleService.findByUsers_Id(SecurityUtils.getUserId()).stream().map(RoleSmallDTO::getLevel)
            .collect(Collectors.toList()));
    Integer optLevel = roleService.findByRoleSmalls(resources.getRoles());
    if (currentLevel > optLevel) {
      throw new BadRequestException("角色权限不足");
    }
  }
}
