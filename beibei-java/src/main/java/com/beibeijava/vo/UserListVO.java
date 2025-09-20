package com.beibeijava.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户列表视图对象
 */
@Data
public class UserListVO {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 用户名
     */
    private String name;

    /**
     * 角色
     */
    private String role;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 订单总数
     */
    private Long totalOrders;

    /**
     * 注册时间
     */
    private LocalDateTime createdAt;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginAt;

    /**
     * 状态描述
     */
    public String getStatusText() {
        return status == 1 ? "正常" : "禁用";
    }

    /**
     * 角色描述
     */
    public String getRoleText() {
        switch (role) {
            case "USER": return "普通用户";
            case "WORKER": return "阿姨";
            case "ADMIN": return "管理员";
            default: return "未知";
        }
    }
}