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
package com.mercemay.shortlink.project.mq.consumer;

import com.mercemay.shortlink.project.common.constant.RedisKeyConstant;
import com.mercemay.shortlink.project.common.convention.exception.ServiceException;
import com.mercemay.shortlink.project.dto.biz.ShortLinkStatsRecordDTO;
import com.mercemay.shortlink.project.mq.idempotent.MessageQueueIdempotentHandler;
import com.mercemay.shortlink.project.service.ShortLinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.locks.LockSupport;

/**
 * 延迟短链接统计消息消费者
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DelayShortLinkStatsConsumer implements InitializingBean {
    private final RedissonClient redissonClient;
    private final ShortLinkService shortLinkService;
    private final MessageQueueIdempotentHandler messageQueueIdempotentHandler;

    public void onMessage() {
        Executors.newSingleThreadExecutor(
                        runnable -> {
                            Thread thread = new Thread(runnable);
                            thread.setName("DelayShortLinkStatsConsumer");
                            thread.setDaemon(Boolean.TRUE);
                            return thread;
                        })
                .execute(() -> {
                    RBlockingDeque<ShortLinkStatsRecordDTO> blockingDeque = redissonClient.getBlockingDeque(RedisKeyConstant.SHORT_LINK_DELAY_QUEUE_KEY); // 获取阻塞双端队列
                    RDelayedQueue<ShortLinkStatsRecordDTO> delayedQueue = redissonClient.getDelayedQueue(blockingDeque); // 获取延迟队列
                    while (true) {
                        try {
                            ShortLinkStatsRecordDTO statsRecordDTO = delayedQueue.poll(); // 获取并移除队列头部元素，若无元素则阻塞等待
                            if (statsRecordDTO != null) {
                                if (!messageQueueIdempotentHandler.isMessageProcessed(statsRecordDTO.getKeys())) {
                                    if (messageQueueIdempotentHandler.isAccomplish(statsRecordDTO.getKeys())) {
                                        return;
                                    }
                                    throw new ServiceException("消息未完成流程，需要消息队列重试");
                                }
                                try {
                                    shortLinkService.shortLinkStats(null, null, statsRecordDTO); // 处理短链接统计逻辑
                                } catch (Throwable ex) {
                                    messageQueueIdempotentHandler.delMessageIdempotentKey(statsRecordDTO.getKeys()); // 异常处理，删除幂等标识
                                    log.error(ex.getMessage(), ex);
                                }
                                messageQueueIdempotentHandler.markMessageAsAccomplish(statsRecordDTO.getKeys()); // 标记消息已完成
                                continue;
                            }
                            LockSupport.parkUntil(500); // 阻塞当前线程500毫秒，避免空轮询
                        } catch (Throwable ignored) {
                        }
                    }
                });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        onMessage();
    }
}
