package com.beibeijava.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 阿姨表
 */
@Data
public class Worker {

    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 等级
     */
    private Integer level;

    /**
     * 工作年限
     */
    private Integer years;

    /**
     * 个人简介
     */
    private String bio;

    /**
     * 评分
     */
    private BigDecimal score;

    /**
     * 状态：1-启用，0-禁用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private java.time.LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private java.time.LocalDateTime updatedAt;
}
