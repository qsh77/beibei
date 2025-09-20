package com.beibeijava.entity;

import lombok.Data;

import java.time.LocalDate;

/**
 * 用户资料表
 */
@Data
public class UserProfile {
    
    private Long userId;
    
    /**
     * 姓名
     */
    private String name;
    
    /**
     * 性别：M-男，F-女，U-未知
     */
    private String gender;
    
    /**
     * 生日
     */
    private LocalDate birthday;
    
    /**
     * 头像
     */
    private String avatar;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 地址
     */
    private String address;
}
