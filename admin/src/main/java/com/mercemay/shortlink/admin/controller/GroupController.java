package com.mercemay.shortlink.admin.controller;

import com.mercemay.shortlink.admin.common.convention.result.Result;
import com.mercemay.shortlink.admin.common.convention.result.Results;
import com.mercemay.shortlink.admin.dto.req.ShortLinkGroupSaveReqDTO;
import com.mercemay.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import com.mercemay.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;
import com.mercemay.shortlink.admin.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 短链接分组控制层
 */
@RestController
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    /**
     * 新增短链接分组
     *
     * @param requestParm 请求参数
     * @return 结果
     */
    @PostMapping("/api/short-link/v1/group/")
    public Result<Void> save(@RequestBody ShortLinkGroupSaveReqDTO requestParm) {
        groupService.saveGroup(requestParm.getName());
        return Results.success();
    }

    /**
     * 查询用户短链接分组列表
     *
     * @return 短链接分组列表
     */
    @GetMapping("/api/short-link/v1/group")
    public Result<List<ShortLinkGroupRespDTO>> listGroup() {
        return Results.success(groupService.listGroup());
    }

    /**
     * 更新短链接分组
     *
     * @param requestParam 请求参数
     * @return 结果
     */
    @PutMapping("/api/short-link/v1/group")
    public Result<Void> updateGroup(@RequestBody ShortLinkGroupUpdateReqDTO requestParam) {
        groupService.updateGroup(requestParam);
        return Results.success();
    }
}
