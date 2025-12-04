package com.mercemay.shortlink.admin.controller;

import com.mercemay.shortlink.admin.common.convention.result.Result;
import com.mercemay.shortlink.admin.common.convention.result.Results;
import com.mercemay.shortlink.admin.dto.req.ShortLinkGroupSaveReqDTO;
import com.mercemay.shortlink.admin.dto.req.ShortLinkGroupSortReqDTO;
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
    @PostMapping("/api/short-link/admin/v1/group/")
    public Result<Void> save(@RequestBody ShortLinkGroupSaveReqDTO requestParm) {
        groupService.saveGroup(requestParm.getName());
        return Results.success();
    }

    /**
     * 查询登录用户短链接分组列表
     *
     * @return 短链接分组列表
     */
    @GetMapping("/api/short-link/admin/v1/group")
    public Result<List<ShortLinkGroupRespDTO>> listGroup() {
        return Results.success(groupService.listGroup());
    }

    /**
     * 更新短链接分组
     *
     * @param requestParam 请求参数
     * @return 结果
     */
    @PutMapping("/api/short-link/admin/v1/group")
    public Result<Void> updateGroup(@RequestBody ShortLinkGroupUpdateReqDTO requestParam) {
        groupService.updateGroup(requestParam);
        return Results.success();
    }

    /**
     * 删除短链接分组
     *
     * @param gid 分组gid
     * @return 结果
     */
    @DeleteMapping("/api/short-link/admin/v1/group")
    public Result<Void> deleteGroup(@RequestParam String gid) {
        groupService.deleteGroup(gid);
        return Results.success();
    }

    /**
     * 短链接分组排序
     *
     * @param requestParam 请求参数
     * @return 结果
     */
    @PostMapping("/api/short-link/admin/v1/group/sort")
    public Result<Void> sortGroup(@RequestBody List<ShortLinkGroupSortReqDTO> requestParam) {
        groupService.sortGroup(requestParam);
        return Results.success();
    }
}
