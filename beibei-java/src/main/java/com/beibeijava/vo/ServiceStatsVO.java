package com.beibeijava.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServiceStatsVO {

    private Long serviceId;

    private String serviceName;

    private Long orderCount;

    private BigDecimal revenue;

    private BigDecimal avgRating;
}