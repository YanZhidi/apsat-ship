package com.zkthinke.modules.monitor.service.impl;

import com.zkthinke.modules.monitor.domain.vo.RedisVo;
import com.zkthinke.modules.monitor.service.RedisService;
import com.zkthinke.utils.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author Zheng Jie
 * @date 2020-10-10
 */
@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    RedisTemplate redisTemplate;

    @Value("${loginCode.expiration}")
    private Long expiration;

    @Override
    public Page<RedisVo> findByKey(String key, Pageable pageable){
        List<RedisVo> redisVos = new ArrayList<>();
        if(!"*".equals(key)){
            key = "*" + key + "*";
        }
        for (Object s : redisTemplate.keys(key)) {
            // 过滤掉权限的缓存
            if (s.toString().indexOf("role::loadPermissionByUser") != -1 || s.toString().indexOf("user::loadUserByUsername") != -1) {
                continue;
            }
            RedisVo redisVo = new RedisVo(s.toString(),redisTemplate.opsForValue().get(s.toString()).toString());
            redisVos.add(redisVo);
        }
        Page<RedisVo> page = new PageImpl<RedisVo>(
                PageUtil.toPage(pageable.getPageNumber(),pageable.getPageSize(),redisVos),
                pageable,
                redisVos.size());
        return page;
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public void flushdb() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    @Override
    public String getCodeVal(String key) {
        try {
            String value = redisTemplate.opsForValue().get(key).toString();
            return value;
        }catch (Exception e){
            return "";
        }
    }

    @Override
    public void saveCode(String key, Object val) {
        redisTemplate.opsForValue().set(key,val);
        redisTemplate.expire(key,expiration, TimeUnit.MINUTES);
    }

    @Override
    public void saveExpireKey(String key, Object val, Long time) {
        redisTemplate.opsForValue().set(key,val);
        redisTemplate.expire(key, time, TimeUnit.SECONDS);
    }

    @Override
    public <T> T get(String key, Supplier<T> supplier, int exp) {
        Object o = redisTemplate.opsForValue().get(key);
        if ( o == null) {
            T t = supplier.get();
            if (t == null) {
                return null;
            }
            if (exp > 0) {
                redisTemplate.opsForValue().set(key, t, exp, TimeUnit.SECONDS);
            } else {
                redisTemplate.opsForValue().set(key, t);
            }
            return t;
        }
        return (T)o;
    }

    @Override
    public <T> void set(String key, T val, int exp) {
        redisTemplate.opsForValue().set(key,val, exp, TimeUnit.SECONDS);
    }

    @Override
    public Long increment(String key, int exp) {
        this.get(key, () -> 0 , exp);
        Long incr = redisTemplate.opsForValue().increment(key);
        return incr;
    }
}
