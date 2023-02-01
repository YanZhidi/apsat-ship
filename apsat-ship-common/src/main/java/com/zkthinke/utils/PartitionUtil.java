package com.zkthinke.utils;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.function.Consumer;

/**
 * 分批处理工具类
 * @author weicb
 * @date 11/29/20 12:52 PM
 */
public class PartitionUtil {

    private static final int DEFAULT_SIZE = 500;

    public static <T> void listPartition(List<T> list, Consumer<? super List<T>> action) {
        if (list == null || list.isEmpty()) {
            return;
        }
        listPartition(list, action, DEFAULT_SIZE);
    }

    public static <T> void listPartition(List<T> list, Consumer<? super List<T>> action, int size) {
        if (list == null || list.isEmpty()) {
            return;
        }
        Lists.partition(list, size).forEach(action);
    }

}
