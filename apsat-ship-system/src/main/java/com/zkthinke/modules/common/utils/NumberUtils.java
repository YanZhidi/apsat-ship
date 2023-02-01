package com.zkthinke.modules.common.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: huqijun
 * @Date: 2020/3/5 23:40
 */
public class NumberUtils {

    public static Map<Integer, Integer> setMonthMap(){
        Map<Integer, Integer> monthMap = new HashMap<>();
        monthMap.put(1, 0);
        monthMap.put(2, 0);
        monthMap.put(3, 0);
        monthMap.put(4, 0);
        monthMap.put(5, 0);
        monthMap.put(6, 0);
        monthMap.put(7, 0);
        monthMap.put(8, 0);
        monthMap.put(9, 0);
        monthMap.put(10, 0);
        monthMap.put(11, 0);
        monthMap.put(12, 0);
        return monthMap;
    }
}