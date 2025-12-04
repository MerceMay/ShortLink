package com.mercemay.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mercemay.shortlink.admin.dao.entity.UserDO;
import com.mercemay.shortlink.admin.dto.req.UserLoginReqDTO;
import com.mercemay.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.mercemay.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.mercemay.shortlink.admin.dto.resp.UserLoginRespDTO;
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

    /**
     * 查询用户名是否存在
     *
     * @param username 用户名
     * @return true存在，false不存在
     */
    Boolean hasUserName(String username);

    /**
     * 用户注册
     *
     * @param requestParam 注册请求参数
     */
    void register(UserRegisterReqDTO requestParam);

    /**
     * 根据用户名更新用户信息
     *
     * @param requestParam 更新请求参数
     */
    void updateByUsername(UserUpdateReqDTO requestParam);

    /**
     * 用户登录
     *
     * @param requestParam 登录请求参数
     * @return 登录返回实体
     */
    UserLoginRespDTO login(UserLoginReqDTO requestParam);

    /**
     * 检查登录状态
     *
     * @param username 用户名
     * @param token    登录token
     * @return 是否登录
     */
    Boolean checkLogin(String username, String token);
}

