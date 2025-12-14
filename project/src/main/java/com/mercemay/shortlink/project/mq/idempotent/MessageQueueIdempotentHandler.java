package com.mercemay.shortlink.project.mq.idempotent;

import com.mercemay.shortlink.project.common.constant.RedisKeyConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 消息队列幂等处理器
 */
@Component
@RequiredArgsConstructor
public class MessageQueueIdempotentHandler {
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 判断消息是否已处理
     *
     * @param messageId 消息ID
     * @return 是否已处理
     */
    public boolean isMessageProcessed(String messageId) {
        String key = RedisKeyConstant.MESSAGE_QUEUE_IDEMPOTENT_KEY + messageId;
        return Boolean.TRUE.equals(stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 2, TimeUnit.MINUTES));
    }

    /**
     * 判断消息是否已完成
     *
     * @param messageId 消息ID
     * @return 是否已完成
     */
    public boolean isAccomplish(String messageId) {
        String key = RedisKeyConstant.MESSAGE_QUEUE_IDEMPOTENT_KEY + messageId;
        return Objects.equals(stringRedisTemplate.opsForValue().get(key), "1");
    }

    /**
     * 标记消息已完成
     *
     * @param messageId 消息ID
     */
    public void setAccomplish(String messageId) {
        String key = RedisKeyConstant.MESSAGE_QUEUE_IDEMPOTENT_KEY + messageId;
        stringRedisTemplate.opsForValue().set(key, "1", 2, TimeUnit.MINUTES);
    }

    /**
     * 异常处理，删除幂等标识
     *
     * @param messageId 消息ID
     */
    public void delMessageIdempotentKey(String messageId) {
        String key = RedisKeyConstant.MESSAGE_QUEUE_IDEMPOTENT_KEY + messageId;
        stringRedisTemplate.delete(key);
    }
}
