package com.mercemay.shortlink.project.service;

/**
 * URL标题服务接口
 */
public interface UrlTitleService {
    /**
     * 通过URL获取标题
     *
     * @param url 网址
     * @return 标题
     */
    String getTitleByUrl(String url);
}
