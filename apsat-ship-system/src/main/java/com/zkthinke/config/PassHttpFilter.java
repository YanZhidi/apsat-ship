package com.zkthinke.config;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

/**
 * Created by kellen on 2018/7/25.
 */
@Component
@WebFilter(urlPatterns = "/*", filterName = "authFilter")  //这里的“/*” 表示的是需要拦截的请求路径
public class PassHttpFilter implements Filter {


  @Override
  public void init(javax.servlet.FilterConfig filterConfig) {

  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
      FilterChain filterChain) throws IOException, ServletException {
    HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
    httpResponse.setHeader("Access-Control-Allow-Origin", "*");
    httpResponse.setHeader("Access-Control-Allow-Headers",
        "Origin, X-Requested-With, Content-Type, Accept,token,Authorization");
    httpResponse.setHeader("Access-Control-Max-Age", "3600");
    httpResponse
        .setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE,PUT");
    httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
    filterChain.doFilter(servletRequest, httpResponse);
  }

  @Override
  public void destroy() {
  }
}
