package com.beibeijava.dto;

import lombok.Data;

/**
 * 用户查询请求
 */
@Data
public class UserQueryRequest {

    /**
     * 页码
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;

    /**
     * 关键字搜索（手机号、姓名）
     */
    private String keyword;

    /**
     * 角色筛选
     */
    private String role;

    /**
     * 状态筛选
     */
    private Integer status;

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;
}