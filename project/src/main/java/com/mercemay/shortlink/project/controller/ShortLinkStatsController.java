package com.mercemay.shortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mercemay.shortlink.project.common.convention.result.Result;
import com.mercemay.shortlink.project.common.convention.result.Results;
import com.mercemay.shortlink.project.dto.req.ShortLinkGroupStatsReqDTO;
import com.mercemay.shortlink.project.dto.req.ShortLinkStatsAccessRecordReqDTO;
import com.mercemay.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import com.mercemay.shortlink.project.dto.resp.ShortLinkStatsAccessRecordRespDTO;
import com.mercemay.shortlink.project.dto.resp.ShortLinkStatsRespDTO;
import com.mercemay.shortlink.project.service.ShortLinkStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短链接监控控制层
 */
@RestController
@RequiredArgsConstructor
public class ShortLinkStatsController {
    private final ShortLinkStatsService shortLinkStatsService;

    /**
     * 获取单个短链接监控数据
     *
     * @param requestParam 请求参数
     * @return 短链接监控数据
     */
    @GetMapping("/api/short-link/v1/stats")
    public Result<ShortLinkStatsRespDTO> getShortLinkStats(ShortLinkStatsReqDTO requestParam) {
        return Results.success(shortLinkStatsService.getShortLinkStats(requestParam));
    }

    /**
     * 获取短链接分组监控数据
     *
     * @param requestParam 请求参数
     * @return 短链接分组监控数据
     */
    @GetMapping("/api/short-link/v1/stats/group")
    public Result<ShortLinkStatsRespDTO> getShortLinkGroupStats(ShortLinkGroupStatsReqDTO requestParam) {
        return Results.success(shortLinkStatsService.getShortLinkGroupStats(requestParam));
    }

    /**
     * 访问单个短链接指定时间内访问记录监控数据
     *
     * @param requestParam 请求参数
     * @return 短链接访问记录监控数据
     */
    @GetMapping("/api/short-link/v1/stats/access-record")
    public Result<IPage<ShortLinkStatsAccessRecordRespDTO>> getShortLinkAccessRecordStats(ShortLinkStatsAccessRecordReqDTO requestParam) {
        return Results.success(shortLinkStatsService.getShortLinkAccessRecordStats(requestParam));
    }
}
