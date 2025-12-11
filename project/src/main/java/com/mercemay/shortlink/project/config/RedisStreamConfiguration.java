package com.mercemay.shortlink.project.config;

import com.mercemay.shortlink.project.common.constant.RedisKeyConstant;
import com.mercemay.shortlink.project.mq.consumer.ShortLinkStatsSaveConsumer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@RequiredArgsConstructor
public class RedisStreamConfiguration {

    private final RedisConnectionFactory redisConnectionFactory;
    private final ShortLinkStatsSaveConsumer shortLinkStatsSaveConsumer;

    @Bean
    public ExecutorService asyncStreamConsumer() {
        AtomicInteger index = new AtomicInteger();
        int processorCount = Runtime.getRuntime().availableProcessors();
        return new ThreadPoolExecutor(processorCount, // 核心线程数为CPU核数
                processorCount + processorCount >> 1, // 最大线程数为核心线程数的1.5倍
                60L, // 非核心线程闲置60秒后回收
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(), // 无界队列
                runnable -> {
                    Thread thread = new Thread(runnable);
                    thread.setName("shortlink_redis_stream_consumer_stats_" + index.incrementAndGet());
                    thread.setDaemon(true); // 设置为守护线程
                    return thread;
                }
        );
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer(ExecutorService asyncStreamConsumer) {
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options =
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                        .batchSize(10) // 每次批量处理10条消息
                        .executor(asyncStreamConsumer) // 使用自定义的线程池
                        .pollTimeout(Duration.ofSeconds(3)) // 轮询超时时间3秒
                        .build();
        StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer =
                StreamMessageListenerContainer.create(redisConnectionFactory, options);
        String topic = RedisKeyConstant.SHORT_LINK_STATS_STREAM_TOPIC_KEY;
        String group = RedisKeyConstant.SHORT_LINK_STATS_STREAM_CONSUMER_GROUP_KEY;
        streamMessageListenerContainer.receiveAutoAck(Consumer.from(group, "stats_consumer"),
                StreamOffset.create(topic, ReadOffset.lastConsumed()),
                shortLinkStatsSaveConsumer);
        return streamMessageListenerContainer;
    }
}
