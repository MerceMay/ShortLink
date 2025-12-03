package com.mercemay.shortlink.admin.common.web;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.mercemay.shortlink.admin.common.convention.errorcode.BaseErrorCode;
import com.mercemay.shortlink.admin.common.convention.exception.AbstractException;
import com.mercemay.shortlink.admin.common.convention.result.Result;
import com.mercemay.shortlink.admin.common.convention.result.Results;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

@Component
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 拦截参数验证异常
     */
    @SneakyThrows// 处理编译异常
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result validExceptionHandler(HttpServletRequest request, MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult(); // 获取绑定结果
        FieldError firstFieldError = CollectionUtil.getFirst(bindingResult.getFieldErrors()); // 获取第一个字段错误
        String exceptionStr = Optional.ofNullable(firstFieldError) // 获取异常信息
                .map(FieldError::getDefaultMessage) // 获取默认消息
                .orElse(StrUtil.EMPTY); // 如果没有错误消息，返回空字符串
        log.error("[{}] {} [ex] {}", request.getMethod(), getUrl(request), exceptionStr);
        return Results.failure(BaseErrorCode.CLIENT_ERROR.code(), exceptionStr); // 返回客户端错误响应
    }

    /**
     * 拦截应用内抛出的异常
     *
     * @param request
     * @return
     */
    @ExceptionHandler(value = {AbstractException.class})
    public Result abstractExceptionHandler(HttpServletRequest request, AbstractException ex) {
        if (ex.getCause() != null) { // 如果有异常原因
            log.error("[{}] {} [ex] {}", request.getMethod(), request.getRequestURL().toString(), ex.toString(), ex.getCause());
            return Results.failure(ex);
        }
        log.error("[{}] {} [ex] {}", request.getMethod(), request.getRequestURL().toString(), ex.toString());
        return Results.failure(ex);
    }

    /**
     * 拦截未捕获的异常
     *
     * @param request
     * @return
     */
    @ExceptionHandler(value = Throwable.class)
    public Result defaultExceptionHandler(HttpServletRequest request, Throwable throwable) {
        log.error("[{}] {} ", request.getMethod(), getUrl(request), throwable);
        return Results.failure();
    }

    private String getUrl(HttpServletRequest request) {
        if (!StringUtils.hasLength(request.getQueryString())) { // 如果没有查询字符串
            return request.getRequestURL().toString(); // 返回请求的URL
        }
        return request.getRequestURL().toString() + "?" + request.getQueryString(); // 返回完整的URL
    }
}
