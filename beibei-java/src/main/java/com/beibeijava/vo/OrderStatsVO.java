package com.beibeijava.vo;

import lombok.Data;

/**
 * 订单统计视图对象
 */
@Data
public class OrderStatsVO {

    private Long total;

    private Long pending;

    private Long assigned;

    private Long doing;

    private Long done;

    private Long canceled;
}