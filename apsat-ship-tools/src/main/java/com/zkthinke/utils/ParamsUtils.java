package com.zkthinke.utils;

import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;

/**
 * @Author: huqijun
 * @Date: 2019/12/28 13:45
 */
public class ParamsUtils {

    private static final String AND_SEP = "&";
    private static final String EQ_SEP = "=";
    private static final String PARAM_SEP = "?";
    public static final String ENCODE = "UTF-8";

    /**
     * 拼接请求参数
     * @param params
     * @return
     */
    public static String joinParams(String... params){
        StringBuilder sb = new StringBuilder();
        Arrays.asList(params).stream().reduce(null, (a, b) -> {
            if ( a == null) {
                return b;
            }
            try {
                if(!StringUtils.isEmpty(b)){
                    b = URLEncoder.encode(b, ENCODE);
                }
            } catch (UnsupportedEncodingException e){

            }
            sb.append(AND_SEP + a + EQ_SEP + b);
            return null;
        });
        int i = sb.indexOf(AND_SEP);
        return 0 == i ? sb.replace(0, 1,PARAM_SEP).toString() : sb.toString();
    }
}