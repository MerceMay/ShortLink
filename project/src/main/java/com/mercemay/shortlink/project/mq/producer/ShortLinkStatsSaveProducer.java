package com.mercemay.shortlink.project.mq.producer;

import com.mercemay.shortlink.project.common.constant.RedisKeyConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ShortLinkStatsSaveProducer {
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 发送统计消息
     *
     * @param produceMap 消息内容
     */
    public void send(Map<String, String> produceMap) {
        stringRedisTemplate.opsForStream().add(RedisKeyConstant.SHORT_LINK_STATS_STREAM_TOPIC_KEY, produceMap);
    }
}
