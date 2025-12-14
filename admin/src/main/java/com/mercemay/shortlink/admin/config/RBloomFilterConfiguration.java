package com.mercemay.shortlink.admin.config;


import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson布隆过滤器配置
 */
@Configuration(value = "rBloomFilterConfigurationByAdmin")
public class RBloomFilterConfiguration {
    /**
     * 用户注册缓存穿透布隆过滤器
     *
     * @param redissonClient Redisson客户端
     * @return 布隆过滤器
     */
    @Bean
    public RBloomFilter<String> userRegisterCachePenetrationBloomFilter(RedissonClient redissonClient) {
        RBloomFilter<String> cachePenetrationBloomFilter = redissonClient.getBloomFilter("userRegisterCachePenetrationBloomFilter");
        cachePenetrationBloomFilter.tryInit(100000000L, 0.001);
        return cachePenetrationBloomFilter;
    }

    /**
     * 防止分组标识注册查询数据库布隆过滤器
     *
     * @param redissonClient Redisson客户端
     * @return 布隆过滤器
     */
    @Bean
    public RBloomFilter<String> gidRegisterCachePenetrationBloomFilter(RedissonClient redissonClient) {
        RBloomFilter<String> cachePenetrationBloomFilter = redissonClient.getBloomFilter("gidRegisterCachePenetrationBloomFilter");
        cachePenetrationBloomFilter.tryInit(200000000L, 0.001);
        return cachePenetrationBloomFilter;
    }
}
