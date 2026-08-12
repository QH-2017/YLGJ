package com.ylgj.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis 工具类（基于 StringRedisTemplate，键值均以字符串存储）
 * <p>
 * 业务落点：
 * - 登录 Token 缓存（实现退出/踢下线）
 * - 登录失败次数限制（账号锁定）
 * - 热点数据缓存（套餐列表等）
 */
@Component
public class RedisUtil {

    /** 登录 Token 前缀 */
    public static final String LOGIN_TOKEN_PREFIX = "login:token:";
    /** 用户 -> Token 前缀（同一账号互踢） */
    public static final String LOGIN_USER_PREFIX = "login:user:";
    /** 登录失败次数前缀 */
    public static final String LOGIN_FAIL_PREFIX = "login:fail:";
    /** 套餐列表缓存 Key */
    public static final String SETMEAL_LIST_KEY = "setmeal:list";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    /**
     * 写入并设置过期时间
     *
     * @param timeout 过期时间（秒）
     */
    public void set(String key, String value, long timeout) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }

    public boolean delete(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.delete(key));
    }

    /**
     * 自增计数（用于登录失败次数）
     */
    public long increment(String key) {
        Long value = stringRedisTemplate.opsForValue().increment(key);
        return value == null ? 0 : value;
    }

    /**
     * 设置过期时间
     *
     * @param timeout 过期时间（秒）
     */
    public boolean expire(String key, long timeout) {
        return Boolean.TRUE.equals(stringRedisTemplate.expire(key, timeout, TimeUnit.SECONDS));
    }
}
