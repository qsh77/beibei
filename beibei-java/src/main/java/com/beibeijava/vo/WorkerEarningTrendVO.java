package com.beibeijava.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 阿姨收入趋势数据
 */
@Data
public class WorkerEarningTrendVO {

    private LocalDate date;

    private BigDecimal amount;
}
