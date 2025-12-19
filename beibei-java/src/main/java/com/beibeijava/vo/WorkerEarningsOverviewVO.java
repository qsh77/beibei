package com.beibeijava.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 阿姨收入概览
 */
@Data
public class WorkerEarningsOverviewVO {

    private BigDecimal todayEarnings;

    private BigDecimal weekEarnings;

    private BigDecimal monthEarnings;

    private BigDecimal totalEarnings;

    private Long totalOrderCount;

    private BigDecimal averageOrderAmount;

    private Double serviceRating;
}
