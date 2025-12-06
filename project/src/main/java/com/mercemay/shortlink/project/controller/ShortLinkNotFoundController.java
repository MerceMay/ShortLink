package com.mercemay.shortlink.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 短链接未找到控制器
 */
@Controller
public class ShortLinkNotFoundController {
    /**
     * 处理短链接未找到的请求
     */
    @RequestMapping("/page/notfound")
    public String notfound() {
        return "notfound";
    }
}
