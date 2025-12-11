package com.mercemay.shortlink.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mercemay.shortlink.admin.common.convention.result.Result;
import com.mercemay.shortlink.admin.common.convention.result.Results;
import com.mercemay.shortlink.admin.remote.ShortLinkRemoteService;
import com.mercemay.shortlink.admin.remote.dto.req.ShortLinkBatchCreateReqDTO;
import com.mercemay.shortlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import com.mercemay.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import com.mercemay.shortlink.admin.remote.dto.req.ShortLinkUpdateReqDTO;
import com.mercemay.shortlink.admin.remote.dto.resp.ShortLinkBaseInfoRespDTO;
import com.mercemay.shortlink.admin.remote.dto.resp.ShortLinkBatchCreateRespDTO;
import com.mercemay.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.mercemay.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import com.mercemay.shortlink.admin.util.EasyExcelWebUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 短链接控制层
 */
@RestController
@RequiredArgsConstructor
public class ShortLinkController {
    private final ShortLinkRemoteService shortLinkRemoteService;

    /**
     * 创建短链接
     *
     * @param requestParam 请求参数
     * @return 结果
     */
    @PostMapping("/api/short-link/admin/v1/create")
    public Result<ShortLinkCreateRespDTO> createShortLinkGroup(@RequestBody ShortLinkCreateReqDTO requestParam) {
        return shortLinkRemoteService.createShortLink(requestParam);
    }

    /**
     * 批量创建短链接
     *
     * @param requestParam 请求参数
     */
    @SneakyThrows
    @PostMapping("/api/short-link/admin/v1/create/batch")
    public void batchCreateShortLink(@RequestBody ShortLinkBatchCreateReqDTO requestParam, HttpServletResponse response) {
        Result<ShortLinkBatchCreateRespDTO> shortLinkBatchCreateRespDTOResult = shortLinkRemoteService.batchCreateShortLink(requestParam);
        if (shortLinkBatchCreateRespDTOResult.isSuccess()) {
            List<ShortLinkBaseInfoRespDTO> baseLinkInfos = shortLinkBatchCreateRespDTOResult.getData().getBaseLinkInfos();
            EasyExcelWebUtil.write(response, "短链接批量创建结果", ShortLinkBaseInfoRespDTO.class, baseLinkInfos);
        }
    }

    /**
     * 更新短链接
     *
     * @param requestParam 请求参数
     * @return 结果
     */
    @PostMapping("/api/short-link/admin/v1/update")
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpdateReqDTO requestParam) {
        shortLinkRemoteService.updateShortLink(requestParam);
        return Results.success();
    }

    /**
     * 分页查询短链接
     *
     * @param requestParm 请求参数
     * @return 结果
     */
    @GetMapping("/api/short-link/admin/v1/page")
    public Result<Page<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParm) {
        return shortLinkRemoteService.pageShortLink(requestParm.getGid(),
                requestParm.getOrderTag(),
                requestParm.getCurrent(),
                requestParm.getSize());
    }
}
