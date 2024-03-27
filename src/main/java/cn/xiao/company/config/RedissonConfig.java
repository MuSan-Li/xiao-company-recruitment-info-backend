package cn.xiao.company.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * redisson 配置
 *
 * @author xiao
 */
@Data
@ConfigurationProperties(prefix = "spring.redis")
@Configuration
public class RedissonConfig {
    private Integer database;
    private String host;
    private Integer port;
    private String password;
    private String username;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setDatabase(database)
                .setAddress("redis://" + host + ":" + port)
        // .setPassword(password)
        ;
        return Redisson.create(config);
    }
}