package com.beibeijava.vo;

import lombok.Data;

/**
 * 登录响应 VO
 */
@Data
public class LoginResponse {
    
    /**
     * JWT Token
     */
    private String token;
    
    /**
     * 用户信息
     */
    private UserInfo userInfo;
    
    @Data
    public static class UserInfo {
        private Long id;
        private String phone;
        private String role;
        private String name;
        private String avatar;
    }
}
