package com.mercemay.shortlink.project.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "short-link.route.whitelist")
public class RouteDomainWhiteListConfiguration {
    /**
     * 是否开启域名白名单校验
     */
    private Boolean enable;

    /**
     * 域名网站名称集合
     */
    private String names;

    /**
     * 域名集合
     */
    private List<String> details;
}
