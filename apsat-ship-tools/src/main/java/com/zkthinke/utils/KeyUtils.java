package com.zkthinke.utils;

import java.util.Arrays;

/**
 * @author weicb
 * @date 2020/1/3 11:27
 */
public class KeyUtils {

    private static final String JOIN_SEP = "#";

    /**
     * 拼接字符串
     * @param params 需要拼接的字符串
     * @return
     */
    public static String joinKeys(String... params){
        StringBuilder sb = new StringBuilder();
        Arrays.asList(params).stream().reduce(null, (a, b) -> {
            if ( a == null) {
                return b;
            }
            sb.append(a);
            sb.append(JOIN_SEP);
            sb.append(b);
            return null;
        });
        return sb.toString();
    }
}
