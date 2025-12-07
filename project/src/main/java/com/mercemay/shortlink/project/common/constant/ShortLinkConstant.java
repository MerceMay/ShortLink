package com.mercemay.shortlink.project.common.constant;

/**
 * 短链接常量
 */
public class ShortLinkConstant {
    /**
     * 永久短链接缓存有效期，单位：毫秒
     */
    public static final long DEFAULT_SHORT_LINK_CACHE_VALID_TIME = 30 * 24 * 60 * 60 * 1000L; // 默认短链接缓存有效期，30天，单位：毫秒

    /**
     * 高德地图获取IP地址归属地接口URL
     */
    public static final String AMAP_REMOTE_URL = "https://restapi.amap.com/v3/ip";
}
