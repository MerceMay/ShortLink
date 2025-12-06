package com.mercemay.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mercemay.shortlink.admin.common.biz.user.UserContext;
import com.mercemay.shortlink.admin.common.convention.result.Result;
import com.mercemay.shortlink.admin.dao.entity.GroupDO;
import com.mercemay.shortlink.admin.dao.mapper.GroupMapper;
import com.mercemay.shortlink.admin.dto.req.ShortLinkGroupSortReqDTO;
import com.mercemay.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import com.mercemay.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;
import com.mercemay.shortlink.admin.remote.ShortLinkRemoteService;
import com.mercemay.shortlink.admin.remote.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.mercemay.shortlink.admin.service.GroupService;
import com.mercemay.shortlink.admin.util.RandomGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 短链接分组接口实现层
 */
@Slf4j
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {

    /**
     * TODO 后续重构为 FeignClient 方式调用
     */
    ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {
    };

    @Override
    public void saveGroup(String groupName) {
        saveGroup(UserContext.getUserName(), groupName);
    }

    @Override
    public void saveGroup(String username, String groupName) {
        String gid = RandomGenerator.generateRandom();
        while (hadGid(username, gid)) { // 检查gid是否存在，如果存在则重新生成
            gid = RandomGenerator.generateRandom();
        }
        GroupDO groupDO = GroupDO.builder()
                .gid(gid)
                .name(groupName)
                .sortOrder(0)
                .username(username)
                .build();
        baseMapper.insert(groupDO);
    }

    @Override
    public List<ShortLinkGroupRespDTO> listGroup() {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getDelFlag, 0)
                .eq(GroupDO::getUsername, UserContext.getUserName())
                .orderByDesc(GroupDO::getSortOrder)
                .orderByDesc(GroupDO::getUpdateTime);
        List<GroupDO> groupDOList = baseMapper.selectList(queryWrapper);
        // 如果分组列表为空，直接返回空列表
        if (CollUtil.isEmpty(groupDOList)) {
            return Collections.emptyList();
        }
        // 转换对象
        List<ShortLinkGroupRespDTO> result = BeanUtil.copyToList(groupDOList, ShortLinkGroupRespDTO.class);

        // 调用远程服务查询每个分组的短链接数量
        try {
            List<String> gidList = groupDOList.stream().map(GroupDO::getGid).toList(); // 提取gid列表
            Result<List<ShortLinkGroupCountQueryRespDTO>> listResult = shortLinkRemoteService.listShortLinkGroupCount(gidList); // 远程调用
            if (listResult != null && CollUtil.isNotEmpty(listResult.getData())) { // 结果不为空，进行数量设置
                Map<String, Integer> countMap = listResult.getData().stream() // 转换为Map
                        .collect(Collectors.toMap(
                                ShortLinkGroupCountQueryRespDTO::getGid, // key: gid
                                ShortLinkGroupCountQueryRespDTO::getShortLinkCount, // value: 短链接数量
                                (oldValue, newValue) -> newValue // 如果有重复key，保留新值
                        ));
                result.forEach(each -> { // 设置每个分组的短链接数量
                    Integer count = countMap.get(each.getGid()); // 获取对应gid的数量
                    each.setShortLinkCount(count != null ? count : 0); // 设置数量，若为空则设置为0
                });
            }
        } catch (Exception e) {
            log.error("调用远程服务查询短链接分组数量异常", e);
        }
        return result;
    }

    @Override
    public void updateGroup(ShortLinkGroupUpdateReqDTO requestParam) {
        LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUserName())
                .eq(GroupDO::getGid, requestParam.getGid())
                .eq(GroupDO::getDelFlag, 0);
        GroupDO groupDO = new GroupDO();
        groupDO.setName(requestParam.getName());
        baseMapper.update(groupDO, updateWrapper);
    }

    @Override
    public void deleteGroup(String gid) {
        LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUserName())
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getDelFlag, 0);
        GroupDO groupDO = new GroupDO();
        groupDO.setDelFlag(1);
        baseMapper.update(groupDO, updateWrapper);
    }

    @Override
    public void sortGroup(List<ShortLinkGroupSortReqDTO> requestParam) {
        requestParam.forEach(each -> {
            GroupDO groupDO = GroupDO.builder()
                    .sortOrder(each.getSortOrder())
                    .build();
            LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                    .eq(GroupDO::getUsername, UserContext.getUserName())
                    .eq(GroupDO::getGid, each.getGid())
                    .eq(GroupDO::getDelFlag, 0);
            baseMapper.update(groupDO, updateWrapper);
        });
    }

    private boolean hadGid(String username, String gid) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getUsername, Optional.ofNullable(username).orElse(UserContext.getUserName()));
        GroupDO groupDO = baseMapper.selectOne(queryWrapper);
        return groupDO != null; // 存在返回true
    }
}
