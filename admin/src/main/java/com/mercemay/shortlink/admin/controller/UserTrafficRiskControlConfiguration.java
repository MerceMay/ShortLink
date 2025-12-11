package com.mercemay.shortlink.admin.controller;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "shortlink.user-traffic-control")
public class UserTrafficRiskControlConfiguration {

    /**
     * 是否启用用户流量风控
     */
    private Boolean enabled;

    /**
     * 单位时间，单位：秒
     */
    private String timeWindow;

    /**
     * 最大请求数
     */
    private Long maxRequests;
}
