package com.zkthinke.utils;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理string字符串
 *
 * @Author: huqijun
 * @Date: 2020/1/7 13:15
 */
public class HandleStringUtils {

    public static List<Long> string2ListLong(String ids){
        if(StringUtils.isEmpty(ids)){
            return new ArrayList<>();
        }
        List<Long> list = new ArrayList<>();
        for(String id : ids.split(",")){
            list.add(Long.valueOf(id));
        }
        return list;
    }
}