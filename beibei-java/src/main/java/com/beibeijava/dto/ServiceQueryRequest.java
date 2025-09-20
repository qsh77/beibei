package com.beibeijava.dto;

import lombok.Data;

/**
 * 服务查询请求
 */
@Data
public class ServiceQueryRequest {

    private String keyword;

    private Integer status; // 0:禁用 1:启用

    private Integer isHot; // 0:非热门 1:热门

    private Integer page = 1;

    private Integer pageSize = 20;
}