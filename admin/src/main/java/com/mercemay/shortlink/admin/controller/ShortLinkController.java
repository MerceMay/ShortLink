package com.mercemay.shortlink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mercemay.shortlink.admin.common.convention.result.Result;
import com.mercemay.shortlink.admin.remote.dto.ShortLInkRemoteService;
import com.mercemay.shortlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import com.mercemay.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import com.mercemay.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.mercemay.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短链接控制层
 */
@RestController
public class ShortLinkController {
    /**
     * TODO 后续重构为 FeignClient 方式调用
     */
    ShortLInkRemoteService shortLInkRemoteService = new ShortLInkRemoteService() {
    };

    /**
     * 创建短链接
     *
     * @param requestParam 请求参数
     * @return 结果
     */
    @PostMapping("/api/short-link/admin/v1/create")
    public Result<ShortLinkCreateRespDTO> createShortLinkGroup(@RequestBody ShortLinkCreateReqDTO requestParam) {
        return shortLInkRemoteService.createShortLink(requestParam);
    }

    /**
     * 分页查询短链接
     *
     * @param requestParm 请求参数
     * @return 结果
     */
    @GetMapping("/api/short-link/admin/v1/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParm) {
        return shortLInkRemoteService.pageShortLink(requestParm);
    }
}
