package com.mercemay.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.StrUtil;
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
import com.mercemay.shortlink.project.dao.entity.ShortLinkDO;
import com.mercemay.shortlink.project.dao.entity.ShortLinkRouteDO;
import com.mercemay.shortlink.project.dao.mapper.ShortLinkMapper;
import com.mercemay.shortlink.project.dao.mapper.ShortLinkRouteMapper;
import com.mercemay.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.mercemay.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.mercemay.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.mercemay.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.mercemay.shortlink.project.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.mercemay.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.mercemay.shortlink.project.service.ShortLinkService;
import com.mercemay.shortlink.project.util.HashUtil;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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

    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        String shortLinkSuffix = generateSuffix(requestParam);
        String fullShortUrl = StrBuilder.create(requestParam.getDomain())
                .append("/")
                .append(shortLinkSuffix)
                .toString();
        ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                .domain(requestParam.getDomain())
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .createdType(requestParam.getCreatedType())
                .validDateType(requestParam.getValidDateType())
                .validDate(requestParam.getValidDate())
                .describe(requestParam.getDescribe())
                .shortUri(shortLinkSuffix)
                .fullShortUrl(fullShortUrl)
                .enableStatus(0)
                .build();
        ShortLinkRouteDO shortLinkRouteDO = ShortLinkRouteDO.builder()
                .fullShortUrl(fullShortUrl)
                .gid(requestParam.getGid())
                .build();
        try {
            baseMapper.insert(shortLinkDO);
            shortLinkRouteMapper.insert(shortLinkRouteDO);
        } catch (DuplicateKeyException e) {
            LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getFullShortUrl, fullShortUrl);
            ShortLinkDO existingShortLink = baseMapper.selectOne(queryWrapper);
            if (existingShortLink != null) {
                log.warn("短链接：{} 已存在", fullShortUrl);
                throw new ServiceException("短链接已存在，请稍后重试");
            }
        }
        shortUriCreateCachePenetrationBloomFilter.add(fullShortUrl);
        return ShortLinkCreateRespDTO.builder().
                fullShortUrl("http://" + shortLinkDO.getFullShortUrl())
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .build();
    }

    @Override
    public IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getEnableStatus, 0)
                .eq(ShortLinkDO::getDelFlag, 0)
                .orderByDesc(ShortLinkDO::getCreateTime);
        IPage<ShortLinkDO> resultPage = baseMapper.selectPage(requestParam, queryWrapper);
        return resultPage.convert(each -> {
            ShortLinkPageRespDTO result = BeanUtil.toBean(each, ShortLinkPageRespDTO.class);
            result.setFullShortUrl("http://" + each.getFullShortUrl());
            return result;
        });
    }

    @Override
    public List<ShortLinkGroupCountQueryRespDTO> listShortLinkGroupCount(List<String> requestParam) {
        QueryWrapper<ShortLinkDO> queryWrapper = Wrappers.<ShortLinkDO>query()
                .select("gid, count(*) as shortLinkCount")
                .in("gid", requestParam)
                .eq("enable_status", 0)
                .eq("del_flag", 0)
                .groupBy("gid");
        List<Map<String, Object>> resultMaps = baseMapper.selectMaps(queryWrapper);
        return BeanUtil.copyToList(resultMaps, ShortLinkGroupCountQueryRespDTO.class);
    }

    @Transactional
    @Override
    public void updateShortLink(ShortLinkUpdateReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0);
        ShortLinkDO existingShortLink = baseMapper.selectOne(queryWrapper);
        if (existingShortLink == null) {
            throw new ClientException("短链接记录不存在");
        }
        ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                .domain(existingShortLink.getDomain())
                .shortUri(existingShortLink.getShortUri())
                .clickNum(existingShortLink.getClickNum())
                .favicon(existingShortLink.getFavicon())
                .createdType(existingShortLink.getCreatedType())
                .gid(requestParam.getGid())
                .originUrl(requestParam.getOriginUrl())
                .describe(requestParam.getDescribe())
                .validDateType(requestParam.getValidDateType())
                .validDate(requestParam.getValidDate())
                .build();
        if (Objects.equals(existingShortLink.getGid(), requestParam.getGid())) {
            LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                    .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                    .eq(ShortLinkDO::getGid, requestParam.getGid())
                    .eq(ShortLinkDO::getEnableStatus, 0)
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .set(Objects.equals(requestParam.getValidDateType(), ValidDateTypeEnum.PERMANENT.getType()), ShortLinkDO::getValidDate, null);
            baseMapper.update(shortLinkDO, updateWrapper);
        } else {
            LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                    .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                    .eq(ShortLinkDO::getGid, existingShortLink.getGid())
                    .eq(ShortLinkDO::getEnableStatus, 0)
                    .eq(ShortLinkDO::getDelFlag, 0);
            baseMapper.delete(updateWrapper);
            baseMapper.insert(shortLinkDO);
        }
    }

    @SneakyThrows
    @Override
    public void redirectUrl(String shortUri, ServletRequest request, ServletResponse response) {
        String serverName = request.getServerName();
        String fullShortUrl = serverName + "/" + shortUri;
        String originLink = stringRedisTemplate.opsForValue().get(RedisKeyConstant.ROUTE_SHORT_LINK_KEY + fullShortUrl);
        if (StrUtil.isNotBlank(originLink)) {
            ((HttpServletResponse) response).sendRedirect(originLink);
            return;
        }
        boolean contains = shortUriCreateCachePenetrationBloomFilter.contains(fullShortUrl);
        if (!contains) {
            return;
        }
        String nullShortLink = stringRedisTemplate.opsForValue().get(RedisKeyConstant.ROUTE_SHORT_LINK_IS_NULL + fullShortUrl);
        if (StrUtil.isNotBlank(nullShortLink)) {
            return;
        }
        RLock lock = redissonClient.getLock(RedisKeyConstant.LOCK_ROUTE_SHORT_LINK_KEY + fullShortUrl);
        lock.lock();
        try {
            originLink = stringRedisTemplate.opsForValue().get(RedisKeyConstant.ROUTE_SHORT_LINK_KEY + fullShortUrl);
            if (StrUtil.isNotBlank(originLink)) {
                ((HttpServletResponse) response).sendRedirect(originLink);
                return;
            }
            LambdaQueryWrapper<ShortLinkRouteDO> shortLinkRouteDOLambdaQueryWrapper = Wrappers.lambdaQuery(ShortLinkRouteDO.class)
                    .eq(ShortLinkRouteDO::getFullShortUrl, fullShortUrl);
            ShortLinkRouteDO shortLinkRouteDO = shortLinkRouteMapper.selectOne(shortLinkRouteDOLambdaQueryWrapper);
            if (shortLinkRouteDO == null) {
                stringRedisTemplate.opsForValue().set(RedisKeyConstant.ROUTE_SHORT_LINK_IS_NULL + fullShortUrl, "-", 30, TimeUnit.MINUTES);
                return;
            }
            LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getGid, shortLinkRouteDO.getGid())
                    .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                    .eq(ShortLinkDO::getEnableStatus, 0)
                    .eq(ShortLinkDO::getDelFlag, 0);
            ShortLinkDO shortLinkDO = baseMapper.selectOne(queryWrapper);
            if (shortLinkDO != null) {
                stringRedisTemplate.opsForValue().set(RedisKeyConstant.ROUTE_SHORT_LINK_KEY + fullShortUrl, shortLinkDO.getOriginUrl());
                ((HttpServletResponse) response).sendRedirect(shortLinkDO.getOriginUrl());
            }
        } finally {
            lock.unlock();
        }
    }


    private String generateSuffix(ShortLinkCreateReqDTO requestParam) {
        int retryCount = 0;
        String shortUri;
        while (true) {
            if (retryCount > 10) {
                throw new ServiceException("短链接生成失败，请稍后重试");
            }
            String originUrl = requestParam.getOriginUrl();
            originUrl += System.currentTimeMillis();
            shortUri = HashUtil.hashToBase62(originUrl);
            if (!shortUriCreateCachePenetrationBloomFilter.contains(requestParam.getDomain() + "/" + shortUri)) {
                break;
            }
            retryCount++;
        }
        return shortUri;
    }
}
