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
 * Redis键常量
 */
public class RedisKeyConstant {
    /**
     * 短链接路由信息Key前缀
     */
    public static final String SHORT_LINK_ROUTE_KEY = "shortlink:route:";

    /**
     * 短链接路由信息空值Key
     */
    public static final String SHORT_LINK_NULL_ROUTE_KEY = "shortlink:route:null";

    /**
     * 短链接路由信息分布式锁Key前缀
     */
    public static final String SHORT_LINK_ROUTE_LOCK = "shortlink:lock:route:";

    /**
     * 短链接访问统计UV Key前缀
     */
    public static final String STATS_UV_KEY = "shortlink:stats:uv:";

    /**
     * 短链接访问统计UIP Key前缀
     */
    public static final String STATS_UIP_KEY = "shortlink:stats:uip:";

    /**
     * 短链接修改短链接 gid 分布式锁 Key 前缀
     */
    public static final String SHORT_LINK_UPDATE_GID_LOCK = "shortlink:lock:update:gid:";

    /**
     * 短链接延迟队列 Key
     */
    public static final String SHORT_LINK_DELAY_QUEUE_KEY = "shortlink:delay_queue:stats";

    /**
     * 幂等标识 Key 前缀
     */
    public static final String MESSAGE_QUEUE_IDEMPOTENT_KEY = "shortlink:mq:idempotent:";

    /**
     * 短链接统计流 Topic Key
     */
    public static final String SHORT_LINK_STATS_STREAM_TOPIC_KEY = "shortlink:stream:stats";

    /**
     * 短链接统计消费组 Key
     */
    public static final String SHORT_LINK_STATS_STREAM_CONSUMER_GROUP_KEY = "shortlink:stream:stats:group";
}
