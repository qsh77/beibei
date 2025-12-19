package com.beibeijava.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 阿姨收入聚合结果
 */
@Data
public class WorkerEarningsAggregate {

    private BigDecimal total;

    private Long orderCount;
}
