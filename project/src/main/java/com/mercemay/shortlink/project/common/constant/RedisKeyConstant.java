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
    public static final String ROUTE_NULL_SHORT_LINK_KEY = "shortlink:route:null";

    /**
     * 短链接路由信息分布式锁Key前缀
     */
    public static final String LOCK_ROUTE_SHORT_LINK_KEY = "shortlink:lock:route:";

    /**
     * 短链接访问统计UV Key前缀
     */
    public static final String STATS_UV_KEY = "shortlink:stats:uv:";

    /**
     * 短链接访问统计UIP Key前缀
     */
    public static final String STATS_UIP_KEY = "shortlink:stats:uip:";
}
