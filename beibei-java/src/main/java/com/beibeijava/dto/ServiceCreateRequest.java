package com.beibeijava.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
public class ServiceCreateRequest {

    @NotBlank(message = "服务名称不能为空")
    private String name;

    private String description;

    @NotNull(message = "基础价格不能为空")
    @Positive(message = "价格必须大于0")
    private BigDecimal basePrice;

    @NotBlank(message = "计价单位不能为空")
    private String unit;

    private Integer hot = 0;
}