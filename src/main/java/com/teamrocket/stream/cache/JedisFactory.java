package com.teamrocket.stream.cache;

import org.springframework.beans.factory.annotation.Value;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

public class JedisFactory {

    @Value("${cache.redis.host}")
    private static String host = "localhost";

    @Value("${cache.redis.port}")
    private static Integer port = 6380;

    @Value("${cache.redis.timeout}")
    private static Integer timeout = 5000;

    @Value("${cache.redis.password}")
    private static String password = "Qwerty!234";
    // TODO configure user and password

    // hide the constructor
    private JedisFactory() {

    }

    private static JedisPool jedisPool;

    static {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);

        jedisPool = new JedisPool(
                poolConfig,
                host,
                port,
                timeout
                //,                password
        );
    }

    public static Jedis getConnection() {
        return jedisPool.getResource();
    }
}
