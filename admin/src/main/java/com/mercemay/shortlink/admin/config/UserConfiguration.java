package com.mercemay.shortlink.admin.config;

import com.mercemay.shortlink.admin.common.biz.user.UserTrafficRiskControlFilter;
import com.mercemay.shortlink.admin.common.biz.user.UserTransmitFilter;
import com.mercemay.shortlink.admin.controller.UserTrafficRiskControlConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 用户配置装配
 */
@Configuration
public class UserConfiguration {
    /**
     * 全局用户传递过滤器
     *
     * @param stringRedisTemplate Redis模板
     * @return 过滤器注册Bean
     */
    @Bean
    public FilterRegistrationBean<UserTransmitFilter> globalUserTransmitFilter(StringRedisTemplate stringRedisTemplate) {
        FilterRegistrationBean<UserTransmitFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new UserTransmitFilter(stringRedisTemplate));
        registration.addUrlPatterns("/*");
        registration.setOrder(0);
        return registration;
    }

    /**
     * 用户流量风控过滤器
     *
     * @param stringRedisTemplate                 Redis模板
     * @param userTrafficRiskControlConfiguration 用户流量风控配置
     * @return 过滤器注册Bean
     */
    @Bean
    @ConditionalOnProperty(name = "shortlink.user-traffic-control.enabled", havingValue = "true")
    public FilterRegistrationBean<UserTrafficRiskControlFilter> globalUserTrafficRiskControlFilter(
            StringRedisTemplate stringRedisTemplate,
            UserTrafficRiskControlConfiguration userTrafficRiskControlConfiguration) {
        FilterRegistrationBean<UserTrafficRiskControlFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new UserTrafficRiskControlFilter(stringRedisTemplate, userTrafficRiskControlConfiguration));
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}
