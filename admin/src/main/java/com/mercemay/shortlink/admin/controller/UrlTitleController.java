package com.mercemay.shortlink.admin.controller;

import com.mercemay.shortlink.admin.common.convention.result.Result;
import com.mercemay.shortlink.admin.remote.ShortLinkRemoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * URL标题控制器
 */
@RestController("urlTitleControllerByAdmin")
@RequiredArgsConstructor
public class UrlTitleController {
    private final ShortLinkRemoteService shortLinkRemoteService;

    /**
     * 通过URL获取标题
     *
     * @param url 网址
     * @return 标题
     */
    @GetMapping("/api/short-link/admin/v1/title")
    public Result<String> getTitleByUrl(@RequestParam("url") String url) {
        return shortLinkRemoteService.getTitleByUrl(url);
    }
}
