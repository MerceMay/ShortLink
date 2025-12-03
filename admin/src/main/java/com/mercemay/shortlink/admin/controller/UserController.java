package com.mercemay.shortlink.admin.controller;

import com.mercemay.shortlink.admin.common.convention.result.Result;
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
        UserRespDTO result = userService.getUserByName(username);
        if (result == null) {
            return new Result<UserRespDTO>().setCode("-1").setMessage("用户不存在");
        } else {
            return new Result<UserRespDTO>().setCode("0").setData(result);
        }
    }
}
