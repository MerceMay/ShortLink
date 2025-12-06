package com.mercemay.shortlink.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mercemay.shortlink.project.dao.entity.ShortLinkDO;
import com.mercemay.shortlink.project.dto.req.RecycleBinSaveReqDTO;

/**
 * 回收站服务接口
 */
public interface RecycleBinService extends IService<ShortLinkDO> {
    /**
     * 保存回收站信息
     *
     * @param requestParam 回收站保存请求DTO
     */
    void saveRecycleBin(RecycleBinSaveReqDTO requestParam);
}
