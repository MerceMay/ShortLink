package com.mercemay.shortlink.admin.dto.req;

import lombok.Data;

/**
 * 短链接分组排序请求参数
 */
@Data
public class ShortLinkGroupSortReqDTO {
    /**
     * 分组ID
     */
    private String gid;

    /**
     * 排序值
     */
    private Integer sortOrder;
}
