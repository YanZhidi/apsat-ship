package com.zkthinke.config;

import cn.hutool.core.util.StrUtil;
import com.zkthinke.utils.SecurityUtils;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

/**
 * Created by kellen on 2019/9/5.
 */
@Configuration
@Slf4j
public class AuditorConfig implements AuditorAware<String> {


  @Override
  public Optional<String> getCurrentAuditor() {
    Optional<String> user;
    try {
      user = Optional.of(SecurityUtils.getUsername());
      if (StrUtil.isNotBlank(user.get())) {
        return user;
      }
    } catch (Exception e) {
      log.error("getCurrentAuditor error");
    }
    return Optional.of("sys");
  }
}
