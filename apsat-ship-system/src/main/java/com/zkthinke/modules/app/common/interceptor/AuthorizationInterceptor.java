package com.zkthinke.modules.app.common.interceptor;


import com.zkthinke.annotation.Login;
import com.zkthinke.exception.AppAuthException;
import com.zkthinke.modules.security.utils.JwtTokenUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * 权限(Token)验证
 * @author huqijun
 * @date 2019/11/16
 */
@Component
@Slf4j
public class AuthorizationInterceptor extends HandlerInterceptorAdapter {

  @Autowired
  private JwtTokenUtil jwtUtils;

  public static final String USER_KEY = "userId";

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    Login annotation;
    if (handler instanceof HandlerMethod) {
      annotation = ((HandlerMethod) handler).getMethodAnnotation(Login.class);
    } else {
      return true;
    }

    if (annotation == null) {
      return true;
    }

    //获取用户凭证
    String token = request.getHeader(jwtUtils.getTokenHeader());
    if (StringUtils.isBlank(token)) {
      token = request.getParameter(jwtUtils.getTokenHeader());
    }

    //凭证为空
    if (StringUtils.isBlank(token)) {
      throw new AppAuthException(jwtUtils.getTokenHeader() + "不能为空",
              HttpStatus.UNAUTHORIZED.value());
    }
    boolean isExpireToken = true;
    try {
      isExpireToken = jwtUtils.isTokenExpired(token);
    } catch (Exception e) {
      log.error("judge expire token error:{}", e);
    }
    if (isExpireToken) {
      throw new AppAuthException(jwtUtils.getTokenHeader() + "失效，请重新登录",
          HttpStatus.UNAUTHORIZED.value());
    }
    //设置userId到request里，后续根据userId，获取用户信息
    request.setAttribute(USER_KEY, Long.parseLong(jwtUtils.getSubjectFromToken(token)));

    return true;
  }
}
