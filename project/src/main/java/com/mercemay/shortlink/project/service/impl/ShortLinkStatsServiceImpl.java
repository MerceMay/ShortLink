package com.mercemay.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mercemay.shortlink.project.dao.entity.*;
import com.mercemay.shortlink.project.dao.mapper.*;
import com.mercemay.shortlink.project.dto.req.ShortLinkStatsAccessRecordReqDTO;
import com.mercemay.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import com.mercemay.shortlink.project.dto.resp.*;
import com.mercemay.shortlink.project.service.ShortLinkStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class ShortLinkStatsServiceImpl implements ShortLinkStatsService {

    private final LinkAccessStatsMapper linkAccessStatsMapper;
    private final LinkLocaleStatsMapper linkLocaleStatsMapper;
    private final LinkAccessLogsMapper linkAccessLogsMapper;
    private final LinkBrowserStatsMapper linkBrowserStatsMapper;
    private final LinkOsStatsMapper linkOsStatsMapper;
    private final LinkDeviceStatsMapper linkDeviceStatsMapper;
    private final LinkNetworkStatsMapper linkNetworkStatsMapper;

    @Override
    public ShortLinkStatsRespDTO getShortLinkStats(ShortLinkStatsReqDTO requestParam) {
        List<LinkAccessStatsDO> LinkAccessStatsDOList = linkAccessStatsMapper.listStatsByShortLink(requestParam);
        if (CollUtil.isEmpty(LinkAccessStatsDOList)) {
            return null;
        }
        // 基础访问详情
        List<ShortLinkStatsAccessDailyRespDTO> dailyRespDTOList = new ArrayList<>();
        List<String> rangeDates = DateUtil.rangeToList(DateUtil.parse(requestParam.getStartDate()), DateUtil.parse(requestParam.getEndDate()), DateField.DAY_OF_MONTH).stream() // 获取日期范围内的所有日期
                .map(DateUtil::formatDate) // 格式化为字符串
                .toList(); // 转换为字符串列表
        rangeDates.forEach(each -> LinkAccessStatsDOList.stream()
                .filter(item -> Objects.equals(each, DateUtil.formatDate(item.getDate()))) // 找到对应日期的数据
                .findFirst()
                .ifPresentOrElse(
                        item -> { // 如果本日期存在，添加对应数据
                            dailyRespDTOList.add(
                                    ShortLinkStatsAccessDailyRespDTO.builder()
                                            .date(each)
                                            .pv(item.getPv())
                                            .uv(item.getUv())
                                            .uip(item.getUip())
                                            .build()
                            );
                        }, () -> { // 如果本日期不存在，添加0数据
                            dailyRespDTOList.add(
                                    ShortLinkStatsAccessDailyRespDTO.builder()
                                            .date(each)
                                            .pv(0)
                                            .uv(0)
                                            .uip(0)
                                            .build()
                            );
                        }));
        // 地区访问详情（仅国内）
        List<ShortLinkStatsLocaleCNRespDTO> localeCNRespDTOList = new ArrayList<>();
        List<LinkLocaleStatsDO> linkLocaleStatsDOList = linkLocaleStatsMapper.listLocaleByShortLink(requestParam); // 获取地区访问数据
        int totalLocaleCnt = linkLocaleStatsDOList.stream().mapToInt(LinkLocaleStatsDO::getCnt).sum(); // 获取总访问量
        linkLocaleStatsDOList.forEach(each -> {
            double percentage = (double) each.getCnt() / totalLocaleCnt; // 计算百分比
            double ratio = Math.round(percentage * 100.0) / 100.0; // 保留两位小数
            localeCNRespDTOList.add(
                    ShortLinkStatsLocaleCNRespDTO.builder()
                            .cnt(each.getCnt())
                            .locale(each.getProvince())
                            .ratio(ratio)
                            .build());
        });
        // 小时访问详情
        List<Integer> hourStats = new ArrayList<>();
        List<LinkAccessStatsDO> hourStatsList = linkAccessStatsMapper.listHourStatsByShortLink(requestParam);
        for (int i = 0; i < 24; i++) {
            AtomicInteger hour = new AtomicInteger(i);
            int hourCnt = hourStatsList.stream()
                    .filter(each -> Objects.equals(each.getHour(), hour.get())) // 找到对应小时的数据
                    .findFirst()
                    .map(LinkAccessStatsDO::getPv) // 获取访问量
                    .orElse(0); // 如果没有数据则为0
            hourStats.add(hourCnt);
        }
        // 高频访问IP详情
        List<ShortLinkStatsTopIpRespDTO> topIpRespDTOList = new ArrayList<>();
        List<HashMap<String, Object>> topIpList = linkAccessLogsMapper.listTopIpByShortLink(requestParam);
        topIpList.forEach(each ->
                topIpRespDTOList.add(
                        ShortLinkStatsTopIpRespDTO.builder()
                                .ip(each.get("ip").toString())
                                .cnt(Integer.parseInt(each.get("count").toString()))
                                .build()
                ));
        // 一周访问详情
        List<Integer> weekdayStats = new ArrayList<>();
        List<LinkAccessStatsDO> weekdayStatsList = linkAccessStatsMapper.listWeekdayStatsByShortLink(requestParam);
        for (int i = 1; i < 8; i++) {
            AtomicInteger weekday = new AtomicInteger(i);
            int weekdayCnt = weekdayStatsList.stream()
                    .filter(each -> Objects.equals(each.getWeekday(), weekday.get())) // 找到对应星期的数据
                    .findFirst()
                    .map(LinkAccessStatsDO::getPv) // 获取访问量
                    .orElse(0); // 如果没有数据则为0
            weekdayStats.add(weekdayCnt);
        }
        // 浏览器访问详情
        List<ShortLinkStatsBrowserRespDTO> browserRespDTOList = new ArrayList<>();
        List<HashMap<String, Object>> browserStatsList = linkBrowserStatsMapper.listBrowserStatsByShortLink(requestParam);
        int totalBrowserCnt = browserStatsList.stream()
                .mapToInt(each -> Integer.parseInt(each.get("count").toString()))
                .sum(); // 获取总访问量
        browserStatsList.forEach(each -> {
            double percentage = (double) Integer.parseInt(each.get("count").toString()) / totalBrowserCnt;
            double ratio = Math.round(percentage * 100.0) / 100.0;
            browserRespDTOList.add(
                    ShortLinkStatsBrowserRespDTO.builder()
                            .cnt(Integer.parseInt(each.get("count").toString()))
                            .browser(each.get("browser").toString())
                            .ratio(ratio)
                            .build()
            );
        });
        // 操作系统访问详情
        List<ShortLinkStatsOsRespDTO> osRespDTOList = new ArrayList<>();
        List<HashMap<String, Object>> osStatsList = linkOsStatsMapper.listOsStatsByShortLink(requestParam);
        int totalOsCnt = osStatsList.stream()
                .mapToInt(each -> Integer.parseInt(each.get("count").toString()))
                .sum(); // 获取总访问量
        osStatsList.forEach(each -> {
            double percentage = (double) Integer.parseInt(each.get("count").toString()) / totalOsCnt;
            double ratio = Math.round(percentage * 100.0) / 100.0;
            osRespDTOList.add(
                    ShortLinkStatsOsRespDTO.builder()
                            .cnt(Integer.parseInt(each.get("count").toString()))
                            .os(each.get("os").toString())
                            .ratio(ratio)
                            .build()
            );
        });
        // 访客访问类型详情
        List<ShortLinkStatsUvRespDTO> uvRespDTOList = new ArrayList<>();
        HashMap<String, Object> uvTypeCntMap = linkAccessLogsMapper.findUvTypeCntByShortLink(requestParam);
        int oldUserCnt = Integer.parseInt(
                Optional.ofNullable(uvTypeCntMap)
                        .map(each -> each.get("oldUserCnt")) // 获取老访客数量
                        .map(Object::toString) // 转为字符串
                        .orElse("0")); // 如果没有数据则为0
        int newUserCnt = Integer.parseInt(
                Optional.ofNullable(uvTypeCntMap)
                        .map(each -> each.get("newUserCnt")) // 获取新访客数量
                        .map(Object::toString) // 转为字符串
                        .orElse("0")); // 如果没有数据则为0
        int totalUserCnt = oldUserCnt + newUserCnt;
        double oldUserPercentage = totalUserCnt == 0 ? 0.0 : (double) oldUserCnt / totalUserCnt;
        double oldUserRatio = Math.round(oldUserPercentage * 100.0) / 100.0;
        double newUserPercentage = totalUserCnt == 0 ? 0.0 : (double) newUserCnt / totalUserCnt;
        double newUserRatio = Math.round(newUserPercentage * 100.0) / 100.0;
        uvRespDTOList.add(
                ShortLinkStatsUvRespDTO.builder()
                        .uvType("newUser")
                        .cnt(newUserCnt)
                        .ratio(newUserRatio)
                        .build()
        );
        uvRespDTOList.add(
                ShortLinkStatsUvRespDTO.builder()
                        .uvType("oldUser")
                        .cnt(oldUserCnt)
                        .ratio(oldUserRatio)
                        .build()
        );
        // 设备访问详情
        List<ShortLinkStatsDeviceRespDTO> deviceRespDTOList = new ArrayList<>();
        List<LinkDeviceStatsDO> deviceStatsList = linkDeviceStatsMapper.listDeviceStatsByShortLink(requestParam);
        int totalDeviceCnt = deviceStatsList.stream()
                .mapToInt(LinkDeviceStatsDO::getCnt)
                .sum(); // 获取总访问量
        deviceStatsList.forEach(each -> {
            double percentage = (double) each.getCnt() / totalDeviceCnt;
            double ratio = Math.round(percentage * 100.0) / 100.0;
            deviceRespDTOList.add(
                    ShortLinkStatsDeviceRespDTO.builder()
                            .cnt(each.getCnt())
                            .device(each.getDevice())
                            .ratio(ratio)
                            .build()
            );
        });
        // 网络访问详情
        List<ShortLinkStatsNetworkRespDTO> networkRespDTOList = new ArrayList<>();
        List<LinkNetworkStatsDO> networkStatsList = linkNetworkStatsMapper.listNetworkStatsByShortLink(requestParam);
        int totalNetworkCnt = networkStatsList.stream()
                .mapToInt(LinkNetworkStatsDO::getCnt)
                .sum(); // 获取总访问量
        networkStatsList.forEach(each -> {
            double percentage = (double) each.getCnt() / totalNetworkCnt;
            double ratio = Math.round(percentage * 100.0) / 100.0;
            networkRespDTOList.add(
                    ShortLinkStatsNetworkRespDTO.builder()
                            .cnt(each.getCnt())
                            .network(each.getNetwork())
                            .ratio(ratio)
                            .build()
            );
        });
        return ShortLinkStatsRespDTO.builder()
                .daily(dailyRespDTOList)
                .localeCnStats(localeCNRespDTOList)
                .hourStats(hourStats)
                .topIpStats(topIpRespDTOList)
                .weekdayStats(weekdayStats)
                .browserStats(browserRespDTOList)
                .osStats(osRespDTOList)
                .uvTypeStats(uvRespDTOList)
                .deviceStats(deviceRespDTOList)
                .networkStats(networkRespDTOList)
                .build();
    }

    @Override
    public IPage<ShortLinkStatsAccessRecordRespDTO> getShortLinkAccessRecordStats(ShortLinkStatsAccessRecordReqDTO requestParam) {
        LambdaQueryWrapper<LinkAccessLogsDO> queryWrapper = Wrappers.lambdaQuery(LinkAccessLogsDO.class)
                .eq(LinkAccessLogsDO::getGid, requestParam.getGid())
                .eq(LinkAccessLogsDO::getFullShortUrl, requestParam.getFullShortUrl())
                .between(LinkAccessLogsDO::getCreateTime, requestParam.getStartDate(), requestParam.getEndDate())
                .eq(LinkAccessLogsDO::getDelFlag, 0)
                .orderByDesc(LinkAccessLogsDO::getCreateTime); // 这个总体的查询条件为：根据gid、fullShortUrl、createTime范围和未删除标志进行过滤，并按创建时间降序排序
        IPage<LinkAccessLogsDO> pageResult = linkAccessLogsMapper.selectPage(requestParam, queryWrapper); // 分页查询
        IPage<ShortLinkStatsAccessRecordRespDTO> resultPage = pageResult.convert(each -> BeanUtil.toBean(each, ShortLinkStatsAccessRecordRespDTO.class)); // 把每个LinkAccessLogsDO转换为ShortLinkStatsAccessRecordRespDTO
        List<String> userAccessLogsList = resultPage.getRecords().stream() // 获取当前页的所有用户列表
                .map(ShortLinkStatsAccessRecordRespDTO::getUser)
                .toList();
        List<Map<String, Object>> uvTypeList = linkAccessLogsMapper.selectUvTypeByUsers(
                requestParam.getGid(),
                requestParam.getFullShortUrl(),
                requestParam.getStartDate(),
                requestParam.getEndDate(),
                userAccessLogsList
        ); // 批量查询用户访客类型
        resultPage.getRecords().forEach(each -> each.setUvType(
                uvTypeList.stream()
                        .filter(item -> Objects.equals(each.getUser(), item.get("user"))) // 找到对应用户的数据
                        .findFirst()
                        .map(item -> item.get("UvType"))
                        .map(Object::toString)
                        .orElse("旧访客") // 如果没有数据则为“老访客”
        ));
        return resultPage;
    }
}
