package com.beibeijava.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderTrendVO {

    private String date;

    private Long orderCount;

    private BigDecimal revenue;
}