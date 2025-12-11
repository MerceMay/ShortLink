package com.mercemay.shortlink.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mercemay.shortlink.admin.common.biz.user.UserContext;
import com.mercemay.shortlink.admin.common.convention.exception.ServiceException;
import com.mercemay.shortlink.admin.common.convention.result.Result;
import com.mercemay.shortlink.admin.dao.entity.GroupDO;
import com.mercemay.shortlink.admin.dao.mapper.GroupMapper;
import com.mercemay.shortlink.admin.remote.ShortLinkRemoteService;
import com.mercemay.shortlink.admin.remote.dto.req.ShortLinkRecycleBinPageReqDTO;
import com.mercemay.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import com.mercemay.shortlink.admin.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 回收站服务实现类
 */
@Service
@RequiredArgsConstructor
public class RecycleBinServiceImpl implements RecycleBinService {
    private final GroupMapper groupMapper;
    private final ShortLinkRemoteService shortLinkRemoteService;


    @Override
    public Result<Page<ShortLinkPageRespDTO>> pageRecycleBinShortLink(ShortLinkRecycleBinPageReqDTO requestParm) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUserName())
                .eq(GroupDO::getDelFlag, 0);
        List<GroupDO> groupDOList = groupMapper.selectList(queryWrapper); // 获取当前用户的所有分组信息
        if (CollUtil.isEmpty(groupDOList)) {
            throw new ServiceException("当前用户未创建任何分组，无法查询短链接");
        }
        requestParm.setGidList(groupDOList.stream().map(GroupDO::getGid).toList());
        return shortLinkRemoteService.pageRecycleBinShortLink(requestParm.getGidList(),
                requestParm.getCurrent(),
                requestParm.getSize());
    }
}
