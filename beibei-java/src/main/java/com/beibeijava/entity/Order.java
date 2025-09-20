package com.beibeijava.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 订单表
 */
@Data
public class Order {
    
    private Long id;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 客户ID
     */
    private Long customerId;
    
    /**
     * 阿姨ID
     */
    private Long workerId;
    
    /**
     * 服务ID
     */
    private Long serviceId;
    
    /**
     * 预约日期
     */
    private LocalDate scheduleDate;
    
    /**
     * 时间段
     */
    private String timeSlot;
    
    /**
     * 地址ID
     */
    private Long addressId;
    
    /**
     * 地址文本（快照）
     */
    private String addressText;
    
    /**
     * 订单金额
     */
    private BigDecimal amount;
    
    /**
     * 订单状态：CREATED、ASSIGNED、DOING、DONE、CANCELED
     */
    private String status;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
