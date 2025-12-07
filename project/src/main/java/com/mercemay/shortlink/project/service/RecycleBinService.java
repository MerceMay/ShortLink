package com.mercemay.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mercemay.shortlink.project.dao.entity.ShortLinkDO;
import com.mercemay.shortlink.project.dto.req.RecycleBinRecoverReqDTO;
import com.mercemay.shortlink.project.dto.req.RecycleBinRemoveReqDTO;
import com.mercemay.shortlink.project.dto.req.RecycleBinSaveReqDTO;
import com.mercemay.shortlink.project.dto.req.ShortLinkRecycleBinPageReqDTO;
import com.mercemay.shortlink.project.dto.resp.ShortLinkPageRespDTO;

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

    /**
     * 分页查询回收站短链接
     *
     * @param requestParam 请求参数
     * @return 短链接分页结果
     */
    IPage<ShortLinkPageRespDTO> pageRecycleBinShortLink(ShortLinkRecycleBinPageReqDTO requestParam);

    /**
     * 恢复回收站短链接
     *
     * @param requestParam 恢复请求参数
     */
    void recoverRecycleBin(RecycleBinRecoverReqDTO requestParam);

    /**
     * 彻底删除回收站短链接
     *
     * @param requestParam 删除请求参数
     */
    void removeRecycleBin(RecycleBinRemoveReqDTO requestParam);
}
