/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
        return Boolean.TRUE.equals(stringRedisTemplate.opsForValue().setIfAbsent(key, "0", 2, TimeUnit.MINUTES));
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
    public void markMessageAsAccomplish(String messageId) {
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
