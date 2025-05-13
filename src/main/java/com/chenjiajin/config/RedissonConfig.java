package com.chenjiajin.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson配置类
 */
@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient() {

        Config config = new Config();
        config.useSingleServer()  // useSingleServer表示单节点   useClusterServers表示集群地址
                //.setPassword("admin")
                .setAddress("redis://127.0.0.1:6379");

        // 创建RedissonClient对象
        return Redisson.create(config);
    }


}