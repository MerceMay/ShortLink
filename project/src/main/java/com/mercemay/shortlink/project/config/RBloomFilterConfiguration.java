package com.mercemay.shortlink.project.config;


import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson 布隆过滤器配置类
 */
@Configuration
public class RBloomFilterConfiguration {
    /**
     * 短链接创建防止缓存穿透布隆过滤器
     *
     * @param redissonClient Redisson 客户端
     * @return 布隆过滤器实例
     */
    @Bean
    public RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter(RedissonClient redissonClient) {
        RBloomFilter<String> cachePenetrationBloomFilter = redissonClient.getBloomFilter("shortUriCreateCachePenetrationBloomFilter");
        cachePenetrationBloomFilter.tryInit(100000000L, 0.001);
        return cachePenetrationBloomFilter;
    }
}
