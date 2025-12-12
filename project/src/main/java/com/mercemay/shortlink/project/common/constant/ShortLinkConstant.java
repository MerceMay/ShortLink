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
