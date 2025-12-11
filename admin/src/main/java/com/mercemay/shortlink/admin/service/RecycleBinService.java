package com.mercemay.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mercemay.shortlink.admin.common.convention.result.Result;
import com.mercemay.shortlink.admin.remote.dto.req.ShortLinkRecycleBinPageReqDTO;
import com.mercemay.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;

/**
 * 回收站服务接口
 */
public interface RecycleBinService {

    /**
     * 分页查询回收站短链接
     *
     * @param requestParm 请求参数
     * @return 结果
     */
    Result<Page<ShortLinkPageRespDTO>> pageRecycleBinShortLink(ShortLinkRecycleBinPageReqDTO requestParm);
}
