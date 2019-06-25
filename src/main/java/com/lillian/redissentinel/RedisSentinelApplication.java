package com.lillian.redissentinel;

import com.lillian.redissentinel.util.RedisHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

@SpringBootApplication
public class RedisSentinelApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisSentinelApplication.class, args);
    }

}
