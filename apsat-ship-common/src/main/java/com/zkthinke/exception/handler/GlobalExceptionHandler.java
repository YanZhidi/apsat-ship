package com.zkthinke.exception.handler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.zkthinke.exception.AppAuthException;
import com.zkthinke.exception.AppException;
import com.zkthinke.exception.BadRequestException;
import com.zkthinke.exception.EntityExistException;
import com.zkthinke.exception.EntityNotFoundException;
import com.zkthinke.response.ResponseResult;
import com.zkthinke.utils.ThrowableUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Created by kellen on 2019/8/24.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * 处理所有不可知的异常
   */
  @ExceptionHandler(Throwable.class)
  public ResponseEntity handleException(Throwable e) {
    // 打印堆栈信息
    log.error(ThrowableUtil.getStackTrace(e));
    ApiError apiError = new ApiError(BAD_REQUEST.value(), e.getMessage());
    return buildResponseEntity(apiError);
  }

  /**
   * 处理 接口无权访问异常AccessDeniedException
   */
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity handleAccessDeniedException(AccessDeniedException e) {
    // 打印堆栈信息
    log.error(ThrowableUtil.getStackTrace(e));
    ApiError apiError = new ApiError(FORBIDDEN.value(), e.getMessage());
    return buildResponseEntity(apiError);
  }

  /**
   * 处理自定义异常
   */
  @ExceptionHandler(value = BadRequestException.class)
  public ResponseEntity<ApiError> badRequestException(BadRequestException e) {
    // 打印堆栈信息
    log.error(ThrowableUtil.getStackTrace(e));
    ApiError apiError = new ApiError(e.getStatus(), e.getMessage());
    return buildResponseEntity(apiError);
  }

  /**
   * 处理 EntityExist
   */
  @ExceptionHandler(value = EntityExistException.class)
  public ResponseEntity<ApiError> entityExistException(EntityExistException e) {
    // 打印堆栈信息
    log.error(ThrowableUtil.getStackTrace(e));
    ApiError apiError = new ApiError(BAD_REQUEST.value(), e.getMessage());
    return buildResponseEntity(apiError);
  }

  /**
   * 处理 EntityNotFound
   */
  @ExceptionHandler(value = EntityNotFoundException.class)
  public ResponseEntity<ApiError> entityNotFoundException(EntityNotFoundException e) {
    // 打印堆栈信息
    log.error(ThrowableUtil.getStackTrace(e));
    ApiError apiError = new ApiError(NOT_FOUND.value(), e.getMessage());
    return buildResponseEntity(apiError);
  }

  /**
   * 处理所有接口数据验证异常
   *
   * @returns
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseResult handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    // 打印堆栈信息
    log.error(ThrowableUtil.getStackTrace(e));
    String[] str = e.getBindingResult().getAllErrors().get(0).getCodes()[1].split("\\.");
    StringBuffer msg = new StringBuffer(str[1] + ":");
    msg.append(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    return ResponseResult.badArgument(msg.toString());
  }

  /**
   * 统一返回
   */
  private ResponseEntity<ApiError> buildResponseEntity(ApiError apiError) {
    return new ResponseEntity(apiError, HttpStatus.valueOf(apiError.getCode()));
  }


  @ExceptionHandler(AppException.class)
  public ResponseResult handleAppException(AppException e) {
    // 打印堆栈信息
    log.error(ThrowableUtil.getStackTrace(e));
    return ResponseResult.fail(e.getMessage());
  }

  @ExceptionHandler(AppAuthException.class)
  public ResponseResult handleAppAuthException(AppAuthException e) {
    // 打印堆栈信息
    log.error(ThrowableUtil.getStackTrace(e));
    return ResponseResult.unLogin();
  }

  @ExceptionHandler(ExpiredJwtException.class)
  public ResponseResult ExpiredJwtException(ExpiredJwtException e) {
    // 打印堆栈信息
    log.error(ThrowableUtil.getStackTrace(e));
    return ResponseResult.unLogin();
  }

}
