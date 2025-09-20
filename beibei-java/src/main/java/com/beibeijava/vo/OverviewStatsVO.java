package com.beibeijava.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OverviewStatsVO {

    private Long totalUsers;

    private Long totalWorkers;

    private Long totalOrders;

    private BigDecimal totalRevenue;

    private Long todayOrders;

    private BigDecimal todayRevenue;

    private Long monthOrders;

    private BigDecimal monthRevenue;
}