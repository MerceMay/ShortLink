package com.mercemay.shortlink.admin.util;

import com.alibaba.excel.EasyExcel;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * EasyExcel Web 工具类
 */
public class EasyExcelWebUtil {
    /**
     * 向浏览器响应 Excel 文件
     *
     * @param response 响应对象
     * @param filename 文件名
     * @param clazz    数据对象类型
     * @param data     数据列表
     */
    @SneakyThrows
    public static void write(HttpServletResponse response, String filename, Class<?> clazz, List<?> data) {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        filename = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("\\+", "%20"); // 处理空格
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + filename + ".xlsx");
        EasyExcel.write(response.getOutputStream(), clazz).sheet("Sheet").doWrite(data);
    }
}
