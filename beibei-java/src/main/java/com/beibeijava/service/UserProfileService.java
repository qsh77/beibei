package com.beibeijava.service;

import com.beibeijava.dto.ChangePasswordRequest;
import com.beibeijava.dto.UpdateProfileRequest;
import com.beibeijava.entity.User;
import com.beibeijava.entity.UserProfile;
import com.beibeijava.mapper.UserMapper;
import com.beibeijava.mapper.UserProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户资料业务层
 */
@Service
@RequiredArgsConstructor
public class UserProfileService {
    
    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * 获取当前用户资料
     */
    public UserProfile getCurrentUserProfile() {
        Long userId = getCurrentUserId();
        UserProfile profile = userProfileMapper.selectById(userId);

        if (profile == null) {
            // 如果用户资料不存在，创建一个默认的（补救措施）
            System.out.println("用户ID " + userId + " 缺少资料记录，自动创建默认资料");
            profile = new UserProfile();
            profile.setUserId(userId);
            profile.setName("用户");
            profile.setGender("U");
            profile.setEmail(null);
            profile.setAddress(null);
            profile.setBirthday(null);
            profile.setAvatar(null);

            int result = userProfileMapper.insert(profile);
            if (result <= 0) {
                throw new RuntimeException("创建默认用户资料失败");
            }
            System.out.println("已为用户ID " + userId + " 创建默认资料");
        }

        return profile;
    }
    
    /**
     * 更新用户资料
     */
    @Transactional
    public void updateProfile(UpdateProfileRequest request) {
        System.out.println("开始更新用户资料");

        Long userId = getCurrentUserId();
        System.out.println("当前用户ID: " + userId);

        UserProfile profile = userProfileMapper.selectById(userId);
        System.out.println("查询到的用户资料: " + profile);

        if (profile == null) {
            // 如果用户资料不存在，创建一个新的
            System.out.println("用户资料不存在，创建新的");
            profile = new UserProfile();
            profile.setUserId(userId);
        }

        profile.setName(request.getName());
        profile.setGender(request.getGender());
        profile.setBirthday(request.getBirthday());
        profile.setAvatar(request.getAvatar());
        profile.setEmail(request.getEmail());
        profile.setAddress(request.getAddress());

        System.out.println("准备保存的用户资料: " + profile);

        int result;
        if (profile.getUserId() == null || userProfileMapper.selectById(userId) == null) {
            System.out.println("执行插入操作");
            result = userProfileMapper.insert(profile);
        } else {
            System.out.println("执行更新操作");
            result = userProfileMapper.update(profile);
        }

        System.out.println("数据库操作结果: " + result);
        if (result <= 0) {
            throw new RuntimeException("数据库操作失败");
        }
    }
    
    /**
     * 修改密码
     */
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        // 验证新密码和确认密码是否一致
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("新密码和确认密码不一致");
        }
        
        Long userId = getCurrentUserId();
        User user = userMapper.findById(userId);
        
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 验证原密码
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new RuntimeException("原密码错误");
        }
        
        // 更新密码
        String newPasswordHash = passwordEncoder.encode(request.getNewPassword());
        userMapper.updatePassword(userId, newPasswordHash, LocalDateTime.now());
    }
    
    /**
     * 获取当前用户信息
     */
    public User getCurrentUser() {
        Long userId = getCurrentUserId();
        return userMapper.findById(userId);
    }
    
    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("用户未登录");
        }

        // 从JWT认证过滤器设置的details中获取用户ID
        Object details = authentication.getDetails();
        if (details instanceof Long) {
            return (Long) details;
        }

        throw new RuntimeException("无法获取当前用户ID");
    }
}
