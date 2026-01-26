package com.mercemay.shortlink.aggregation.config;

import com.alibaba.fastjson.JSON;
import com.mercemay.shortlink.admin.common.convention.result.Result;
import com.mercemay.shortlink.admin.common.convention.result.Results;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.PrintWriter;
import java.util.Objects;

/**
 * 演示环境模式配置
 */
@Configuration
@RequiredArgsConstructor
public class DemoModeConfiguration implements WebMvcConfigurer {
    private final DemoModeProperties demoModeProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new DemoModeInterceptor())
                .addPathPatterns("/**");
    }

    public class DemoModeInterceptor implements HandlerInterceptor {
        @Override
        public boolean preHandle(@Nullable HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable Object handler) throws Exception {
            if (demoModeProperties.getEnabled()
                    && demoModeProperties.getBlacklist().contains(request.getRequestURI())
                    && !Objects.equals(request.getMethod(), "GET")) {
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out = response.getWriter();
                Result<Void> result = Results.failure("B000001", "演示环境部分功能受限，请访问短链接跳转、监控等功能");
                out.println(JSON.toJSONString(result));
                out.flush();
                return false;
            }
            return true;
        }
    }
}
