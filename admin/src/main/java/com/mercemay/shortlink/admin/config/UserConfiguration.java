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
     * @return 过滤器注册Bean
     */
    @Bean
    public FilterRegistrationBean<UserTransmitFilter> globalUserTransmitFilter() {
        FilterRegistrationBean<UserTransmitFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new UserTransmitFilter());
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
