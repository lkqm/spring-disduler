package com.github.lkqm.disduler.lock;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.types.Expiration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁
 */
@AllArgsConstructor
public class RedisLock implements Lock {

    private StringRedisTemplate redisTemplate;

    @Override
    public boolean lock(String key, String value, int expiredSeconds) {
        Boolean result = redisTemplate.opsForValue().setIfAbsent(key, value, expiredSeconds, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(result);
    }

    @Override
    public boolean release(String key, String value) {
        // 提示: 必须指定returnType, 类型: 此处必须为Long, 不能是Integer
        RedisScript<Long> script = RedisScript.of("if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end", Long.class);
        Long rows = redisTemplate.execute(script, Arrays.asList(key), value);
        return rows != null && rows > 0;
    }

}
