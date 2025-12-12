/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mercemay.shortlink.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import com.mercemay.shortlink.admin.common.convention.result.Result;
import com.mercemay.shortlink.admin.common.convention.result.Results;
import com.mercemay.shortlink.admin.dto.req.UserLoginReqDTO;
import com.mercemay.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.mercemay.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.mercemay.shortlink.admin.dto.resp.UserActualRespDTO;
import com.mercemay.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.mercemay.shortlink.admin.dto.resp.UserRespDTO;
import com.mercemay.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制层
 */
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户返回实体
     */
    @GetMapping("/api/short-link/admin/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable("username") String username) {
        return Results.success(userService.getUserByName(username));
    }

    /**
     * 根据用户名获取用户无脱敏信息
     *
     * @param username 用户名
     * @return 用户返回实体
     */
    @GetMapping("/api/short-link/admin/v1/actual/user/{username}")
    public Result<UserActualRespDTO> getActualUserByUsername(@PathVariable("username") String username) {
        return Results.success(BeanUtil.toBean(userService.getUserByName(username), UserActualRespDTO.class));
    }

    /**
     * 查询用户名是否存在
     *
     * @return true存在，false不存在
     */
    @GetMapping("/api/short-link/admin/v1/has-username")
    public Result<Boolean> hasUsername(@RequestParam("username") String username) {
        return Results.success(userService.hasUserName(username));
    }

    /**
     * 用户注册
     *
     * @param requestParam 注册请求参数
     * @return 结果
     */
    @PostMapping("/api/short-link/admin/v1/user")
    public Result<Void> register(@RequestBody UserRegisterReqDTO requestParam) {
        userService.register(requestParam);
        return Results.success();
    }

    /**
     * 根据用户名更新用户信息
     *
     * @param requestParam 更新请求参数
     * @return 结果
     */
    @PutMapping("/api/short-link/admin/v1/user")
    public Result<Void> update(@RequestBody UserUpdateReqDTO requestParam) {
        userService.updateByUsername(requestParam);
        return Results.success();
    }

    /**
     * 用户登录
     *
     * @param requestParam 登录请求参数
     * @return 结果
     */
    @PostMapping("/api/short-link/admin/v1/user/login")
    public Result<UserLoginRespDTO> login(@RequestBody UserLoginReqDTO requestParam) {
        return Results.success(userService.login(requestParam));
    }

    /**
     * 检查用户登录状态
     *
     * @param username 用户名
     * @param token    登录token
     * @return 结果
     */
    @GetMapping("/api/short-link/admin/v1/user/check-login")
    public Result<Boolean> checkLogin(@RequestParam("username") String username, @RequestParam("token") String token) {
        return Results.success(userService.checkLogin(username, token));
    }

    /**
     * 用户登出
     *
     * @param username 用户名
     * @param token    登录token
     */
    @DeleteMapping("/api/short-link/admin/v1/user/logout")
    public Result<Void> logout(@RequestParam("username") String username, @RequestParam("token") String token) {
        userService.logout(username, token);
        return Results.success();
    }
}
