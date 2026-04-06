package com.adopt.apigw.spring;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.adopt.apigw.constants.CacheConstant;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(CacheConstant.COMMONTYPE, CacheConstant.ALL_COMMONTYPE, CacheConstant.CLIENT_SRV);
    }
}
