package com.zkthinke.modules.app.common.resolver;

import com.zkthinke.annotation.LoginUser;
import com.zkthinke.modules.app.common.interceptor.AuthorizationInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 有@LoginUser注解的方法参数，注入当前登录用户
 * @author huqijun
 * @date 2019/11/16
 */
@Component
@AllArgsConstructor
public class LoginUserHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.getParameterType().isAssignableFrom(Long.class) && parameter
        .hasParameterAnnotation(
            LoginUser.class);
  }

  @Override
  public Long resolveArgument(MethodParameter parameter, ModelAndViewContainer container,
      NativeWebRequest request, WebDataBinderFactory factory) {
    //获取用户ID
    Object userId = request
        .getAttribute(AuthorizationInterceptor.USER_KEY, RequestAttributes.SCOPE_REQUEST);
    if (userId == null) {
      return null;
    }
    return (Long) userId;
  }
}
