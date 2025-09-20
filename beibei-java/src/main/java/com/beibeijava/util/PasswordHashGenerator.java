package com.beibeijava.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "qsh265219";
        String hashedPassword = encoder.encode(password);
        System.out.println("原始密码: " + password);
        System.out.println("BCrypt哈希: " + hashedPassword);

        // 验证密码
        boolean matches = encoder.matches(password, hashedPassword);
        System.out.println("验证结果: " + matches);

        // 验证数据库中现有的哈希
        String dbHash = "$2a$10$Ss6E0pKdA2jU5pAhXhXcBOsZ4gBK1Xr5p9vT0oP4HdNfV.R2u3Hru";
        boolean dbMatches = encoder.matches(password, dbHash);
        System.out.println("数据库哈希验证结果: " + dbMatches);
    }
}