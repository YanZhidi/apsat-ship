package com.zkthinke.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.teaopenapi.models.Config;
import com.zkthinke.config.SMSProperties;
import com.zkthinke.service.SMSService;
import lombok.extern.slf4j.Slf4j;


import java.util.Map;

@Slf4j
public class AliyunSMSServiceImpl implements SMSService {

    private Client client;

    private SMSProperties.Aliyun aliyun;

    public AliyunSMSServiceImpl(SMSProperties.Aliyun aliyun) throws Exception {
        Config config = new Config();
        config.setAccessKeyId(aliyun.getAccessKeyId());
        config.setAccessKeySecret(aliyun.getAccessKeySecret());
        config.setEndpoint(aliyun.getEndPoint());
        this.aliyun = aliyun;
        this.client = new Client(config);
    }

    @Override
    public void doSend(String phoneNumbers,String templateCode, Map<String, String> params) {
        SendSmsRequest request = new SendSmsRequest();
        request.setPhoneNumbers(phoneNumbers);
        request.setSignName(aliyun.getSignName());
        request.setTemplateCode(templateCode);
        request.setTemplateParam(JSONObject.toJSONString(params));
        try {
            client.sendSms(request);
        } catch (Exception e) {
            log.error("AliyunSMSServiceImpl.doSend（）异常：", e);
        }
    }
}
