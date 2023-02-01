package com.zkthinke.modules.security.config;

import com.zkthinke.modules.security.security.JwtAuthenticationEntryPoint;
import com.zkthinke.modules.security.security.JwtAuthorizationTokenFilter;
import com.zkthinke.modules.security.service.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private JwtAuthenticationEntryPoint unauthorizedHandler;

  @Autowired
  private JwtUserDetailsService jwtUserDetailsService;

  /**
   * 自定义基于JWT的安全过滤器
   */
  @Autowired
  JwtAuthorizationTokenFilter authenticationTokenFilter;

  @Value("${jwt.header}")
  private String tokenHeader;

  @Value("${jwt.auth.path}")
  private String loginPath;

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth
        .userDetailsService(jwtUserDetailsService)
        .passwordEncoder(passwordEncoderBean());
  }

  @Bean
  GrantedAuthorityDefaults grantedAuthorityDefaults() {
    // Remove the ROLE_ prefix
    return new GrantedAuthorityDefaults("");
  }

  @Bean
  public PasswordEncoder passwordEncoderBean() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Override
  protected void configure(HttpSecurity httpSecurity) throws Exception {

    httpSecurity

        // 禁用 CSRF
        .csrf().disable()

        // 授权异常
        .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()

        // 不创建会话
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

        // 过滤请求
        .authorizeRequests()
        .antMatchers(
            HttpMethod.GET,
            "/*.html",
            "/**/*.html",
            "/**/*.css",
            "/**/*.js"
        ).permitAll()

        .antMatchers(HttpMethod.POST, "/auth/" + loginPath).permitAll()
        .antMatchers("/auth/vCode").permitAll()
        .antMatchers("/auth/get-token").permitAll()
        // swagger start
        .antMatchers("/swagger-ui.html").permitAll()
        .antMatchers("/swagger-resources/**").permitAll()
        .antMatchers("/*/api-docs").anonymous()
        .antMatchers("/app/front/dict-detail/**").permitAll()
        // swagger end
        .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()

        .antMatchers("/druid/**").permitAll()
        // 所有请求都需要认证
        .anyRequest().authenticated()
        // 防止iframe 造成跨域
        .and().headers().frameOptions().disable();

    httpSecurity
        .addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
  }
}
