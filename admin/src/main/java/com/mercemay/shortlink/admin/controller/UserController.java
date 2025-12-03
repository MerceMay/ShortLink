package com.mercemay.shortlink.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import com.mercemay.shortlink.admin.common.convention.result.Result;
import com.mercemay.shortlink.admin.common.convention.result.Results;
import com.mercemay.shortlink.admin.dto.resp.UserActualRespDTO;
import com.mercemay.shortlink.admin.dto.resp.UserRespDTO;
import com.mercemay.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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
     * @param username
     * @return
     */
    @GetMapping("/api/shortlink/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable("username") String username) {
        return Results.success(userService.getUserByName(username));
    }

    /**
     * 根据用户名获取用户无脱敏信息
     *
     * @param username
     * @return
     */
    @GetMapping("/api/shortlink/v1/actual/user/{username}")
    public Result<UserActualRespDTO> getActualUserByUsername(@PathVariable("username") String username) {
        return Results.success(BeanUtil.toBean(userService.getUserByName(username), UserActualRespDTO.class));
    }

}
