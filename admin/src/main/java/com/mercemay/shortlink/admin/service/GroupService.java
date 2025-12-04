package com.mercemay.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mercemay.shortlink.admin.dao.entity.GroupDO;
import com.mercemay.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import com.mercemay.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;

import java.util.List;

/**
 * 短链接分组接口层
 */
public interface GroupService extends IService<GroupDO> {

    /**
     * 新增短链接分组
     *
     * @param groupName 分组名称
     */
    void saveGroup(String groupName);

    /**
     * 查询用户短链接分组列表
     *
     * @return 短链接分组列表
     */
    List<ShortLinkGroupRespDTO> listGroup();

    /**
     * 更新短链接分组
     *
     * @param requestParam 请求参数
     */
    void updateGroup(ShortLinkGroupUpdateReqDTO requestParam);
}
