package com.mercemay.shortlink.admin.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 用户登录响应参数
 */
@Data
@AllArgsConstructor
public class UserLoginRespDTO {
    /**
     * 用户Token
     */
    private String token;
}
