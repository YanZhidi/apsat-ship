package com.zkthinke.modules.apsat.ship.utils;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zkthinke.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 航行警告API工具类
 *
 * @author dww
 * @since 1.0
 */
@Component
@Slf4j
public class NavigationWarningUtil {

    //E航海用户登录地址
    @Value("${navigation.setting.loginUrl}")
    private String loginUrl;

    //E航海用户MRN
    @Value("${navigation.setting.userMrn}")
    private String userMrn;

    //E航海用户密码
    @Value("${navigation.setting.password}")
    private String password;

    //E航海系统终端授权key
    @Value("${navigation.setting.key}")
    private String key;


    //3.3.	按区域获取航行警告/航行通告信息 url
    @Value("${navigation.setting.mSIbyAreaUrl}")
    private String mSIbyAreaUrl;


    /**
     * 3.3.	按区域获取航行警告/航行通告信息
     *
     * @param area     区域,WKT格式
     * @param type     0:全部 1:航行警告 2：航行通告
     * @param language 版本 zho(中文)  eng(英文)
     * @return 统一视图对象
     */
    public ResponseResult getNoticeByArea(String area, Integer type, String language) {
        // E航海用户登录获取登录token
        String token = getToken();
        //调用API按区域获取航行警告/航行通告信息接口
        Map<String, Object> requestParamMap = new HashMap<>();
        requestParamMap.put("area", area);
        requestParamMap.put("type", type);
        requestParamMap.put("language", language);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("param", JSONObject.toJSON(requestParamMap));
        log.info("按区域获取航行警告/航行通告信息接口地址:[{}].", mSIbyAreaUrl);
        log.info("按区域获取航行警告/航行通告信息,请求参数:area:[{}],type:[{}],language:[{}],token:[{}].", area, type, language, token);
        JSONArray data;
        try {
            String result = HttpRequest.post(mSIbyAreaUrl)
                    .header("Authorization", token)
                    .form(paramMap)
                    .execute()
                    .body();

            JSONObject jsonObject = JSONObject.parseObject(result);
            Integer errorCode = jsonObject.getInteger("code");
            if (errorCode != 200) {
                log.info("按区域获取航行警告/航行通告信息,异常响应报文:[{}]", result);
            }
            if (errorCode == 200) {
                //按区域获取航行警告/航行通告信息
                data = jsonObject.getJSONArray("data");
            } else {
                return ResponseResult.fail();
            }
        } catch (Exception e) {
            log.error("调用按区域获取航行警告/航行通告信息调用接口异常:[{}].", e.getMessage(), e);
            return ResponseResult.fail();
        }
        return ResponseResult.ok(data);
    }

    /**
     * E航海用户登录获取登录token
     *
     * @return token
     */
    private String getToken() {
        Map<String, Object> paramMap = new HashMap<>();
        Map<String, String> requestParamMap = new HashMap<>();
        requestParamMap.put("userMrn", userMrn);
        requestParamMap.put("password", Base64.encodeBase64String(password.getBytes()));
        requestParamMap.put("key", key);
        paramMap.put("param", JSONObject.toJSON(requestParamMap));
        log.info("E航海用户登录地址:[{}].", loginUrl);
        log.info("E航海用户登录,请求报文:[{}].", JSONObject.toJSONString(paramMap));
        try {
            String result = HttpUtil.post(loginUrl, paramMap);
            log.info("E航海用户登录,响应报文:[{}].", JSONObject.toJSONString(result));
            JSONObject jsonObject = JSONObject.parseObject(result);
            Integer errorCode = jsonObject.getInteger("code");
            if (errorCode == 200) {
                return jsonObject.getString("token");
            }
        } catch (Exception e) {
            log.error("E航海用户登录获取登录token异常:[{}].", e.getMessage(), e);
        }
        return null;
    }
}
