package com.mercemay.shortlink.gateway.config;

import lombok.Data;

import java.util.List;

/**
 * 网关配置类
 */
@Data
public class Config {
    /**
     * 白名单前置标识
     */
    private List<String> whitePathList;
}
