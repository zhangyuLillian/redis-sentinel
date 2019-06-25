/**
 * transfer Inc.
 * Copyright (c) 2012-2016 All Rights Reserved.
 */
package com.lillian.redissentinel.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * redis配置bean
 *
 * <p>具体说明</p>
 *
 * @author zhangyu
 */
@Configuration
@Slf4j
public class RedisConfig implements EnvironmentAware {


    private  Environment   environment;

    @Bean(name = "jedisConnectionFactory")
    public JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(
            redisSentinelConfiguration());
        return jedisConnectionFactory;
    }

    @Bean
    public RedisSentinelConfiguration redisSentinelConfiguration() {
        RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration();
        redisSentinelConfiguration.master("mymaster");
        redisSentinelConfiguration.sentinel(environment.getProperty("spring.redis.host1"),
            Integer.valueOf(environment.getProperty("spring.redis.port")));
        redisSentinelConfiguration.sentinel(environment.getProperty("spring.redis.host2"),
            Integer.valueOf(environment.getProperty("spring.redis.port")));
        redisSentinelConfiguration.sentinel(environment.getProperty("spring.redis.host3"),
            Integer.valueOf(environment.getProperty("spring.redis.port")));
        redisSentinelConfiguration.setDatabase(Integer.valueOf(environment.getProperty("spring.redis.database")));
        redisSentinelConfiguration.setPassword(environment.getProperty("spring.redis.password"));
        return redisSentinelConfiguration;

    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(@Qualifier("jedisConnectionFactory") RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new RedisObjectSerializer());

        log.info("RedisTemplate install");
        return template;
    }

    /** 
     * @see EnvironmentAware#setEnvironment(Environment)
     */
    @Override
    public void setEnvironment(final Environment environment) {
        this.environment = environment;
    }

}
