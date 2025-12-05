package com.mercemay.shortlink.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mercemay.shortlink.project.dao.entity.ShortLinkDO;
import com.mercemay.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.mercemay.shortlink.project.dto.resp.ShortLinkCreateRespDTO;

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
}
