package com.mercemay.shortlink.project.initialize;

import com.mercemay.shortlink.project.common.constant.RedisKeyConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 短链接统计流初始化任务
 */
@Component
@RequiredArgsConstructor
public class ShortLinkStatsStreamInitializeTask implements InitializingBean {
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        Boolean keyExists = stringRedisTemplate.hasKey(RedisKeyConstant.SHORT_LINK_STATS_STREAM_TOPIC_KEY);
        if (!keyExists) {
            stringRedisTemplate.opsForStream().createGroup(RedisKeyConstant.SHORT_LINK_STATS_STREAM_TOPIC_KEY, RedisKeyConstant.SHORT_LINK_STATS_STREAM_CONSUMER_GROUP_KEY);
        }
    }
}
