package com.beibeijava.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 订单列表视图对象
 */
@Data
public class OrderListVO {

    private Long id;

    private String orderNo;

    private String customerName;

    private String customerPhone;

    private String workerName;

    private String serviceName;

    private LocalDate scheduleDate;

    private String timeSlot;

    private BigDecimal amount;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}