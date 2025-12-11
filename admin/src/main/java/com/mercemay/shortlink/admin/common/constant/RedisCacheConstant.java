package com.mercemay.shortlink.admin.common.constant;

/**
 * Redis缓存常量
 */
public class RedisCacheConstant {
    /**
     * 用户注册锁前缀
     */
    public static final String USER_REGISTER_LOCK = "shortlink:lock:user:register:";

    /**
     * 分组创建锁
     */
    public static final String GROUP_CREATE_LOCK = "shortlink:lock:group:create:";
}
