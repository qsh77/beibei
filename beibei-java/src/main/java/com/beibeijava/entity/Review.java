package com.beibeijava.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订单评价实体
 */
@Data
public class Review {

    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 评分（1-5分）
     */
    private Integer rating;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 评价图片（JSON格式）
     */
    private String imgs;

    /**
     * 创建人（用户ID）
     */
    private Long createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
