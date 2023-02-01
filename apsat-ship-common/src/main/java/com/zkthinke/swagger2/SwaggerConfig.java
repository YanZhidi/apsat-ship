package com.zkthinke.swagger2;

import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import com.zkthinke.annotation.LoginUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * api页面 /swagger-ui.html Created by kellen on 2019/8/24.
 */

@Configuration
@EnableSwagger2
@EnableSwaggerBootstrapUI
public class SwaggerConfig {

  @Value("${jwt.header}")
  private String tokenHeader;

  @Value("${swagger.enabled}")
  private Boolean enabled;

  @Bean
  public Docket createRestApi() {
    ParameterBuilder ticketPar = new ParameterBuilder();
    List<Parameter> pars = new ArrayList<Parameter>();
    ticketPar.name(tokenHeader).description("token")
        .modelRef(new ModelRef("string"))
        .parameterType("header")
        .defaultValue("Bearer ")
        .required(true)
        .build();
    pars.add(ticketPar.build());
    return new Docket(DocumentationType.SWAGGER_2)
        .enable(enabled)
        .ignoredParameterTypes(LoginUser.class)
        .apiInfo(apiInfo())
            .groupName("平台基础功能")
        .select()
        .paths(PathSelectors.any())
        .apis(RequestHandlerSelectors.basePackage("com.zkthinke.modules"))
        .build()
        .globalOperationParameters(pars);
  }

  private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
        .title("智慧航运平台接口文档")
        .version("1.0")
        .build();
  }

  @Bean
  public Docket createSecondRestApi() {
    ParameterBuilder ticketPar = new ParameterBuilder();
    List<Parameter> pars = new ArrayList<Parameter>();
    ticketPar.name(tokenHeader).description("token")
            .modelRef(new ModelRef("string"))
            .parameterType("header")
            .defaultValue("Bearer ")
            .required(true)
            .build();
    pars.add(ticketPar.build());
    return new Docket(DocumentationType.SWAGGER_2)
            .enable(enabled)
            .ignoredParameterTypes(LoginUser.class)
            .apiInfo(apiInfo())
            .groupName("智慧航运系统")
            .select()
            .paths(PathSelectors.any())
            .apis(RequestHandlerSelectors.basePackage("com.zkthinke.modules.apsat"))
            .build()
            .globalOperationParameters(pars);
  }

}
