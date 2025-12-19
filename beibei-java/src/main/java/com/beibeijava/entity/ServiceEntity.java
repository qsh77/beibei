package com.beibeijava.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 服务表
 */
@Data
public class ServiceEntity {
    
    private Long id;

    /**
     * 服务名称
     */
    private String name;
    
    /**
     * 服务描述
     */
    private String description;
    
    /**
     * 基础价格
     */
    private BigDecimal basePrice;
    
    /**
     * 计价单位：小时、天、单
     */
    private String unit;
    
    /**
     * 状态：1-启用，0-禁用
     */
    private Integer status;
    
    /**
     * 是否热门：1-是，0-否
     */
    private Integer hot;

    /**
     * 类目ID
     */
    private Long categoryId;
    
    /**
     * 评分
     */
    private BigDecimal rating;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
