package com.zkthinke.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface SMSService {

    void doSend(String phoneNumbers,String templateCode, Map<String, String> params);

}
