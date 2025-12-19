package com.beibeijava.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 阿姨收入明细视图
 */
@Data
public class WorkerEarningDetailVO {

    private Long orderId;

    private String orderNo;

    private String serviceName;

    private String customerName;

    private String customerPhone;

    private BigDecimal amount;

    private BigDecimal commission;

    private BigDecimal earnings;

    private String status;

    private LocalDateTime completedAt;
}
