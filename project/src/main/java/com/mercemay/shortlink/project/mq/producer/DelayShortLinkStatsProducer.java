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
package com.mercemay.shortlink.project.mq.producer;

import cn.hutool.core.lang.UUID;
import com.mercemay.shortlink.project.common.constant.RedisKeyConstant;
import com.mercemay.shortlink.project.dto.biz.ShortLinkStatsRecordDTO;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 延迟短链接统计消息生产者
 */
@Component
@RequiredArgsConstructor
public class DelayShortLinkStatsProducer {
    private final RedissonClient redissonClient;

    /**
     * 发送延迟短链接统计消息
     *
     * @param shortLinkStatsRecordDTO 短链接统计记录DTO
     */
    public void send(ShortLinkStatsRecordDTO shortLinkStatsRecordDTO) {
        shortLinkStatsRecordDTO.setKeys(UUID.fastUUID().toString());
        RBlockingDeque<ShortLinkStatsRecordDTO> blockingDeque = redissonClient.getBlockingDeque(RedisKeyConstant.SHORT_LINK_DELAY_QUEUE_KEY);
        RDelayedQueue<ShortLinkStatsRecordDTO> delayedQueue = redissonClient.getDelayedQueue(blockingDeque);
        // 将短链接统计记录添加到延迟队列，延迟5秒后可被消费
        delayedQueue.offer(shortLinkStatsRecordDTO, 5, TimeUnit.SECONDS);
    }
}
