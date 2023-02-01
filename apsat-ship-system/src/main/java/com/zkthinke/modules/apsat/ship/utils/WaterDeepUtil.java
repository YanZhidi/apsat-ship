package com.zkthinke.modules.apsat.ship.utils;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.zkthinke.modules.common.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 调用水深API工具类
 *
 * @auther SONGXF
 * @date 2021/7/14 17:38
 */
@Component
@Slf4j
public class WaterDeepUtil {

    public static String token;
    public static String lineToken;
    //登录url
    public static String loginUrl;
    //水深获取url
    public static String waterDeepUrl;
    //等位线url
    public static String waterLineUrl;
    //获取token重试次数,不能多线程调用,多线程调用要加锁同步或者用threadLocal
    public static Integer tryCount = 0;
    public static Integer waterLineTryCount = 0;
    //获取等位线 现在传入固定值
    final static String ROUTEPOINTS="111.00894927978513 20.762614799308196,119.68789100646971 20.578233576048206,119.13333892822261 16.71504186091755,116.0843753814697 13.99953560822837,114.29094314575192 14.57178786107805,111.34317398071288 17.67951671057631,108.63959312438962 17.480525590756514,111.00894927978513 20.762614799308196";

    public static Double getWaterDeep(Double longitide, Double latitude) {
        log.info("根据经纬度获取水深入参[{}]-[{}]",longitide,latitude);
        Double weightDeep = null;
        if (StringUtils.isEmpty(token)) {
            log.info("token为null,开始第一次获取");
            token = getToken();
        }
        //调用API获取水深
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("fLon", longitide);
        paramMap.put("fLat", latitude);
        log.info("根据经纬度获取水深调用接口Token{}",token);
        log.info("根据经纬度获取水深调用接口Url{}",waterDeepUrl);
        try {
            String result = HttpRequest.post(waterDeepUrl)
                    .header("Authorization", token)
                    .form(paramMap)
                    .execute()
                    .body();

            JSONObject jsonObject = JSONObject.parseObject(result);
            log.info("根据经纬度获取水深调用接口出参{}",jsonObject.toJSONString());
            Integer errorCode = jsonObject.getInteger("errorCode");
            if (errorCode == 103 && tryCount < 1) {
                log.info("登录失效,即将第一次尝试重新获取token");
                token = getToken();
                tryCount++;
                getWaterDeep(longitide, latitude);
            }
            if (errorCode == 0) {
                //解析水深
                JSONObject data = jsonObject.getJSONObject("data");
                weightDeep = data.getDouble("actualVal");
            }

        } catch (Exception e) {
            log.error("水深数据API获取Token失败",e);
        }
        tryCount = 0;
        return weightDeep;

    }

    /**
     * 获取水位线
     * @param waterDeep 入参为水深
     * @return
     */
    public static JSONObject getWatersodiff(String waterDeep) {
        //结果反参
        JSONObject data = null;
//        List<Map<String, List<String>>> resultList = new ArrayList<>();
//        Map<String, List<String>> wateLine = new HashMap<>();
        if (StringUtils.isEmpty(lineToken)) {
            log.info("token为null,开始第一次获取");
            lineToken = getToken();
        }
        //调用API获取等位线
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("maxWaterDepthVal", waterDeep);
        paramMap.put("routePoints", ROUTEPOINTS);
        try {
            log.info("水深的URL为"+waterLineUrl);
            log.info("水深的TOKEN为"+lineToken);
            log.info("水深为"+waterDeep);
            String result = HttpRequest.post(waterLineUrl)
                    .header("Authorization", lineToken)
                    .form(paramMap)
                    .execute()
                    .body();

            JSONObject jsonObject = JSONObject.parseObject(result);
            Integer errorCode = jsonObject.getInteger("errorCode");
            if (errorCode == 102 && tryCount < 1) {
                log.info("登录失效,即将第一次尝试重新获取token");
                lineToken = getToken();
                waterLineTryCount++;
                getWatersodiff(waterDeep);
            }
            if (errorCode == 0) {
                //解析水深
                 data = jsonObject.getJSONObject("data");
//                data.entrySet().forEach(x->{
//                    List<String> stringList = JSONObject.parseArray(JSONObject.toJSONString(data.getJSONArray(x.getKey())), String.class);
//                    Map<String, List<String>> wateLine = new HashMap<>();
//                    wateLine.put(x.getKey(),stringList);
//                    resultList.add(wateLine);
//                });
            }

        } catch (Exception e) {
            log.error("水深数据API获取Token失败",e);
        }
        waterLineTryCount = 0;
        return data;
    }
    public static String getToken() {
        String token = null;
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("user", Constant.WATERDEEP_API_LOGIN_USER);
        paramMap.put("pass", Constant.WATERDEEP_API_LOGIN_PASS);
        try {
            String result = HttpUtil.post(loginUrl, paramMap);

            JSONObject jsonObject = JSONObject.parseObject(result);
            Integer errorCode = jsonObject.getInteger("errorCode");
            if (errorCode == 0) {
                //解析水深
                JSONObject data = jsonObject.getJSONObject("data");
                return data.getString("token");
            }

        } catch (Exception e) {
            log.error("水深数据API获取Token失败",e);
        }
        return token;
    }

    @Value("${apsat.setting.loginUrl}")
    public void setLoginUrl(String loginUrl) {
        WaterDeepUtil.loginUrl = loginUrl;
    }

    @Value("${apsat.setting.waterDeepUrl}")
    public void setWaterDeepUrl(String waterDeepUrl) {
        WaterDeepUtil.waterDeepUrl = waterDeepUrl;
    }

    @Value("${apsat.setting.waterLineUrl}")
    public void setWaterLineUrl(String waterLineUrl) {
        WaterDeepUtil.waterLineUrl = waterLineUrl;
    }


}
