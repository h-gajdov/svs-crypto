package com.svsbrains.svscrypto.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Boot configuration class for setting up caching using Caffeine.
 * <p>
 * This configuration defines a CacheManager bean that manages caches for the application.
 * Specifically, it sets up a cache named "dailyDataBySymbol" to store cryptocurrency data
 * fetched from the database or external APIs. The cache is configured with an expiry
 * time and a maximum size to optimize memory usage and performance.
 * </p>
 */
@Configuration
public class CacheConfig {
    /**
     * Defines the CacheManager bean for the application.
     * <p>
     * Uses a CaffeineCacheManager to manage caches. In this setup:
     * - The cache is named "dailyDataBySymbol".
     * - Each cache entry expires 10 minutes after being written (TTL).
     * - The cache can hold up to 5000 entries to avoid excessive memory usage.
     * </p>
     *
     * @return a configured CacheManager instance
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "dailyDataAllCoins", "dailyDataAllSymbols"
        );
        cacheManager.setCaffeine(
                Caffeine.newBuilder()
                        .expireAfterWrite(10, java.util.concurrent.TimeUnit.MINUTES)
                        .maximumSize(5000)
        );
        return cacheManager;
    }
}
