package com.beibeijava.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

/**
 * 更新用户资料请求 DTO
 */
@Data
public class UpdateProfileRequest {
    
    @NotBlank(message = "姓名不能为空")
    private String name;
    
    private String gender;
    
    private LocalDate birthday;
    
    private String avatar;
    
    private String email;
    
    private String address;
}
