package com.mercemay.shortlink.project.controller;

import com.mercemay.shortlink.project.common.convention.result.Result;
import com.mercemay.shortlink.project.common.convention.result.Results;
import com.mercemay.shortlink.project.dto.req.ShortLinkStatsReqDTO;
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
     */
    @GetMapping("/api/short-link/v1/stats")
    public Result<ShortLinkStatsRespDTO> getShortLinkStats(ShortLinkStatsReqDTO requestParam) {
        return Results.success(shortLinkStatsService.getShortLinkStats(requestParam));
    }
}
