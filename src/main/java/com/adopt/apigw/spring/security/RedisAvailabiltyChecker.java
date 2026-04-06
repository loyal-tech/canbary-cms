package com.adopt.apigw.spring.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisAvailabiltyChecker {

    private static final Logger logger = LoggerFactory.getLogger(RedisAvailabiltyChecker.class);

    @Bean
    public CommandLineRunner checkRedisAvailability(LettuceConnectionFactory factory) {
        return args -> {
            try {
                factory.getConnection().ping();
                logger.info("Redis is available.");
            } catch (Exception e) {
                logger.warn("Redis is not available. Shutting down Redis-related beans.");
                factory.destroy();
            }
        };
    }

//    @Bean
//    public ApplicationRunner checkRedis(RedisTemplate<String, String> redisTemplate) {
//        return args -> {
//            while (true) {
//                try {
//                    redisTemplate.hasKey("dummy");
//                    break; // ready
//                } catch (RedisSystemException e) {
//                    logger.warn("Waiting for Redis...");
//                    Thread.sleep(2000);
//                }
//            }
//        };
//    }
}
