package com.example.petshopbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 存入键值对，并设置过期时间
     * @param key 键
     * @param value 值
     * @param time 过期时间
     * @param timeUnit 时间单位
     */
    public void set(String key, String value, long time, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(key, value, time, timeUnit);
    }

    /**
     * 根据键获取值
     * @param key 键
     * @return 值
     */
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 根据键删除
     * @param key 键
     * @return 是否成功
     */
    public boolean delete(String key) {
        return stringRedisTemplate.delete(key);
    }
}
