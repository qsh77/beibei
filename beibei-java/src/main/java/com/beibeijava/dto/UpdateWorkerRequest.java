package com.beibeijava.dto;

import lombok.Data;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 更新阿姨信息请求对象
 */
@Data
public class UpdateWorkerRequest {

    /**
     * 等级(1-5星)
     */
    @NotNull(message = "等级不能为空")
    @Min(value = 1, message = "等级不能小于1")
    @Max(value = 5, message = "等级不能大于5")
    private Integer level;

    /**
     * 工作年限
     */
    @NotNull(message = "工作年限不能为空")
    @Min(value = 0, message = "工作年限不能小于0")
    @Max(value = 50, message = "工作年限不能大于50")
    private Integer years;

    /**
     * 个人简介
     */
    @Size(max = 500, message = "个人简介不能超过500字符")
    private String bio;

    /**
     * 评分
     */
    @DecimalMin(value = "0.00", message = "评分不能小于0")
    @DecimalMax(value = "5.00", message = "评分不能大于5")
    private BigDecimal score;

    /**
     * 状态：1-启用，0-禁用
     */
    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态值无效")
    @Max(value = 1, message = "状态值无效")
    private Integer status;

    /**
     * 更新原因
     */
    @Size(max = 200, message = "更新原因不能超过200字符")
    private String reason;
}