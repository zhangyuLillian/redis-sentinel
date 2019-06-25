# redis-sentinel
redis 哨兵(sentinel)与springboot集成实战

#application.properties
配置文件需要配置三台哨兵的ip和端口和密码，你们下载后需要改成自己的Ip

启动RedisSentinelApplication的main方法，可以看到下面redis的相关日志
```
2019-06-25 09:11:45.203  INFO 65048 --- [main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 18ms. Found 0 repository interfaces.
2019-06-25 09:11:45.363  INFO 65048 --- [main] redis.clients.jedis.JedisSentinelPool    : Trying to find master from available Sentinels...
2019-06-25 09:11:45.385  INFO 65048 --- [main] redis.clients.jedis.JedisSentinelPool    : Redis master running at 192.168.71.102:6379, starting Sentinel listeners...
2019-06-25 09:11:45.394  INFO 65048 --- [main] redis.clients.jedis.JedisSentinelPool    : Created JedisPool to master at 192.168.71.102:6379
2019-06-25 09:11:45.402  INFO 65048 --- [main] c.l.redissentinel.config.RedisConfig     : RedisTemplate install
2019-06-25 09:11:45.594  INFO 65048 --- [main] c.l.r.RedisSentinelApplication           : Started RedisSentinelApplication in 1.089 seconds (JVM running for 1.894)
```
#test
RedisTest类里面有测试案例
