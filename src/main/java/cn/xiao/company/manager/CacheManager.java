package cn.xiao.company.manager;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * 多级缓存
 *
 * @author xiao
 */
@Component
public class CacheManager {

    /**
     * 本地缓存
     */
    Cache<String, Object> localCache = Caffeine.newBuilder().expireAfterWrite(60, TimeUnit.MINUTES).maximumSize(10_000).build();
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     */
    public void put(String key, Object value) {
        localCache.put(key, value);
        set(key, value, 60, TimeUnit.MINUTES);
    }

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 读缓存
     *
     * @param key
     * @return
     */
    public Object get(String key) {
        // 先从本地缓存中获取
        Object value = localCache.getIfPresent(key);
        if (Objects.nonNull(value)) {
            return value;
        }

        // 本地缓存未命中，尝试从 Redis 获取
        value = redisTemplate.opsForValue().get(key);
        if (Objects.nonNull(value)) {
            // 将 redis 的值写入到本地缓存
            localCache.put(key, value);
        }

        return value;
    }

    /**
     * 移除缓存
     *
     * @param key
     */
    public void delete(String key) {
        localCache.invalidate(key);
        redisTemplate.delete(key);
    }


}