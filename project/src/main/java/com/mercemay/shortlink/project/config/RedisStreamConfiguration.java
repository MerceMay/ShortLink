package com.mercemay.shortlink.project.config;

import com.mercemay.shortlink.project.mq.consumer.ShortLinkStatsSaveConsumer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    private final StringRedisTemplate stringRedisTemplate;
    private final ShortLinkStatsSaveConsumer shortLinkStatsSaveConsumer;

    @Value("${spring.data.redis.channel-topic.short-link-stats}")
    private String topic;
    @Value("${spring.data.redis.channel-topic.short-link-stats-group}")
    private String group;

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

    @PostConstruct
    public void init() {
        StreamOperations<String, Object, Object> streamOperations = stringRedisTemplate.opsForStream();

        // 检查 Stream 是否存在
        if (stringRedisTemplate.hasKey(topic)) {
            // 获取现有的 Group 列表
            StreamInfo.XInfoGroups groups = streamOperations.groups(topic);
            // 检查我们的 group 是否在列表中
            boolean groupExists = groups.stream()
                    .anyMatch(g -> group.equals(g.groupName())); // 修正点：使用 g.groupName()

            if (!groupExists) {
                streamOperations.createGroup(topic, group);
            }
        } else {
            // Stream 不存在，自动创建 Stream 和 Group，从 0 开始读取
            streamOperations.createGroup(topic, ReadOffset.from("0"), group);
        }
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
        streamMessageListenerContainer.receiveAutoAck(Consumer.from(group, "stats_consumer"),
                StreamOffset.create(topic, ReadOffset.lastConsumed()),
                shortLinkStatsSaveConsumer);
        return streamMessageListenerContainer;
    }
}
