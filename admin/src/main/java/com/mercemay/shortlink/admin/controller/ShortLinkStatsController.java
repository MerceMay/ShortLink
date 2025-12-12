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
package com.mercemay.shortlink.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mercemay.shortlink.admin.common.convention.result.Result;
import com.mercemay.shortlink.admin.remote.ShortLinkRemoteService;
import com.mercemay.shortlink.admin.remote.dto.req.ShortLinkGroupStatsAccessRecordReqDTO;
import com.mercemay.shortlink.admin.remote.dto.req.ShortLinkGroupStatsReqDTO;
import com.mercemay.shortlink.admin.remote.dto.req.ShortLinkStatsReqDTO;
import com.mercemay.shortlink.admin.remote.dto.resp.ShortLinkStatsAccessRecordRespDTO;
import com.mercemay.shortlink.admin.remote.dto.resp.ShortLinkStatsRespDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短链接监控控制器
 */
@RestController
@RequiredArgsConstructor
public class ShortLinkStatsController {
    private final ShortLinkRemoteService shortLinkRemoteService;

    /**
     * 访问单个短链接指定时间内监控数据
     *
     * @param requestParam 请求参数
     * @return 短链接监控数据
     */
    @GetMapping("/api/short-link/admin/v1/stats")
    public Result<ShortLinkStatsRespDTO> getShortLinkStats(ShortLinkStatsReqDTO requestParam) {
        return shortLinkRemoteService.getShortLinkStats(requestParam.getFullShortUrl(),
                requestParam.getGid(),
                requestParam.getStartDate(),
                requestParam.getEndDate());
    }

    /**
     * 获取短链接分组监控数据
     *
     * @param requestParam 请求参数
     * @return 短链接分组监控数据
     */
    @GetMapping("/api/short-link/admin/v1/stats/group")
    public Result<ShortLinkStatsRespDTO> getShortLinkGroupStats(ShortLinkGroupStatsReqDTO requestParam) {
        return shortLinkRemoteService.getShortLinkGroupStats(requestParam.getGid(),
                requestParam.getStartDate(),
                requestParam.getEndDate());
    }

    /**
     * 访问单个短链接指定时间内访问记录监控数据
     *
     * @param requestParam 请求参数
     * @return 短链接访问记录监控数据
     */
    @GetMapping("/api/short-link/admin/v1/stats/access-record")
    public Result<Page<ShortLinkStatsAccessRecordRespDTO>> getShortLinkAccessRecordStats(ShortLinkStatsReqDTO requestParam) {
        return shortLinkRemoteService.getShortLinkAccessRecordStats(requestParam.getFullShortUrl(),
                requestParam.getGid(),
                requestParam.getStartDate(),
                requestParam.getEndDate());
    }

    /**
     * 访问短链接分组指定时间内访问记录监控数据
     *
     * @param requestParam 请求参数
     * @return 短链接分组访问记录监控数据
     */
    @GetMapping("/api/short-link/admin/v1/stats/access-record/group")
    public Result<Page<ShortLinkStatsAccessRecordRespDTO>> getShortLinkGroupAccessRecordStats(ShortLinkGroupStatsAccessRecordReqDTO requestParam) {
        return shortLinkRemoteService.getShortLinkGroupAccessRecordStats(requestParam.getGid(),
                requestParam.getStartDate(),
                requestParam.getEndDate());
    }
}
