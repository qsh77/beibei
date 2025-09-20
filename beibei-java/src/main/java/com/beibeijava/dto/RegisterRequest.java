package com.beibeijava.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 注册请求 DTO
 */
@Data
public class RegisterRequest {
    
    @NotBlank(message = "手机号不能为空")
    private String phone;
    
    @NotBlank(message = "密码不能为空")
    private String password;
    
    @NotBlank(message = "姓名不能为空")
    private String name;
}
