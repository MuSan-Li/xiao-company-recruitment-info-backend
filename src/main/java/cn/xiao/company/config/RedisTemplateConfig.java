package cn.xiao.company.config;

import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

/**
 * redis 配置
 *
 * @author xiao
 */
@Component
public class RedisTemplateConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplateConf(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        // 设置key的序列化格式
        template.setKeySerializer(RedisSerializer.string());
        // json序列化工具 设置value的序列化格式
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }


}
