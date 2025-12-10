package com.mercemay.shortlink.admin.remote;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mercemay.shortlink.admin.common.convention.result.Result;
import com.mercemay.shortlink.admin.dto.req.RecycleBinRecoverReqDTO;
import com.mercemay.shortlink.admin.dto.req.RecycleBinRemoveReqDTO;
import com.mercemay.shortlink.admin.dto.req.RecycleBinSaveReqDTO;
import com.mercemay.shortlink.admin.remote.dto.req.*;
import com.mercemay.shortlink.admin.remote.dto.resp.*;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 短链接远程服务接口
 */
public interface ShortLinkRemoteService {

    /**
     * 创建短链接
     *
     * @param requestParam 请求参数
     * @return 结果
     */
    default Result<ShortLinkCreateRespDTO> createShortLink(ShortLinkCreateReqDTO requestParam) {
        String resultBodyStr = HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/create", JSON.toJSONString((requestParam)));
        return JSON.parseObject(resultBodyStr, new TypeReference<>() {
        });
    }

    /**
     * 批量创建短链接
     *
     * @param requestParam 请求参数
     * @return 结果
     */
    default Result<ShortLinkBatchCreateRespDTO> batchCreateShortLink(ShortLinkBatchCreateReqDTO requestParam) {
        String resultBodyStr = HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/create/batch", JSON.toJSONString((requestParam)));
        return JSON.parseObject(resultBodyStr, new TypeReference<>() {
        });
    }

    /**
     * 更新短链接
     *
     * @param requestParam 请求参数
     */
    default void updateShortLink(ShortLinkUpdateReqDTO requestParam) {
        HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/update", JSON.toJSONString((requestParam)));
    }

    /**
     * 分页查询短链接
     *
     * @param requestParm 请求参数
     * @return 结果
     */
    default Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParm) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("gid", requestParm.getGid());
        requestMap.put("orderTag", requestParm.getOrderTag());
        requestMap.put("current", requestParm.getCurrent());
        requestMap.put("size", requestParm.getSize());
        String resultPageStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/page", requestMap);
        return JSON.parseObject(resultPageStr, new TypeReference<>() {
        });
    }


    /**
     * 查询短链接分组内数量
     *
     * @param requestParam 分组标识列表
     * @return 短链接分组数量列表
     */
    default Result<List<ShortLinkGroupCountQueryRespDTO>> listShortLinkGroupCount(List<String> requestParam) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("requestParam", requestParam);
        String resultListStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/count", requestMap);
        return JSON.parseObject(resultListStr, new TypeReference<>() {
        });
    }

    /**
     * 通过URL获取标题
     *
     * @param url 网址
     * @return 标题
     */
    default Result<String> getTitleByUrl(@RequestParam("url") String url) {
        String resultStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/title?url=" + url);
        return JSON.parseObject(resultStr, new TypeReference<>() {
        });
    }

    /**
     * 保存回收站记录
     *
     * @param recycleBinSaveReqDTO 回收站保存请求参数
     */
    default void saveRecycleBin(RecycleBinSaveReqDTO recycleBinSaveReqDTO) {
        HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/recycle-bin/save", JSON.toJSONString(recycleBinSaveReqDTO));
    }

    /**
     * 分页查询回收站短链接
     *
     * @param requestParm 请求参数
     * @return 结果
     */
    default Result<IPage<ShortLinkPageRespDTO>> pageRecycleBinShortLink(ShortLinkRecycleBinPageReqDTO requestParm) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("gidList", requestParm.getGidList());
        requestMap.put("current", requestParm.getCurrent());
        requestMap.put("size", requestParm.getSize());
        String resultPageStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/recycle-bin/page", requestMap);
        return JSON.parseObject(resultPageStr, new TypeReference<>() {
        });
    }

    /**
     * 恢复回收站短链接
     *
     * @param requestParam 恢复请求参数
     */
    default void recoverRecycleBin(RecycleBinRecoverReqDTO requestParam) {
        HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/recycle-bin/recover", JSON.toJSONString(requestParam));
    }

    /**
     * 删除回收站短链接
     *
     * @param requestParam 删除请求参数
     */
    default void removeRecycleBin(RecycleBinRemoveReqDTO requestParam) {
        HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/recycle-bin/remove", JSON.toJSONString(requestParam));
    }

    /**
     * 获取单个短链接监控数据
     */
    default Result<ShortLinkStatsRespDTO> getShortLinkStats(ShortLinkStatsReqDTO requestParam) {
        String resultBodyStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/stats", BeanUtil.beanToMap(requestParam));
        return JSON.parseObject(resultBodyStr, new TypeReference<>() {
        });
    }

    /**
     * 获取短链接分组监控数据
     *
     * @param requestParam 请求参数
     * @return 短链接分组监控数据
     */
    default Result<ShortLinkStatsRespDTO> getShortLinkGroupStats(ShortLinkGroupStatsReqDTO requestParam) {
        String resultBodyStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/stats/group", BeanUtil.beanToMap(requestParam));
        return JSON.parseObject(resultBodyStr, new TypeReference<>() {
        });
    }

    /**
     * 访问单个短链接指定时间内访问记录监控数据
     *
     * @param requestParam 请求参数
     * @return 短链接访问记录监控数据
     */
    default Result<IPage<ShortLinkStatsAccessRecordRespDTO>> getShortLinkAccessRecordStats(ShortLinkStatsReqDTO requestParam) {
        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(requestParam, false, true);
        stringObjectMap.remove("orders");
        stringObjectMap.remove("records");
        String resultBodyStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/stats/access-record", stringObjectMap);
        return JSON.parseObject(resultBodyStr, new TypeReference<>() {
        });
    }

    /**
     * 访问短链接分组指定时间内访问记录监控数据
     *
     * @param requestParam 请求参数
     * @return 短链接分组访问记录监控数据
     */
    default Result<IPage<ShortLinkStatsAccessRecordRespDTO>> getShortLinkGroupAccessRecordStats(ShortLinkGroupStatsAccessRecordReqDTO requestParam) {
        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(requestParam, false, true);
        stringObjectMap.remove("orders");
        stringObjectMap.remove("records");
        String resultBodyStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/stats/access-record/group", stringObjectMap);
        return JSON.parseObject(resultBodyStr, new TypeReference<>() {
        });
    }
}
