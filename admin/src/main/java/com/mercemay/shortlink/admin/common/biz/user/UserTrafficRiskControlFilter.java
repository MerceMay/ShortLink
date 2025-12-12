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
package com.mercemay.shortlink.admin.common.biz.user;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import com.mercemay.shortlink.admin.common.convention.errorcode.BaseErrorCode;
import com.mercemay.shortlink.admin.common.convention.exception.ClientException;
import com.mercemay.shortlink.admin.common.convention.result.Results;
import com.mercemay.shortlink.admin.controller.UserTrafficRiskControlConfiguration;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class UserTrafficRiskControlFilter implements Filter {

    private final StringRedisTemplate stringRedisTemplate;
    private final UserTrafficRiskControlConfiguration userTrafficRiskControlConfiguration;

    private static final String USER_TRAFFIC_RISK_CONTROL_LUA_SCRIPT_PATH = "lua/user_traffic_risk_control.lua";

    @SneakyThrows
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(USER_TRAFFIC_RISK_CONTROL_LUA_SCRIPT_PATH)));
        redisScript.setResultType(Long.class);
        String username = Optional.ofNullable(UserContext.getUserName()).orElse("anonymous");
        Long result = null;
        try {
            result = stringRedisTemplate.execute(
                    redisScript,
                    Lists.newArrayList(username),
                    userTrafficRiskControlConfiguration.getTimeWindow()
            );
        } catch (Throwable ex) {
            log.error("用户流量风控，执行Lua脚本异常，用户名：{}", username, ex);
            returnJson((HttpServletResponse) servletResponse, JSON.toJSONString(Results.failure(new ClientException(BaseErrorCode.TRAFFIC_CONTROL_ERROR))));
        }
        if (result == null || result > userTrafficRiskControlConfiguration.getMaxRequests()) {
            returnJson((HttpServletResponse) servletResponse, JSON.toJSONString(Results.failure(new ClientException(BaseErrorCode.TRAFFIC_CONTROL_ERROR))));
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private void returnJson(HttpServletResponse response, String json) throws Exception {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.print(json);
        }
    }
}
