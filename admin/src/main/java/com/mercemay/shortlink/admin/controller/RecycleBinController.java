package com.mercemay.shortlink.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mercemay.shortlink.admin.common.convention.result.Result;
import com.mercemay.shortlink.admin.common.convention.result.Results;
import com.mercemay.shortlink.admin.dto.req.RecycleBinRecoverReqDTO;
import com.mercemay.shortlink.admin.dto.req.RecycleBinRemoveReqDTO;
import com.mercemay.shortlink.admin.dto.req.RecycleBinSaveReqDTO;
import com.mercemay.shortlink.admin.remote.ShortLinkRemoteService;
import com.mercemay.shortlink.admin.remote.dto.req.ShortLinkRecycleBinPageReqDTO;
import com.mercemay.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import com.mercemay.shortlink.admin.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 回收站控制层
 */
@RestController
@RequiredArgsConstructor
public class RecycleBinController {

    private final RecycleBinService recycleBinService;

    private final ShortLinkRemoteService shortLinkRemoteService;


    /**
     * 保存回收站记录
     *
     * @param recycleBinSaveReqDTO 请求参数
     * @return 结果
     */
    @PostMapping("/api/short-link/admin/v1/recycle-bin/save")
    public Result<Void> saveRecycleBin(@RequestBody RecycleBinSaveReqDTO recycleBinSaveReqDTO) {
        shortLinkRemoteService.saveRecycleBin(recycleBinSaveReqDTO);
        return Results.success();
    }

    /**
     * 分页查询回收站短链接
     *
     * @param requestParm 请求参数
     * @return 结果
     */
    @GetMapping("/api/short-link/admin/v1/recycle-bin/page")
    public Result<Page<ShortLinkPageRespDTO>> pageRecycleBinShortLink(ShortLinkRecycleBinPageReqDTO requestParm) {
        return recycleBinService.pageRecycleBinShortLink(requestParm);
    }

    /**
     * 恢复回收站短链接
     *
     * @param requestParm 请求参数
     * @return 结果
     */
    @PostMapping("/api/short-link/admin/v1/recycle-bin/recover")
    public Result<Void> recoverRecycleBin(@RequestBody RecycleBinRecoverReqDTO requestParm) {
        shortLinkRemoteService.recoverRecycleBin(requestParm);
        return Results.success();
    }

    /**
     * 删除回收站短链接
     *
     * @param requestParam 请求参数
     * @return 结果
     */
    @PostMapping("/api/short-link/admin/v1/recycle-bin/remove")
    public Result<Void> removeRecycleBin(@RequestBody RecycleBinRemoveReqDTO requestParam) {
        shortLinkRemoteService.removeRecycleBin(requestParam);
        return Results.success();
    }
}
