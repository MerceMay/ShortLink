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
@Deprecated
@RequiredArgsConstructor
public class DelayShortLinkStatsProducer {
    private final RedissonClient redissonClient;

    /**
     * 发送延迟短链接统计消息
     *
     * @param statsRecord 短链接统计记录DTO
     */
    public void send(ShortLinkStatsRecordDTO statsRecord) {
        statsRecord.setKeys(UUID.fastUUID().toString());
        RBlockingDeque<ShortLinkStatsRecordDTO> blockingDeque = redissonClient.getBlockingDeque(RedisKeyConstant.SHORT_LINK_DELAY_QUEUE_KEY);
        RDelayedQueue<ShortLinkStatsRecordDTO> delayedQueue = redissonClient.getDelayedQueue(blockingDeque);
        // 将短链接统计记录添加到延迟队列，延迟5秒后可被消费
        delayedQueue.offer(statsRecord, 5, TimeUnit.SECONDS);
    }
}
