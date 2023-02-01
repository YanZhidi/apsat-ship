package com.zkthinke.modules.monitor.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.function.Supplier;

/**
 * 可自行扩展
 * @author Zheng Jie
 * @date 2020-10-10
 */
public interface RedisService {

    /**
     * findById
     * @param key
     * @return
     */
    Page findByKey(String key, Pageable pageable);

    /**
     * 查询验证码的值
     * @param key
     * @return
     */
    String getCodeVal(String key);

    /**
     * 保存验证码
     * @param key
     * @param val
     */
    void saveCode(String key, Object val);

    /**
     * delete
     * @param key
     */
    void delete(String key);

    /**
     * 清空所有缓存
     */
    void flushdb();

    /**
     * 保存过期的key
     * @param key
     * @param val
     * @param time
     */
    void saveExpireKey(String key, Object val, Long time);

    /**
     * 获取缓存，未获取到调用supplier方法并保存
     * @param key 缓存key
     * @param supplier 数据查询
     * @param exp 超时时间
     * @param <T>
     * @return
     */
    <T> T get(String key,  Supplier<T> supplier, int exp);

    /**
     * 缓存数据
     * @param key 缓存key
     * @param val 缓存值
     * @param exp 超时时间
     * @param <T>
     */
    <T> void set(String key, T val, int exp);

    /**
     * redis 原子操作
     * @param key 缓存 key
     * @param exp 超时时间
     * @return
     */
    Long increment(String key, int exp);
}
