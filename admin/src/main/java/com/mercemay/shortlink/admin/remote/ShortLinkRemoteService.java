package com.mercemay.shortlink.admin.remote;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mercemay.shortlink.admin.common.convention.result.Result;
import com.mercemay.shortlink.admin.dto.req.RecycleBinRecoverReqDTO;
import com.mercemay.shortlink.admin.dto.req.RecycleBinRemoveReqDTO;
import com.mercemay.shortlink.admin.dto.req.RecycleBinSaveReqDTO;
import com.mercemay.shortlink.admin.remote.dto.req.ShortLinkBatchCreateReqDTO;
import com.mercemay.shortlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import com.mercemay.shortlink.admin.remote.dto.req.ShortLinkUpdateReqDTO;
import com.mercemay.shortlink.admin.remote.dto.resp.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 短链实际远程服务接口
 */
@FeignClient(value = "short-link-project", url = "${aggregation.remote-url:}")
public interface ShortLinkRemoteService {

    /**
     * 创建短链接
     *
     * @param requestParam 请求参数
     * @return 结果
     */
    @PostMapping("/api/short-link/v1/create")
    Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam);

    /**
     * 批量创建短链接
     *
     * @param requestParam 请求参数
     * @return 结果
     */
    @PostMapping("/api/short-link/v1/create/batch")
    Result<ShortLinkBatchCreateRespDTO> batchCreateShortLink(@RequestBody ShortLinkBatchCreateReqDTO requestParam);

    /**
     * 更新短链接
     *
     * @param requestParam 请求参数
     */
    @PostMapping("/api/short-link/v1/update")
    void updateShortLink(@RequestBody ShortLinkUpdateReqDTO requestParam);

    /**
     * 分页查询短链接
     *
     * @param gid      分组标识
     * @param current  当前页
     * @param size     每页数量
     * @param orderTag 排序标识
     * @return 结果
     */
    @GetMapping("/api/short-link/v1/page")
    Result<Page<ShortLinkPageRespDTO>> pageShortLink(@RequestParam("gid") String gid,
                                                     @RequestParam("orderTag") String orderTag,
                                                     @RequestParam("current") Long current,
                                                     @RequestParam("size") Long size);


    /**
     * 查询短链接分组内数量
     *
     * @param requestParam 分组标识列表
     * @return 短链接分组数量列表
     */
    @GetMapping("/api/short-link/v1/count")
    Result<List<ShortLinkGroupCountQueryRespDTO>> listShortLinkGroupCount(@RequestParam("requestParam") List<String> requestParam);

    /**
     * 通过URL获取标题
     *
     * @param url 网址
     * @return 标题
     */
    @GetMapping("/api/short-link/v1/title")
    Result<String> getTitleByUrl(@RequestParam("url") String url);

    /**
     * 保存回收站记录
     *
     * @param requestParam 回收站保存请求参数
     */
    @PostMapping("/api/short-link/v1/recycle-bin/save")
    void saveRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam);

    /**
     * 分页查询回收站短链接
     *
     * @param gidList 分组标识列表
     * @param current 当前页
     * @param size    每页数量
     * @return 结果
     */
    @GetMapping("/api/short-link/v1/recycle-bin/page")
    Result<Page<ShortLinkPageRespDTO>> pageRecycleBinShortLink(@RequestParam("gidList") List<String> gidList,
                                                               @RequestParam("current") Long current,
                                                               @RequestParam("size") Long size);

    /**
     * 恢复回收站短链接
     *
     * @param requestParam 恢复请求参数
     */
    @PostMapping("/api/short-link/v1/recycle-bin/recover")
    void recoverRecycleBin(@RequestBody RecycleBinRecoverReqDTO requestParam);

    /**
     * 删除回收站短链接
     *
     * @param requestParam 删除请求参数
     */
    @PostMapping("/api/short-link/v1/recycle-bin/remove")
    void removeRecycleBin(@RequestBody RecycleBinRemoveReqDTO requestParam);

    /**
     * 获取单个短链接监控数据
     *
     * @param fullShortUrl 完整短链接
     * @param gid          分组标识
     * @param enableStatus 启用标识 0：启用 1：未启用
     * @param startDate    开始日期
     * @param endDate      结束日期
     * @return 短链接监控数据
     */
    @GetMapping("/api/short-link/v1/stats")
    Result<ShortLinkStatsRespDTO> getShortLinkStats(@RequestParam("fullShortUrl") String fullShortUrl,
                                                    @RequestParam("gid") String gid,
                                                    @RequestParam("enableStatus") Integer enableStatus,
                                                    @RequestParam("startDate") String startDate,
                                                    @RequestParam("endDate") String endDate);

    /**
     * 获取短链接分组监控数据
     *
     * @param gid       分组标识
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 短链接分组监控数据
     */
    @GetMapping("/api/short-link/v1/stats/group")
    Result<ShortLinkStatsRespDTO> getShortLinkGroupStats(@RequestParam("gid") String gid,
                                                         @RequestParam("startDate") String startDate,
                                                         @RequestParam("endDate") String endDate);

    /**
     * 访问单个短链接指定时间内访问记录监控数据
     *
     * @param fullShortUrl 完整短链接
     * @param gid          分组标识
     * @param startDate    开始日期
     * @param endDate      结束日期
     * @param enableStatus 启用标识 0：启用 1：未启用
     * @param current      当前页
     * @param size         每页数量
     * @return 短链接访问记录监控数据
     */
    @GetMapping("/api/short-link/v1/stats/access-record")
    Result<Page<ShortLinkStatsAccessRecordRespDTO>> getShortLinkAccessRecordStats(@RequestParam("fullShortUrl") String fullShortUrl,
                                                                                  @RequestParam("gid") String gid,
                                                                                  @RequestParam("startDate") String startDate,
                                                                                  @RequestParam("endDate") String endDate,
                                                                                  @RequestParam("enableStatus") Integer enableStatus,
                                                                                  @RequestParam("current") Long current,
                                                                                  @RequestParam("size") Long size);

    /**
     * 访问短链接分组指定时间内访问记录监控数据
     *
     * @param gid       分组标识
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param current   当前页
     * @param size      每页数量
     * @return 短链接分组访问记录监控数据
     */
    @GetMapping("/api/short-link/v1/stats/access-record/group")
    Result<Page<ShortLinkStatsAccessRecordRespDTO>> getShortLinkGroupAccessRecordStats(@RequestParam("gid") String gid,
                                                                                       @RequestParam("startDate") String startDate,
                                                                                       @RequestParam("endDate") String endDate,
                                                                                       @RequestParam("current") Long current,
                                                                                       @RequestParam("size") Long size);

}
