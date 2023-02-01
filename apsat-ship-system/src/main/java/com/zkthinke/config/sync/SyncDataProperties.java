package com.zkthinke.config.sync;

import com.alibaba.fastjson.JSONObject;
import com.zkthinke.modules.apsat.sync.constant.OSNConstant;
import com.zkthinke.modules.common.constant.Constant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据同步相关配置
 * create by weicb
 */
@ConfigurationProperties(prefix = "data.sync")
@Setter
@Getter
public class SyncDataProperties {

   private String transId;
   private String token;
   private String appId;
   private String shipNavigationUrl;
   private String shipDeviceUrl;


   /**
    * 设置header的属性
    * @return
    */
   public JSONObject genHeaders(){
      JSONObject headers = new JSONObject();
      headers.put(OSNConstant.TRANS_ID, this.transId);
      headers.put(OSNConstant.APP_ID, this.appId);
      headers.put(OSNConstant.TOKEN, this.token);
      headers.put(OSNConstant.TIMESTAMP, 1595736533075L);
      headers.put("_", System.currentTimeMillis());
      return headers;
   }

}
