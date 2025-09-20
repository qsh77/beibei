package com.beibeijava.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

/**
 * 创建订单请求 DTO
 */
@Data
public class CreateOrderRequest {
    
    @NotNull(message = "服务ID不能为空")
    private Long serviceId;
    
    @NotNull(message = "预约日期不能为空")
    private LocalDate scheduleDate;
    
    @NotBlank(message = "时间段不能为空")
    private String timeSlot;
    
    @NotNull(message = "地址ID不能为空")
    private Long addressId;
    
    private String remark;
}
