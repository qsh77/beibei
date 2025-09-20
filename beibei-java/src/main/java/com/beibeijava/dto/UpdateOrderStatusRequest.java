package com.beibeijava.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 更新订单状态请求
 */
@Data
public class UpdateOrderStatusRequest {

    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @NotBlank(message = "订单状态不能为空")
    private String status;

    private String note;
}