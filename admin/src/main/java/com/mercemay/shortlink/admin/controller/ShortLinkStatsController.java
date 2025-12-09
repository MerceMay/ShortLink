package com.mercemay.shortlink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mercemay.shortlink.admin.common.convention.result.Result;
import com.mercemay.shortlink.admin.remote.ShortLinkRemoteService;
import com.mercemay.shortlink.admin.remote.dto.req.ShortLinkGroupStatsAccessRecordReqDTO;
import com.mercemay.shortlink.admin.remote.dto.req.ShortLinkGroupStatsReqDTO;
import com.mercemay.shortlink.admin.remote.dto.req.ShortLinkStatsReqDTO;
import com.mercemay.shortlink.admin.remote.dto.resp.ShortLinkStatsAccessRecordRespDTO;
import com.mercemay.shortlink.admin.remote.dto.resp.ShortLinkStatsRespDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短链接监控控制器
 */
@RestController
@RequiredArgsConstructor
public class ShortLinkStatsController {
    /**
     * TODO 后续重构为 FeignClient 方式调用
     */
    ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {
    };

    /**
     * 访问单个短链接指定时间内监控数据
     *
     * @param requestParam 请求参数
     * @return 短链接监控数据
     */
    @GetMapping("/api/short-link/admin/v1/stats")
    public Result<ShortLinkStatsRespDTO> getShortLinkStats(ShortLinkStatsReqDTO requestParam) {
        return shortLinkRemoteService.getShortLinkStats(requestParam);
    }

    /**
     * 获取短链接分组监控数据
     *
     * @param requestParam 请求参数
     * @return 短链接分组监控数据
     */
    @GetMapping("/api/short-link/admin/v1/stats/group")
    public Result<ShortLinkStatsRespDTO> getShortLinkGroupStats(ShortLinkGroupStatsReqDTO requestParam) {
        return shortLinkRemoteService.getShortLinkGroupStats(requestParam);
    }

    /**
     * 访问单个短链接指定时间内访问记录监控数据
     *
     * @param requestParam 请求参数
     * @return 短链接访问记录监控数据
     */
    @GetMapping("/api/short-link/admin/v1/stats/access-record")
    public Result<IPage<ShortLinkStatsAccessRecordRespDTO>> getShortLinkAccessRecordStats(ShortLinkStatsReqDTO requestParam) {
        return shortLinkRemoteService.getShortLinkAccessRecordStats(requestParam);
    }

    /**
     * 访问短链接分组指定时间内访问记录监控数据
     *
     * @param requestParam 请求参数
     * @return 短链接分组访问记录监控数据
     */
    @GetMapping("/api/short-link/admin/v1/stats/access-record/group")
    public Result<IPage<ShortLinkStatsAccessRecordRespDTO>> getShortLinkGroupAccessRecordStats(ShortLinkGroupStatsAccessRecordReqDTO requestParam) {
        return shortLinkRemoteService.getShortLinkGroupAccessRecordStats(requestParam);
    }
}
