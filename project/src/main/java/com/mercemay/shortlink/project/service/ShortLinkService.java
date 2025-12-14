package com.mercemay.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mercemay.shortlink.project.dao.entity.ShortLinkDO;
import com.mercemay.shortlink.project.dto.biz.ShortLinkStatsRecordDTO;
import com.mercemay.shortlink.project.dto.req.ShortLinkBatchCreateReqDTO;
import com.mercemay.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.mercemay.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.mercemay.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.mercemay.shortlink.project.dto.resp.ShortLinkBatchCreateRespDTO;
import com.mercemay.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.mercemay.shortlink.project.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.mercemay.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.util.List;

/**
 * 短链接接口层
 */
public interface ShortLinkService extends IService<ShortLinkDO> {

    /**
     * 创建短链接
     *
     * @param requestParam 创建参数
     * @return 短链接返回结果
     */
    ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam);

    /**
     * 通过分布式锁创建短链接
     *
     * @param requestParam 创建参数
     * @return 短链接返回结果
     */
    ShortLinkCreateRespDTO createShortLinkByLock(ShortLinkCreateReqDTO requestParam);

    /**
     * 批量创建短链接
     *
     * @param requestParam 请求参数
     * @return 短链接批量创建结果
     */
    ShortLinkBatchCreateRespDTO batchCreateShortLink(ShortLinkBatchCreateReqDTO requestParam);

    /**
     * 分页查询短链接
     *
     * @param requestParam 请求参数
     * @return 短链接分页结果
     */
    IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam);

    /**
     * 查询短链接分组内数量
     *
     * @param requestParam 分组标识列表
     * @return 短链接分组数量列表
     */
    List<ShortLinkGroupCountQueryRespDTO> listShortLinkGroupCount(List<String> requestParam);

    /**
     * 更新短链接
     *
     * @param requestParam 请求参数
     */
    void updateShortLink(ShortLinkUpdateReqDTO requestParam);

    /**
     * 短链接重定向
     *
     * @param shortUri 短链接后缀
     * @param request  请求
     * @param response 响应
     */
    void redirectUrl(String shortUri, ServletRequest request, ServletResponse response);

    /**
     * 短链接统计
     *
     * @param shortLinkStatsRecord 统计记录
     */
    void shortLinkStats(ShortLinkStatsRecordDTO shortLinkStatsRecord);
}
