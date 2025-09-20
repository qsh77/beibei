package com.beibeijava.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户地址表
 */
@Data
public class UserAddress {
    
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 联系人姓名
     */
    private String contactName;
    
    /**
     * 联系人电话
     */
    private String contactPhone;
    
    /**
     * 省份
     */
    private String province;
    
    /**
     * 城市
     */
    private String city;
    
    /**
     * 区县
     */
    private String district;
    
    /**
     * 详细地址
     */
    private String detail;
    
    /**
     * 是否默认地址：1-是，0-否
     */
    private Integer isDefault;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
