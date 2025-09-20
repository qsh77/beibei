package com.beibeijava.dto;

import lombok.Data;

/**
 * 阿姨查询请求对象
 */
@Data
public class WorkerQueryRequest {

    /**
     * 页码，从1开始
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer size = 20;

    /**
     * 搜索关键字（手机号或姓名）
     */
    private String keyword;

    /**
     * 等级筛选（1-5星）
     */
    private Integer level;

    /**
     * 状态筛选（0-禁用，1-启用）
     */
    private Integer status;

    /**
     * 最小评分
     */
    private Double minScore;

    /**
     * 最大评分
     */
    private Double maxScore;

    /**
     * 最小工作年限
     */
    private Integer minYears;

    /**
     * 最大工作年限
     */
    private Integer maxYears;

    /**
     * 排序字段
     */
    private String sortBy;

    /**
     * 排序方向：asc-升序，desc-降序
     */
    private String sortDirection = "desc";

    /**
     * 开始日期（注册时间筛选）
     */
    private String startDate;

    /**
     * 结束日期（注册时间筛选）
     */
    private String endDate;

    /**
     * 验证页码和页面大小
     */
    public void validate() {
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1 || size > 100) {
            size = 20;
        }
        if (minScore != null && (minScore < 0 || minScore > 5)) {
            minScore = null;
        }
        if (maxScore != null && (maxScore < 0 || maxScore > 5)) {
            maxScore = null;
        }
        if (minYears != null && minYears < 0) {
            minYears = null;
        }
        if (maxYears != null && maxYears < 0) {
            maxYears = null;
        }
        if (level != null && (level < 1 || level > 5)) {
            level = null;
        }
        if (status != null && (status < 0 || status > 1)) {
            status = null;
        }
        if (sortDirection == null || (!sortDirection.equals("asc") && !sortDirection.equals("desc"))) {
            sortDirection = "desc";
        }
    }
}