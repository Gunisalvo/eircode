package com.flexco.eircode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;


@EnableAutoConfiguration
@ComponentScan("com.flexco.eircode")
@EnableCaching
public class EircodeApp {

    @Value("${redis.url}")
    private String redisUrl;

    @Value("${redis.port}")
    private Integer redisPort;

    @Value("${redis.expiration}")
    private Integer redisExpiration;

    @Value("${redis.pwd}")
    private String redisPassword;

    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        JedisConnectionFactory redisConnectionFactory = new JedisConnectionFactory();
        redisConnectionFactory.setHostName(this.redisUrl);
        redisConnectionFactory.setPassword(this.redisPassword);
        redisConnectionFactory.setPort(this.redisPort);
        return redisConnectionFactory;
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory cf) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(cf);
        return redisTemplate;
    }

    @Bean
    public CacheManager cacheManager(RedisTemplate redisTemplate) {
        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
        cacheManager.setDefaultExpiration(this.redisExpiration);
        return cacheManager;
    }

    public static void main(String ...  args) throws Exception {
        SpringApplication.run(EircodeApp.class, args);
    }

}
