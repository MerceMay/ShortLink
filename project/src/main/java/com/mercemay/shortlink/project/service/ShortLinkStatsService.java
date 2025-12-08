package com.mercemay.shortlink.project.service;

import com.mercemay.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import com.mercemay.shortlink.project.dto.resp.ShortLinkStatsRespDTO;

/**
 * 短链接监控接口服务层
 */
public interface ShortLinkStatsService {
    /**
     * 获取单个短链接监控数据
     *
     */
    ShortLinkStatsRespDTO getShortLinkStats(ShortLinkStatsReqDTO requestParam);
}
