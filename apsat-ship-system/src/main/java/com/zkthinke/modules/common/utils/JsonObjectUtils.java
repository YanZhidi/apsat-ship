package com.zkthinke.modules.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.zkthinke.utils.StringUtils;

import java.math.BigDecimal;

/**
 * @Author: huqijun
 * @Date: 2020/1/16 18:31
 */
public final class JsonObjectUtils {

    /**
     * getStr
     * @param jsonObj
     * @param key
     * @return
     */
    public static String getStr(JSONObject jsonObj, String key){
        if(null == jsonObj.get(key)){
            return null;
        }
        return jsonObj.get(key).toString();
    }

    /**
     * getLongStr
     * @param jsonObj
     * @param key
     * @return
     */
    public static String getLongStr(JSONObject jsonObj, String key){
        if(null == jsonObj.get(key)){
            return null;
        }
        return jsonObj.get(key).toString();
    }

    /**
     * getInteger
     * @param jsonObj
     * @param key
     * @return
     */
    public static Integer getInteger(JSONObject jsonObj, String key){
        if(null == jsonObj.get(key)){
            return null;
        }
        String str = jsonObj.get(key).toString();
        if(StringUtils.isEmpty(str)){
            return null;
        }
        return Integer.valueOf(str);
    }

    /**
     * getLong
     * @param jsonObj
     * @param key
     * @return
     */
    public static Long getLong(JSONObject jsonObj, String key){
        if(null == jsonObj.get(key)){
            return null;
        }
        String str = jsonObj.get(key).toString();
        if(StringUtils.isEmpty(str)){
            return null;
        }
        return Long.valueOf(str);
    }

    /**
     * getBigDecimal
     * @param jsonObj
     * @param key
     * @return
     */
    public static BigDecimal getBigDecimal(JSONObject jsonObj, String key){
        if(null == jsonObj.get(key)){
            return null;
        }
        String str = jsonObj.get(key).toString();
        if(StringUtils.isEmpty(str)){
            return null;
        }
        return BigDecimal.valueOf(Double.valueOf(str));
    }

    /**
     * getFloat
     * @param jsonObj
     * @param key
     * @return
     */
    public static Float getFloat(JSONObject jsonObj, String key){
        if(null == jsonObj.get(key)){
            return null;
        }
        String str = jsonObj.get(key).toString();
        if(StringUtils.isEmpty(str)){
            return null;
        }
        return Float.valueOf(str);
    }
}