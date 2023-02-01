package com.zkthinke.config;

import com.zkthinke.service.SMSService;
import com.zkthinke.service.impl.AliyunSMSServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@EnableConfigurationProperties(SMSProperties.class)
public class SMSConfiguration {

    private final SMSProperties properties;

    public SMSConfiguration(SMSProperties properties) {
        this.properties = properties;
    }

    @Bean
    public SMSService smsService() {
        String active = properties.getActive();
        try {
            if ("aliyun".equals(active)) {
                return new AliyunSMSServiceImpl(properties.getAliyun());
            }
        } catch (Exception e) {
            log.error("SMSService实例化异常：",e);
        }
        return null;
    }

}
