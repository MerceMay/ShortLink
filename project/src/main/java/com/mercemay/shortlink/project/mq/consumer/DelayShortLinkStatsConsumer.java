package com.mercemay.shortlink.project.mq.consumer;

import com.mercemay.shortlink.project.common.constant.RedisKeyConstant;
import com.mercemay.shortlink.project.dto.biz.ShortLinkStatsRecordDTO;
import com.mercemay.shortlink.project.service.ShortLinkService;
import lombok.RequiredArgsConstructor;
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
@Component
@RequiredArgsConstructor
public class DelayShortLinkStatsConsumer implements InitializingBean {
    private final RedissonClient redissonClient;
    private final ShortLinkService shortLinkService;

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
                                shortLinkService.shortLinkStats(null, null, statsRecordDTO); // 处理短链接统计逻辑
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
