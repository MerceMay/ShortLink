package com.mercemay.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mercemay.shortlink.admin.dao.entity.UserDO;
import com.mercemay.shortlink.admin.dto.resp.UserRespDTO;

/**
 * 用户接口层
 */
public interface UserService extends IService<UserDO> {

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户返回实体
     */
    UserRespDTO getUserByName(String username);
}

