package com.lillian.redissentinel;

import com.lillian.redissentinel.util.RedisHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author zhangyu
 * @description
 * @date 2019/6/24 20:10
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@SpringBootTest(classes = RedisSentinelApplication.class)
@Slf4j
public class RedisTest {
    @Autowired
    RedisHelper redisHelper;

    @Test
    public void setAndGet() {
        redisHelper.set("hello","redis");
        String value = redisHelper.get("hello");
        log.info("value : {}",value);
    }
}
