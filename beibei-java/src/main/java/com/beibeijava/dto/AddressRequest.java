package com.beibeijava.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 地址请求 DTO
 */
@Data
public class AddressRequest {
    
    @NotBlank(message = "联系人姓名不能为空")
    private String contactName;
    
    @NotBlank(message = "联系人电话不能为空")
    private String contactPhone;
    
    @NotBlank(message = "省份不能为空")
    private String province;
    
    @NotBlank(message = "城市不能为空")
    private String city;
    
    @NotBlank(message = "区县不能为空")
    private String district;
    
    @NotBlank(message = "详细地址不能为空")
    private String detail;
    
    @NotNull(message = "是否默认地址不能为空")
    private Boolean isDefault;
}
