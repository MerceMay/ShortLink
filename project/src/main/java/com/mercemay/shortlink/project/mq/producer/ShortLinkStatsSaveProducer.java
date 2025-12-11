package com.mercemay.shortlink.project.mq.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ShortLinkStatsSaveProducer {
    private final StringRedisTemplate stringRedisTemplate;

    @Value("${spring.data.redis.channel-topic.short-link-stats}")
    private String topic;

    /**
     * 发送统计消息
     *
     * @param produceMap 消息内容
     */
    public void send(Map<String, String> produceMap) {
        stringRedisTemplate.opsForStream().add(topic, produceMap);
    }
}
