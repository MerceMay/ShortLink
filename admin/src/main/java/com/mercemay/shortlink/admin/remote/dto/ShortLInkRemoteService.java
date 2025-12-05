package com.mercemay.shortlink.admin.remote.dto;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mercemay.shortlink.admin.common.convention.result.Result;
import com.mercemay.shortlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import com.mercemay.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import com.mercemay.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.mercemay.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;

import java.util.HashMap;
import java.util.Map;

/**
 * 短链接远程服务接口
 */
public interface ShortLInkRemoteService {

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
     * 分页查询短链接
     *
     * @param requestParm 请求参数
     * @return 结果
     */
    default Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParm) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("gid", requestParm.getGid());
        requestMap.put("current", requestParm.getCurrent());
        requestMap.put("size", requestParm.getSize());
        String resultPageStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/page", requestMap);
        return JSON.parseObject(resultPageStr, new TypeReference<>() {
        });
    }

}
