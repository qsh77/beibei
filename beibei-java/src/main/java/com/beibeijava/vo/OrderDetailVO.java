package com.beibeijava.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 订单详情视图对象
 */
@Data
public class OrderDetailVO {

    private Long id;

    private String orderNo;

    private Long customerId;
    private String customerName;
    private String customerPhone;
    private String customerAvatar;

    private Long workerId;
    private String workerName;
    private String workerPhone;
    private String workerAvatar;
    private Integer workerLevel;
    private Integer workerYears;
    private BigDecimal workerScore;

    private Long serviceId;
    private String serviceName;
    private String serviceDescription;
    private String serviceUnit;
    private String categoryName;

    private LocalDate scheduleDate;

    private String timeSlot;

    private Long addressId;
    private String addressText;
    private String contactName;
    private String contactPhone;

    private BigDecimal amount;

    private String status;

    private String remark;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}