package com.zkthinke.response;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Api(value = "统一返回数据对象")
public class ResponseResult<T> implements Response {


  @ApiModelProperty(notes = "操作状态码：1000 成功；9999 失败；1002 参数未通过校验，其他按照错误码查询")
  int code = SUCCESS_CODE;
  @ApiModelProperty(notes = "提示信息")
  String message;

  @ApiModelProperty(notes = "返回的业务参数")
  T data;

  public ResponseResult(ResultCode resultCode, T data) {
    this.code = resultCode.code();
    this.message = resultCode.msg();
    this.data = data;
  }

  public ResponseResult(int errno, String errmsg) {
    this.code = errno;
    this.message = errmsg;
  }

  public ResponseResult(ResultCode resultCode) {
    this.code = resultCode.code();
    this.message = resultCode.msg();
  }

  public static ResponseResult ok() {
    ResponseResult responseResult = new ResponseResult(CommonCode.SUCESS);
    return responseResult;
  }

  public static ResponseResult ok(Object data) {
    ResponseResult responseResult = new ResponseResult(CommonCode.SUCESS, data);
    return responseResult;
  }

  public static ResponseResult fail(String msg) {
    ResponseResult responseResult = new ResponseResult(CommonCode.FAIL.code(), msg);
    return responseResult;
  }

  public static ResponseResult result(ResultCode resultCode) {
    ResponseResult responseResult = new ResponseResult(resultCode.code(), resultCode.msg());
    return responseResult;
  }

  public static ResponseResult result(Integer code, String message) {
    ResponseResult responseResult = new ResponseResult(code, message);
    return responseResult;
  }

  public static ResponseResult badArgument(String msg) {
    ResponseResult responseResult = new ResponseResult(CommonCode.BAD_ARGUMENT.code(), msg);
    return responseResult;
  }

  public static ResponseResult unLogin() {
    ResponseResult responseResult = new ResponseResult(CommonCode.UN_LOGIN);
    return responseResult;
  }

  public static ResponseResult fail() {
    ResponseResult responseResult = new ResponseResult(CommonCode.FAIL);
    return responseResult;
  }

}
