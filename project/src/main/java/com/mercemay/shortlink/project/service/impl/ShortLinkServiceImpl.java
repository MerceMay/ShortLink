package com.mercemay.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mercemay.shortlink.project.common.constant.RedisKeyConstant;
import com.mercemay.shortlink.project.common.convention.exception.ClientException;
import com.mercemay.shortlink.project.common.convention.exception.ServiceException;
import com.mercemay.shortlink.project.common.enums.ValidDateTypeEnum;
import com.mercemay.shortlink.project.config.RouteDomainWhiteListConfiguration;
import com.mercemay.shortlink.project.dao.entity.*;
import com.mercemay.shortlink.project.dao.mapper.*;
import com.mercemay.shortlink.project.dto.biz.ShortLinkStatsRecordDTO;
import com.mercemay.shortlink.project.dto.req.ShortLinkBatchCreateReqDTO;
import com.mercemay.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.mercemay.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.mercemay.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.mercemay.shortlink.project.dto.resp.*;
import com.mercemay.shortlink.project.mq.producer.ShortLinkStatsSaveProducer;
import com.mercemay.shortlink.project.service.ShortLinkService;
import com.mercemay.shortlink.project.util.HashUtil;
import com.mercemay.shortlink.project.util.LinkUtil;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 短链接接口实现层
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {

    private final RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter;
    private final ShortLinkRouteMapper shortLinkRouteMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;
    private final RouteDomainWhiteListConfiguration routeDomainWhiteListConfiguration;
    private final ShortLinkStatsSaveProducer shortLinkStatsSaveProducer;

    @Value("${short-link.domain.default}")
    private String createShortLinkDefaultDomain;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        verifyDomainInWhiteList(requestParam.getOriginUrl());
        String shortLinkSuffix = generateSuffix(requestParam);
        String fullShortUrl = StrBuilder.create(createShortLinkDefaultDomain)
                .append("/")
                .append(shortLinkSuffix)
                .toString();
        ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                .domain(createShortLinkDefaultDomain)
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .createdType(requestParam.getCreatedType())
                .validDateType(requestParam.getValidDateType())
                .validDate(requestParam.getValidDate())
                .describe(requestParam.getDescribe())
                .shortUri(shortLinkSuffix)
                .fullShortUrl(fullShortUrl)
                .enableStatus(0)
                .totalPv(0)
                .totalUv(0)
                .totalUip(0)
                .delTime(0L)
                .favicon(getFavicon(requestParam.getOriginUrl()))
                .build();
        ShortLinkRouteDO shortLinkRouteDO = ShortLinkRouteDO.builder()
                .fullShortUrl(fullShortUrl)
                .gid(requestParam.getGid())
                .build();
        try {
            baseMapper.insert(shortLinkDO);
            shortLinkRouteMapper.insert(shortLinkRouteDO);
        } catch (DuplicateKeyException e) {
            if (!shortUriCreateCachePenetrationBloomFilter.contains(fullShortUrl)) {
                shortUriCreateCachePenetrationBloomFilter.add(fullShortUrl);
            }
            throw new ServiceException("短链接已存在，请重试生成");
        }
        stringRedisTemplate.opsForValue().set(
                RedisKeyConstant.SHORT_LINK_ROUTE_KEY + fullShortUrl,
                requestParam.getOriginUrl(),
                LinkUtil.getLinkCacheValidDate(requestParam.getValidDate()),
                TimeUnit.MILLISECONDS);
        shortUriCreateCachePenetrationBloomFilter.add(fullShortUrl);
        return ShortLinkCreateRespDTO.builder().
                fullShortUrl("http://" + shortLinkDO.getFullShortUrl())
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .build();
    }

    @Override
    public ShortLinkCreateRespDTO createShortLinkByLock(ShortLinkCreateReqDTO requestParam) {
        verifyDomainInWhiteList(requestParam.getOriginUrl());
        String fullShortUrl;
        RLock rLock = redissonClient.getLock(RedisKeyConstant.SHORT_LINK_CREATE_BY_DISTRIBUTED_LOCK_KEY);
        rLock.lock();
        try {
            String shortLinkSuffix = generateSuffixByLock(requestParam);
            fullShortUrl = StrBuilder.create(createShortLinkDefaultDomain)
                    .append("/")
                    .append(shortLinkSuffix)
                    .toString();
            ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                    .domain(createShortLinkDefaultDomain)
                    .originUrl(requestParam.getOriginUrl())
                    .gid(requestParam.getGid())
                    .createdType(requestParam.getCreatedType())
                    .validDateType(requestParam.getValidDateType())
                    .validDate(requestParam.getValidDate())
                    .describe(requestParam.getDescribe())
                    .shortUri(shortLinkSuffix)
                    .enableStatus(0)
                    .totalPv(0)
                    .totalUv(0)
                    .totalUip(0)
                    .delTime(0L)
                    .fullShortUrl(fullShortUrl)
                    .favicon(getFavicon(requestParam.getOriginUrl()))
                    .build();
            ShortLinkRouteDO shortLinkRouteDO = ShortLinkRouteDO.builder()
                    .fullShortUrl(fullShortUrl)
                    .gid(requestParam.getGid())
                    .build();
            try {
                baseMapper.insert(shortLinkDO);
                shortLinkRouteMapper.insert(shortLinkRouteDO);
            } catch (DuplicateKeyException e) {
                throw new ServiceException("短链接已存在，请重试生成");
            }
            stringRedisTemplate.opsForValue().set(
                    RedisKeyConstant.SHORT_LINK_ROUTE_KEY + fullShortUrl,
                    requestParam.getOriginUrl(),
                    LinkUtil.getLinkCacheValidDate(requestParam.getValidDate()),
                    TimeUnit.MILLISECONDS);
        } finally {
            rLock.unlock();
        }
        return ShortLinkCreateRespDTO.builder()
                .fullShortUrl("http://" + fullShortUrl)
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .build();
    }

    @Override
    public ShortLinkBatchCreateRespDTO batchCreateShortLink(ShortLinkBatchCreateReqDTO requestParam) {
        List<String> originUrls = requestParam.getOriginUrls();
        List<String> describes = requestParam.getDescribes();
        List<ShortLinkBaseInfoRespDTO> result = new ArrayList<>();
        for (int i = 0; i < originUrls.size(); i++) {
            ShortLinkCreateReqDTO shortLinkCreateReqDTO = BeanUtil.toBean(requestParam, ShortLinkCreateReqDTO.class);
            shortLinkCreateReqDTO.setOriginUrl(originUrls.get(i));
            shortLinkCreateReqDTO.setDescribe(describes.get(i));
            try {
                ShortLinkCreateRespDTO shortLinkCreateRespDTO = createShortLink(shortLinkCreateReqDTO);
                result.add(
                        ShortLinkBaseInfoRespDTO.builder()
                                .fullShortUrl(shortLinkCreateRespDTO.getFullShortUrl())
                                .originUrl(shortLinkCreateRespDTO.getOriginUrl())
                                .describe(describes.get(i))
                                .build()
                );
            } catch (Throwable ex) {
                log.error("批量创建短链接异常，原始链接：{}", originUrls.get(i));
            }
        }
        return ShortLinkBatchCreateRespDTO.builder()
                .total(result.size())
                .baseLinkInfos(result)
                .build();
    }

    @Override
    public IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam) {
        IPage<ShortLinkDO> resultPage = baseMapper.pageShortLink(requestParam);
        return resultPage.convert(each -> {
            ShortLinkPageRespDTO result = BeanUtil.toBean(each, ShortLinkPageRespDTO.class);
            result.setDomain("http://" + result.getDomain());
            return result;
        });
    }

    @Override
    public List<ShortLinkGroupCountQueryRespDTO> listShortLinkGroupCount(List<String> requestParam) {
        QueryWrapper<ShortLinkDO> queryWrapper = Wrappers.query(new ShortLinkDO())
                .select("gid, count(*) as shortLinkCount")
                .in("gid", requestParam)
                .eq("enable_status", 0)
                .eq("del_flag", 0)
                .eq("del_time", 0L)
                .groupBy("gid");
        List<Map<String, Object>> resultMaps = baseMapper.selectMaps(queryWrapper);
        return BeanUtil.copyToList(resultMaps, ShortLinkGroupCountQueryRespDTO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateShortLink(ShortLinkUpdateReqDTO requestParam) {
        verifyDomainInWhiteList(requestParam.getOriginUrl());
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getOriginGid())
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0);
        ShortLinkDO existingShortLink = baseMapper.selectOne(queryWrapper);
        if (existingShortLink == null) {
            throw new ClientException("短链接记录不存在");
        }
        if (Objects.equals(existingShortLink.getGid(), requestParam.getGid())) { // 如果分组没有变化，直接更新其他字段
            LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                    .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                    .eq(ShortLinkDO::getGid, requestParam.getGid())
                    .eq(ShortLinkDO::getEnableStatus, 0)
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .set(Objects.equals(requestParam.getValidDateType(), ValidDateTypeEnum.PERMANENT.getType()), ShortLinkDO::getValidDate, null);
            ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                    .domain(existingShortLink.getDomain())
                    .shortUri(existingShortLink.getShortUri())
                    .favicon(Objects.equals(requestParam.getOriginUrl(), existingShortLink.getOriginUrl()) ? existingShortLink.getFavicon() : getFavicon(requestParam.getOriginUrl()))
                    .createdType(existingShortLink.getCreatedType())
                    .gid(requestParam.getGid())
                    .originUrl(requestParam.getOriginUrl())
                    .describe(requestParam.getDescribe())
                    .validDateType(requestParam.getValidDateType())
                    .validDate(requestParam.getValidDate())
                    .build();
            baseMapper.update(shortLinkDO, updateWrapper);
        } else { // 如果分组变化了，需要加分布式锁，防止并发修改
            RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(RedisKeyConstant.SHORT_LINK_UPDATE_GID_LOCK + requestParam.getFullShortUrl()); // 获取分布式读写锁
            RLock rLock = readWriteLock.writeLock(); // 获取写锁
            rLock.lock();
            try {
                LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                        .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                        .eq(ShortLinkDO::getGid, requestParam.getGid())
                        .eq(ShortLinkDO::getDelFlag, 0)
                        .eq(ShortLinkDO::getDelTime, 0L)
                        .eq(ShortLinkDO::getEnableStatus, 0);
                ShortLinkDO deletedShortLinkDO = ShortLinkDO.builder()
                        .delTime(System.currentTimeMillis())
                        .build();
                deletedShortLinkDO.setDelFlag(1); // 逻辑删除
                baseMapper.update(deletedShortLinkDO, updateWrapper);
                ShortLinkDO newShortLinkDO = ShortLinkDO.builder()
                        .domain(createShortLinkDefaultDomain)
                        .originUrl(requestParam.getOriginUrl())
                        .gid(requestParam.getGid())
                        .createdType(existingShortLink.getCreatedType())
                        .validDateType(requestParam.getValidDateType())
                        .validDate(requestParam.getValidDate())
                        .describe(requestParam.getDescribe())
                        .shortUri(existingShortLink.getShortUri())
                        .enableStatus(existingShortLink.getEnableStatus())
                        .totalPv(existingShortLink.getTotalPv())
                        .totalUv(existingShortLink.getTotalUv())
                        .totalUip(existingShortLink.getTotalUip())
                        .fullShortUrl(existingShortLink.getFullShortUrl())
                        .favicon(Objects.equals(requestParam.getOriginUrl(), existingShortLink.getOriginUrl()) ? existingShortLink.getFavicon() : getFavicon(requestParam.getOriginUrl()))
                        .delTime(0L)
                        .build();
                baseMapper.insert(newShortLinkDO); // 插入新记录
                // 更新表：t_link_route
                LambdaQueryWrapper<ShortLinkRouteDO> shortLinkRouteDOLambdaQueryWrapper = Wrappers.lambdaQuery(ShortLinkRouteDO.class)
                        .eq(ShortLinkRouteDO::getFullShortUrl, requestParam.getFullShortUrl())
                        .eq(ShortLinkRouteDO::getGid, existingShortLink.getGid());
                ShortLinkRouteDO shortLinkRouteDO = shortLinkRouteMapper.selectOne(shortLinkRouteDOLambdaQueryWrapper);
                shortLinkRouteMapper.delete(shortLinkRouteDOLambdaQueryWrapper);
                shortLinkRouteDO.setGid(requestParam.getGid());
                shortLinkRouteMapper.insert(shortLinkRouteDO);
            } finally {
                rLock.unlock();
            }
        }
        if (!Objects.equals(existingShortLink.getValidDateType(), requestParam.getValidDateType()) // 如果有效期类型、有效期或者跳转链接有变化，删除缓存
                || !Objects.equals(existingShortLink.getValidDate(), requestParam.getValidDate())
                || !Objects.equals(existingShortLink.getOriginUrl(), requestParam.getOriginUrl())) {
            stringRedisTemplate.delete(RedisKeyConstant.SHORT_LINK_ROUTE_KEY + requestParam.getFullShortUrl());
            Date currentDate = new Date();
            if (existingShortLink.getValidDate() != null && existingShortLink.getValidDate().before(currentDate)) { // 如果之前是过期的短链接，现在更新后变成了永久或未过期的，删除空值缓存
                if (Objects.equals(requestParam.getValidDateType(), ValidDateTypeEnum.PERMANENT.getType()) || requestParam.getValidDate().after(currentDate)) {
                    stringRedisTemplate.delete(RedisKeyConstant.SHORT_LINK_NULL_ROUTE_KEY + requestParam.getFullShortUrl());
                }
            }
        }
    }

    @SneakyThrows
    @Override
    public void redirectUrl(String shortUri, ServletRequest request, ServletResponse response) {
        String serverName = request.getServerName();
        String serverPort = Optional.of(request.getServerPort())
                .filter(each -> !Objects.equals(each, 80))
                .map(String::valueOf)
                .map(each -> ":" + each)
                .orElse("");
        String fullShortUrl = serverName + serverPort + "/" + shortUri;
        String originLink = stringRedisTemplate.opsForValue().get(RedisKeyConstant.SHORT_LINK_ROUTE_KEY + fullShortUrl);
        if (StrUtil.isNotBlank(originLink)) { // 缓存存在，直接返回
            shortLinkStats(buildLinkStatsRecordAndSetUser(fullShortUrl, request, response)); // 统计短链接访问数据
            ((HttpServletResponse) response).sendRedirect(originLink);
            return;
        }
        boolean contains = shortUriCreateCachePenetrationBloomFilter.contains(fullShortUrl); // 布隆过滤器判断是否存在该短链接
        if (!contains) { // 布隆过滤器不存在该短链接，直接返回未找到
            ((HttpServletResponse) response).sendRedirect("/page/notfound");
            return;
        }
        // 布隆过滤器存在，有两种可能，缓存击穿或短链接真实存在，继续往下走
        String nullShortLink = stringRedisTemplate.opsForValue().get(RedisKeyConstant.SHORT_LINK_NULL_ROUTE_KEY + fullShortUrl); // 如果有空值缓存，直接返回未找到
        if (StrUtil.isNotBlank(nullShortLink)) {
            ((HttpServletResponse) response).sendRedirect("/page/notfound");
            return;
        }
        // 没有空值缓存，可能是缓存击穿，使用分布式锁加载缓存
        RLock lock = redissonClient.getLock(RedisKeyConstant.SHORT_LINK_ROUTE_LOCK + fullShortUrl);
        lock.lock();
        try {
            originLink = stringRedisTemplate.opsForValue().get(RedisKeyConstant.SHORT_LINK_ROUTE_KEY + fullShortUrl); // 重复检查缓存
            if (StrUtil.isNotBlank(originLink)) { // 如果缓存存在，说明被其他线程已经加载到缓存，直接返回
                shortLinkStats(buildLinkStatsRecordAndSetUser(fullShortUrl, request, response));
                ((HttpServletResponse) response).sendRedirect(originLink);
                return;
            }
            nullShortLink = stringRedisTemplate.opsForValue().get(RedisKeyConstant.SHORT_LINK_NULL_ROUTE_KEY + fullShortUrl); // 再次检查空值缓存
            if (StrUtil.isNotBlank(nullShortLink)) {
                ((HttpServletResponse) response).sendRedirect("/page/notfound");
                return;
            }
            LambdaQueryWrapper<ShortLinkRouteDO> shortLinkRouteDOLambdaQueryWrapper = Wrappers.lambdaQuery(ShortLinkRouteDO.class)
                    .eq(ShortLinkRouteDO::getFullShortUrl, fullShortUrl);
            ShortLinkRouteDO shortLinkRouteDO = shortLinkRouteMapper.selectOne(shortLinkRouteDOLambdaQueryWrapper);
            if (shortLinkRouteDO == null) { // 路由表没有记录，说明短链接不存在
                stringRedisTemplate.opsForValue().set(RedisKeyConstant.SHORT_LINK_NULL_ROUTE_KEY + fullShortUrl, "-", 30, TimeUnit.MINUTES); // 设置空值缓存，防止缓存穿透
                ((HttpServletResponse) response).sendRedirect("/page/notfound");
                return;
            }
            LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getGid, shortLinkRouteDO.getGid())
                    .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                    .eq(ShortLinkDO::getEnableStatus, 0)
                    .eq(ShortLinkDO::getDelFlag, 0);
            ShortLinkDO shortLinkDO = baseMapper.selectOne(queryWrapper);
            if (shortLinkDO == null || (shortLinkDO.getValidDate() != null && shortLinkDO.getValidDate().before(new Date()))) { // 短链接记录不存在或已过期
                stringRedisTemplate.opsForValue().set(RedisKeyConstant.SHORT_LINK_NULL_ROUTE_KEY + fullShortUrl, "-", 30, TimeUnit.MINUTES);
                ((HttpServletResponse) response).sendRedirect("/page/notfound");
                return;
            }
            stringRedisTemplate.opsForValue().set( // 短链接存在且并没有过期，加载到缓存
                    RedisKeyConstant.SHORT_LINK_ROUTE_KEY + fullShortUrl,
                    shortLinkDO.getOriginUrl(),
                    LinkUtil.getLinkCacheValidDate(shortLinkDO.getValidDate()),
                    TimeUnit.MILLISECONDS);
            shortLinkStats(buildLinkStatsRecordAndSetUser(fullShortUrl, request, response));
            ((HttpServletResponse) response).sendRedirect(shortLinkDO.getOriginUrl());
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void shortLinkStats(ShortLinkStatsRecordDTO shortLinkStatsRecord) {
        Map<String, String> produceMessage = new HashMap<>();
        produceMessage.put("statsRecord", JSON.toJSONString(shortLinkStatsRecord));
        shortLinkStatsSaveProducer.send(produceMessage);
    }

    private ShortLinkStatsRecordDTO buildLinkStatsRecordAndSetUser(String fullShortUrl, ServletRequest request, ServletResponse response) {
        AtomicBoolean uvFirstFlag = new AtomicBoolean();
        Cookie[] cookies = ((HttpServletRequest) request).getCookies();
        AtomicReference<String> uv = new AtomicReference<>();
        // 当前请求没有uv Cookie时，生成一个新的uv Cookie并添加到响应中
        Runnable addResponseCookieTask = () -> {
            uv.set(UUID.fastUUID().toString());
            Cookie uvCookie = new Cookie("uv", uv.get());
            uvCookie.setMaxAge(30 * 60 * 60 * 24); // 30天
            uvCookie.setPath(StrUtil.sub(fullShortUrl, fullShortUrl.indexOf("/"), fullShortUrl.length())); // 设置 Cookie 的作用路径为根路径
            ((HttpServletResponse) response).addCookie(uvCookie);
            uvFirstFlag.set(Boolean.TRUE);
            stringRedisTemplate.opsForSet().add(RedisKeyConstant.STATS_UV_KEY + fullShortUrl, uv.get()); // 将访客标识存储到 Redis 中
        };
        if (ArrayUtil.isNotEmpty(cookies)) { // 如果请求中有 Cookie，分两种，有 uv Cookie 和没有 uv Cookie
            Arrays.stream(cookies)
                    .filter(each -> Objects.equals(each.getName(), "uv"))
                    .findFirst()
                    .map(Cookie::getValue)
                    .ifPresentOrElse(each -> { // 如果存在 uv Cookie
                        uv.set(each); // 获取 Cookie 的值
                        Long uvAdded = stringRedisTemplate.opsForSet().add(RedisKeyConstant.STATS_UV_KEY + fullShortUrl, each);
                        uvFirstFlag.set(uvAdded != null && uvAdded > 0L); // 如果添加成功，表示是首次访问
                    }, addResponseCookieTask); // 如果不存在 uv Cookie，执行添加响应 Cookie 的任务
        } else { // 如果请求中没有任何 Cookie
            addResponseCookieTask.run();
        }
        String remoteAddr = LinkUtil.getActualIp((HttpServletRequest) request);
        String os = LinkUtil.getOs((HttpServletRequest) request);
        String browser = LinkUtil.getBrowser((HttpServletRequest) request);
        String device = LinkUtil.getDevice((HttpServletRequest) request);
        String network = LinkUtil.getNetwork((HttpServletRequest) request);
        Long uipAdded = stringRedisTemplate.opsForSet().add(RedisKeyConstant.STATS_UIP_KEY + fullShortUrl, remoteAddr); // 将访客 IP 存储到 Redis 中
        boolean uipFirstFlag = uipAdded != null && uipAdded > 0L;
        return ShortLinkStatsRecordDTO.builder()
                .fullShortUrl(fullShortUrl)
                .uv(uv.get())
                .uvFirstFlag(uvFirstFlag.get())
                .uipFirstFlag(uipFirstFlag)
                .remoteAddr(remoteAddr)
                .os(os)
                .browser(browser)
                .device(device)
                .network(network)
                .currentDate(new Date())
                .build();
    }


    private String generateSuffix(ShortLinkCreateReqDTO requestParam) {
        int retryCount = 0;
        String shortUri;
        while (true) {
            if (retryCount > 10) {
                throw new ServiceException("短链接生成失败，请稍后重试");
            }
            String originUrl = requestParam.getOriginUrl();
            originUrl += UUID.randomUUID().toString();
            shortUri = HashUtil.hashToBase62(originUrl);
            if (!shortUriCreateCachePenetrationBloomFilter.contains(createShortLinkDefaultDomain + "/" + shortUri)) {
                break;
            }
            retryCount++;
        }
        return shortUri;
    }

    private String generateSuffixByLock(ShortLinkCreateReqDTO requestParam) {
        int retryCount = 0;
        String shortUri;
        while (true) {
            if (retryCount > 10) {
                throw new ServiceException("短链接生成失败，请稍后重试");
            }
            String originUrl = requestParam.getOriginUrl();
            originUrl += UUID.randomUUID().toString();
            shortUri = HashUtil.hashToBase62(originUrl);
            LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getGid, requestParam.getGid())
                    .eq(ShortLinkDO::getFullShortUrl, createShortLinkDefaultDomain + "/" + shortUri)
                    .eq(ShortLinkDO::getDelFlag, 0);
            ShortLinkDO shortLinkDO = baseMapper.selectOne(queryWrapper);
            if (shortLinkDO == null) {
                break;
            }
            retryCount++;
        }
        return shortUri;
    }

    @SneakyThrows
    private String getFavicon(String url) {
        URL targetUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            Document document = Jsoup.connect(url).get();
            Element faviconLink = document.select("link[rel~=(?i)^(shortcut )?icon]").first();
            if (faviconLink != null) {
                return faviconLink.attr("abs:href");
            }
        }
        return null;
    }

    private void verifyDomainInWhiteList(String originUrl) {
        Boolean enable = routeDomainWhiteListConfiguration.getEnable();
        if (enable == null || !enable) {
            return;
        }
        String domain = LinkUtil.extractDomain(originUrl);
        if (StrUtil.isBlank(domain)) {
            throw new ClientException("跳转链接填写错误，请检查后重新填写");
        }
        List<String> details = routeDomainWhiteListConfiguration.getDetails();
        if (!details.contains(domain)) {
            throw new ClientException("跳转链接只能不能跳转到白名单内，请生成以下网站跳转链接：" + routeDomainWhiteListConfiguration.getNames());
        }
    }
}
