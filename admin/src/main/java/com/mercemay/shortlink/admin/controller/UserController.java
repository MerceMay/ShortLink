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
    @GetMapping("/api/short-link/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable("username") String username) {
        return Results.success(userService.getUserByName(username));
    }

    /**
     * 根据用户名获取用户无脱敏信息
     *
     * @param username 用户名
     * @return 用户返回实体
     */
    @GetMapping("/api/short-link/v1/actual/user/{username}")
    public Result<UserActualRespDTO> getActualUserByUsername(@PathVariable("username") String username) {
        return Results.success(BeanUtil.toBean(userService.getUserByName(username), UserActualRespDTO.class));
    }

    /**
     * 查询用户名是否存在
     *
     * @return true存在，false不存在
     */
    @GetMapping("/api/short-link/v1/has-username")
    public Result<Boolean> hasUsername(@RequestParam("username") String username) {
        return Results.success(userService.hasUserName(username));
    }

    /**
     * 用户注册
     *
     * @param requestParam 注册请求参数
     * @return 结果
     */
    @PostMapping("/api/short-link/v1/user")
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
    @PutMapping("/api/short-link/v1/user")
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
    @PostMapping("/api/short-link/v1/user/login")
    public Result<UserLoginRespDTO> login(@RequestBody UserLoginReqDTO requestParam) {
        return Results.success(userService.login(requestParam));
    }

    /**
     * 检查用户登录状态
     *
     * @return 结果
     */
    @GetMapping("/api/short-link/v1/user/check-login")
    public Result<Boolean> checkLogin(@RequestParam("username") String username, @RequestParam("token") String token) {
        return Results.success(userService.checkLogin(username, token));
    }
}
