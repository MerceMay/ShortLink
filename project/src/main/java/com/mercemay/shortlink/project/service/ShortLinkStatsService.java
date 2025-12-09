package com.mercemay.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mercemay.shortlink.project.dto.req.ShortLinkGroupStatsAccessRecordReqDTO;
import com.mercemay.shortlink.project.dto.req.ShortLinkGroupStatsReqDTO;
import com.mercemay.shortlink.project.dto.req.ShortLinkStatsAccessRecordReqDTO;
import com.mercemay.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import com.mercemay.shortlink.project.dto.resp.ShortLinkStatsAccessRecordRespDTO;
import com.mercemay.shortlink.project.dto.resp.ShortLinkStatsRespDTO;

/**
 * 短链接监控接口服务层
 */
public interface ShortLinkStatsService {
    /**
     * 获取单个短链接监控数据
     *
     * @param requestParam 获取单个短链接监控数据入参
     * @return 短链接监控数据
     */
    ShortLinkStatsRespDTO getShortLinkStats(ShortLinkStatsReqDTO requestParam);

    /**
     * 获取短链接分组监控数据
     *
     * @param requestParam 获取短链接分组监控数据入参
     * @return 短链接分组监控数据
     */
    ShortLinkStatsRespDTO getShortLinkGroupStats(ShortLinkGroupStatsReqDTO requestParam);

    /**
     * 访问单个短链接指定时间内访问记录监控数据
     *
     * @param requestParam 获取短链接监控访问记录数据入参
     * @return 短链接访问记录监控数据
     */
    IPage<ShortLinkStatsAccessRecordRespDTO> getShortLinkAccessRecordStats(ShortLinkStatsAccessRecordReqDTO requestParam);

    /**
     * 访问短链接分组指定时间内访问记录监控数据
     *
     * @param requestParam 获取短链接分组监控访问记录数据入参
     * @return 短链接分组访问记录监控数据
     */
    IPage<ShortLinkStatsAccessRecordRespDTO> getShortLinkGroupAccessRecordStats(ShortLinkGroupStatsAccessRecordReqDTO requestParam);
}
