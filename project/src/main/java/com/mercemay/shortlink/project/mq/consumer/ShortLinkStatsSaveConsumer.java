package com.mercemay.shortlink.project.mq.consumer;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Week;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mercemay.shortlink.project.common.constant.RedisKeyConstant;
import com.mercemay.shortlink.project.common.constant.ShortLinkConstant;
import com.mercemay.shortlink.project.dao.entity.*;
import com.mercemay.shortlink.project.dao.mapper.*;
import com.mercemay.shortlink.project.dto.biz.ShortLinkStatsRecordDTO;
import com.mercemay.shortlink.project.mq.idempotent.MessageQueueIdempotentHandler;
import com.mercemay.shortlink.project.mq.producer.DelayShortLinkStatsProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShortLinkStatsSaveConsumer implements StreamListener<String, MapRecord<String, String, String>> {
    private final ShortLinkMapper shortLinkMapper;
    private final ShortLinkRouteMapper shortLinkRouteMapper;
    private final RedissonClient redissonClient;
    private final LinkAccessStatsMapper linkAccessStatsMapper;
    private final LinkLocaleStatsMapper linkLocaleStatsMapper;
    private final LinkOsStatsMapper linkOsStatsMapper;
    private final LinkBrowserStatsMapper linkBrowserStatsMapper;
    private final LinkAccessLogsMapper linkAccessLogsMapper;
    private final LinkDeviceStatsMapper linkDeviceStatsMapper;
    private final LinkNetworkStatsMapper linkNetworkStatsMapper;
    private final LinkStatsTodayMapper linkStatsTodayMapper;
    private final DelayShortLinkStatsProducer delayShortLinkStatsProducer;
    private final StringRedisTemplate stringRedisTemplate;
    private final MessageQueueIdempotentHandler messageQueueIdempotentHandler;
    @Value("${short-link.stats.locale.amap-key}")
    private String statsLocaleAmapKey;

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        String stream = message.getStream();
        RecordId id = message.getId();
        if (messageQueueIdempotentHandler.isMessageProcessed(id.toString())) {
            if (messageQueueIdempotentHandler.isAccomplish(id.toString())) {
                return;
            }
            messageQueueIdempotentHandler.delMessageIdempotentKey(id.toString());
        }
        try {
            Map<String, String> valueMap = message.getValue();
            String fullShortUrl = valueMap.get("fullShortUrl");
            if (StrUtil.isNotBlank(fullShortUrl)) {
                String gid = valueMap.get("gid");
                ShortLinkStatsRecordDTO shortLinkStatsRecordDTO = JSON.parseObject(valueMap.get("shortLinkStatsRecord"), ShortLinkStatsRecordDTO.class);
                processMessage(fullShortUrl, gid, shortLinkStatsRecordDTO);
            }
            stringRedisTemplate.opsForStream().delete(Objects.requireNonNull(stream), id.getValue());
            messageQueueIdempotentHandler.markMessageAsAccomplish(id.toString());
        } catch (Throwable ex) {
            messageQueueIdempotentHandler.delMessageIdempotentKey(id.toString());
            log.error("记录短链接访问量消息处理失败，消息ID：{}", id.toString(), ex);
        }
    }

    public void processMessage(String fullShortUrl, String gid, ShortLinkStatsRecordDTO shortLinkStatsRecord) {
        fullShortUrl = Optional.ofNullable(fullShortUrl).orElse(shortLinkStatsRecord.getFullShortUrl());
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(RedisKeyConstant.SHORT_LINK_UPDATE_GID_LOCK + fullShortUrl);
        RLock rLock = readWriteLock.readLock(); // 获取读锁
        if (!rLock.tryLock()) {
            delayShortLinkStatsProducer.send(shortLinkStatsRecord); // 获取读锁失败，说明有写操作在进行，异步处理统计数据
            return;
        }
        try {
            if (StrUtil.isBlank(gid)) {
                LambdaQueryWrapper<ShortLinkRouteDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkRouteDO.class).eq(ShortLinkRouteDO::getFullShortUrl, fullShortUrl);
                ShortLinkRouteDO shortLinkRouteDO = shortLinkRouteMapper.selectOne(queryWrapper);
                gid = shortLinkRouteDO.getGid();
            }
            int hour = DateUtil.hour(new Date(), true);
            Week week = DateUtil.dayOfWeekEnum(new Date());
            int weekValue = week.getIso8601Value();
            LinkAccessStatsDO linkAccessStatsDO = LinkAccessStatsDO.builder().pv(1).uv(shortLinkStatsRecord.getUvFirstFlag() ? 1 : 0) // 只有在首次访问时才计为1
                    .uip(shortLinkStatsRecord.getUipFirstFlag() ? 1 : 0) // 只有在首次访问时才计为1
                    .hour(hour).weekday(weekValue).fullShortUrl(fullShortUrl).gid(gid).date(new Date()).build();
            linkAccessStatsMapper.shortLinkStats(linkAccessStatsDO);
            Map<String, Object> localeParamMap = new HashMap<>(); // 位置统计参数
            localeParamMap.put("key", statsLocaleAmapKey);
            localeParamMap.put("ip", shortLinkStatsRecord.getRemoteAddr());
            String localeResultStr = HttpUtil.get(ShortLinkConstant.AMAP_REMOTE_URL, localeParamMap);
            JSONObject localeResultObj = JSON.parseObject(localeResultStr);
            String infoCode = localeResultObj.getString("infocode");
            String actualProvince = "未知";
            String actualCity = "未知";
            if (StrUtil.isNotBlank(infoCode) && StrUtil.equals(infoCode, "10000")) {
                String province = localeResultObj.getString("province");
                boolean unknownFlag = StrUtil.equals(province, "[]");
                LinkLocaleStatsDO linkLocaleStatsDO = LinkLocaleStatsDO.builder().province(actualProvince = unknownFlag ? actualProvince : province).city(actualCity = unknownFlag ? actualCity : localeResultObj.getString("city")).adcode(unknownFlag ? "未知" : localeResultObj.getString("adcode")).cnt(1).fullShortUrl(fullShortUrl).country("中国").gid(gid).date(new Date()).build();
                linkLocaleStatsMapper.shortLinkLocaleStats(linkLocaleStatsDO);
            }
            LinkOsStatsDO linkOsStatsDO = LinkOsStatsDO.builder().os(shortLinkStatsRecord.getOs()).cnt(1).gid(gid).fullShortUrl(fullShortUrl).date(new Date()).build();
            linkOsStatsMapper.shortLinkOsStats(linkOsStatsDO);
            LinkBrowserStatsDO linkBrowserStatsDO = LinkBrowserStatsDO.builder().browser(shortLinkStatsRecord.getBrowser()).cnt(1).gid(gid).fullShortUrl(fullShortUrl).date(new Date()).build();
            linkBrowserStatsMapper.shortLinkBrowserStats(linkBrowserStatsDO);
            LinkDeviceStatsDO linkDeviceStatsDO = LinkDeviceStatsDO.builder().device(shortLinkStatsRecord.getDevice()).cnt(1).gid(gid).fullShortUrl(fullShortUrl).date(new Date()).build();
            linkDeviceStatsMapper.shortLinkDeviceStats(linkDeviceStatsDO);
            LinkNetworkStatsDO linkNetworkStatsDO = LinkNetworkStatsDO.builder().network(shortLinkStatsRecord.getNetwork()).cnt(1).gid(gid).fullShortUrl(fullShortUrl).date(new Date()).build();
            linkNetworkStatsMapper.shortLinkNetworkStats(linkNetworkStatsDO);
            LinkAccessLogsDO linkAccessLogsDO = LinkAccessLogsDO.builder().user(shortLinkStatsRecord.getUv()).ip(shortLinkStatsRecord.getRemoteAddr()).browser(shortLinkStatsRecord.getBrowser()).os(shortLinkStatsRecord.getOs()).network(shortLinkStatsRecord.getNetwork()).device(shortLinkStatsRecord.getDevice()).locale(StrUtil.join("-", "中国", actualProvince, actualCity)).gid(gid).fullShortUrl(fullShortUrl).build();
            linkAccessLogsMapper.insert(linkAccessLogsDO);
            shortLinkMapper.incrementStats(gid, fullShortUrl, 1, shortLinkStatsRecord.getUvFirstFlag() ? 1 : 0, shortLinkStatsRecord.getUipFirstFlag() ? 1 : 0);
            LinkStatsTodayDO linkStatsTodayDO = LinkStatsTodayDO.builder().todayPv(1).todayUv(shortLinkStatsRecord.getUvFirstFlag() ? 1 : 0).todayUip(shortLinkStatsRecord.getUipFirstFlag() ? 1 : 0).fullShortUrl(fullShortUrl).gid(gid).date(new Date()).build();
            linkStatsTodayMapper.shortLinkTodayState(linkStatsTodayDO);
        } catch (Throwable ex) {
            log.error("短链接访问量统计异常", ex);
            throw ex;
        } finally {
            rLock.unlock();
        }
    }
}
