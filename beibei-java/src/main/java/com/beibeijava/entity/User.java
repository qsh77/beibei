package com.beibeijava.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户表
 */
@Data
public class User {
    
    private Long id;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 密码哈希
     */
    private String passwordHash;
    
    /**
     * 角色：USER-用户，WORKER-阿姨，ADMIN-管理员
     */
    private String role;
    
    /**
     * 状态：1-启用，0-禁用
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
