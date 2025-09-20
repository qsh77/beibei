package com.beibeijava.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 订单查询请求
 */
@Data
public class OrderQueryRequest {

    private String keyword;

    private String status;

    private LocalDate startDate;

    private LocalDate endDate;

    private Long serviceId;

    private Long workerId;

    private Long customerId;

    private Integer page = 1;

    private Integer size = 20;
}