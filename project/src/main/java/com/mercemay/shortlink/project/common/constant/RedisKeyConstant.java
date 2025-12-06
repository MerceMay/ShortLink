package com.mercemay.shortlink.project.common.constant;

/**
 * Redis键常量
 */
public class RedisKeyConstant {
    /**
     * 短链接路由信息Key前缀
     */
    public static final String ROUTE_SHORT_LINK_KEY = "shortlink:route:";

    /**
     * 短链接路由信息空值Key
     */
    public static final String ROUTE_SHORT_LINK_IS_NULL = "shortlink:route:null";

    /**
     * 短链接路由信息分布式锁Key前缀
     */
    public static final String LOCK_ROUTE_SHORT_LINK_KEY = "shortlink:lock:route:";
}
