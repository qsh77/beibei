package com.beibeijava.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新用户状态请求
 */
@Data
public class UpdateUserStatusRequest {

    /**
     * 用户状态：1-启用，0-禁用
     */
    @NotNull(message = "状态不能为空")
    private Integer status;

    /**
     * 操作原因
     */
    private String reason;
}