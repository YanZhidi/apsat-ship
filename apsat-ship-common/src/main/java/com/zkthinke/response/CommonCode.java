package com.zkthinke.response;

public enum CommonCode implements ResultCode {

  SUCESS(1000, "操作成功"),
  BAD_ARGUMENT(1002, "参数错误"),
  IDENTIFY_ERROR(1003, "请先完成身份认证"),
  FAIL(9999, "操作失败"),
  UN_LOGIN(1009, "请登录");

  private int code;
  private String message;

  CommonCode(int code, String msg) {
    this.code = code;
    this.message = msg;
  }


  @Override
  public int code() {
    return code;
  }

  @Override
  public String msg() {
    return message;
  }

}
