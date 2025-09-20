package com.beibeijava.service;

import com.beibeijava.dto.UpdateUserStatusRequest;
import com.beibeijava.dto.UserQueryRequest;
import com.beibeijava.entity.User;
import com.beibeijava.mapper.UserMapper;
import com.beibeijava.vo.PageResult;
import com.beibeijava.vo.UserDetailVO;
import com.beibeijava.vo.UserListVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理员用户管理业务层
 */
@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 分页查询用户列表
     */
    public PageResult<UserListVO> getUserList(UserQueryRequest request) {
        // 参数验证
        if (request.getPage() < 1) {
            request.setPage(1);
        }
        if (request.getSize() < 1 || request.getSize() > 100) {
            request.setSize(10);
        }

        // 计算偏移量
        int offset = (request.getPage() - 1) * request.getSize();

        // 处理关键字搜索
        String keyword = StringUtils.hasText(request.getKeyword()) ? request.getKeyword().trim() : null;

        // 查询用户列表
        List<UserListVO> users = userMapper.findUserList(
                keyword,
                request.getRole(),
                request.getStatus(),
                request.getStartDate(),
                request.getEndDate(),
                offset,
                request.getSize()
        );

        // 查询总数
        Long total = userMapper.countUsers(
                keyword,
                request.getRole(),
                request.getStatus(),
                request.getStartDate(),
                request.getEndDate()
        );

        return new PageResult<>(users, total, request.getPage(), request.getSize());
    }

    /**
     * 获取用户详情
     */
    public UserDetailVO getUserDetail(Long userId) {
        if (userId == null) {
            throw new RuntimeException("用户ID不能为空");
        }

        UserDetailVO userDetail = userMapper.findUserDetail(userId);
        if (userDetail == null) {
            throw new RuntimeException("用户不存在");
        }

        return userDetail;
    }

    /**
     * 更新用户状态
     */
    @Transactional
    public void updateUserStatus(Long userId, UpdateUserStatusRequest request) {
        if (userId == null) {
            throw new RuntimeException("用户ID不能为空");
        }

        // 检查用户是否存在
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 不能禁用管理员账号
        if ("ADMIN".equals(user.getRole()) && request.getStatus() == 0) {
            throw new RuntimeException("不能禁用管理员账号");
        }

        // 更新状态
        int result = userMapper.updateStatus(userId, request.getStatus(), LocalDateTime.now());
        if (result <= 0) {
            throw new RuntimeException("更新用户状态失败");
        }
    }

    /**
     * 重置用户密码
     */
    @Transactional
    public void resetUserPassword(Long userId) {
        if (userId == null) {
            throw new RuntimeException("用户ID不能为空");
        }

        // 检查用户是否存在
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 不能重置管理员密码
        if ("ADMIN".equals(user.getRole())) {
            throw new RuntimeException("不能重置管理员密码");
        }

        // 生成默认密码：123456
        String defaultPassword = "123456";
        String encodedPassword = passwordEncoder.encode(defaultPassword);

        // 更新密码
        int result = userMapper.resetPassword(userId, encodedPassword, LocalDateTime.now());
        if (result <= 0) {
            throw new RuntimeException("重置密码失败");
        }
    }

    /**
     * 更新用户角色
     */
    @Transactional
    public void updateUserRole(Long userId, String newRole) {
        if (userId == null) {
            throw new RuntimeException("用户ID不能为空");
        }

        if (!StringUtils.hasText(newRole)) {
            throw new RuntimeException("角色不能为空");
        }

        // 验证角色有效性
        if (!"USER".equals(newRole) && !"WORKER".equals(newRole)) {
            throw new RuntimeException("无效的角色类型");
        }

        // 检查用户是否存在
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 不能修改管理员角色
        if ("ADMIN".equals(user.getRole())) {
            throw new RuntimeException("不能修改管理员角色");
        }

        // 如果角色没有变化，直接返回
        if (newRole.equals(user.getRole())) {
            return;
        }

        // 更新角色
        int result = userMapper.updateRole(userId, newRole, LocalDateTime.now());
        if (result <= 0) {
            throw new RuntimeException("更新用户角色失败");
        }
    }

    /**
     * 批量更新用户状态
     */
    @Transactional
    public void batchUpdateUserStatus(List<Long> userIds, Integer status) {
        if (userIds == null || userIds.isEmpty()) {
            throw new RuntimeException("用户ID列表不能为空");
        }

        if (status == null || (status != 0 && status != 1)) {
            throw new RuntimeException("无效的状态值");
        }

        // 检查是否包含管理员
        for (Long userId : userIds) {
            User user = userMapper.findById(userId);
            if (user != null && "ADMIN".equals(user.getRole()) && status == 0) {
                throw new RuntimeException("批量操作中包含管理员账号，不能禁用");
            }
        }

        // 批量更新状态
        LocalDateTime now = LocalDateTime.now();
        for (Long userId : userIds) {
            userMapper.updateStatus(userId, status, now);
        }
    }

    /**
     * 获取用户统计信息
     */
    public UserStatsVO getUserStats() {
        // 查询各种统计数据
        Long totalUsers = userMapper.countUsers(null, null, null, null, null);
        Long normalUsers = userMapper.countUsers(null, "USER", 1, null, null);
        Long workers = userMapper.countUsers(null, "WORKER", 1, null, null);
        Long disabledUsers = userMapper.countUsers(null, null, 0, null, null);

        // 今日新增用户（需要格式化日期）
        String today = LocalDateTime.now().toLocalDate().toString();
        Long todayNewUsers = userMapper.countUsers(null, null, null, today, today);

        UserStatsVO stats = new UserStatsVO();
        stats.setTotalUsers(totalUsers);
        stats.setNormalUsers(normalUsers);
        stats.setWorkers(workers);
        stats.setDisabledUsers(disabledUsers);
        stats.setTodayNewUsers(todayNewUsers);

        return stats;
    }

    /**
     * 用户统计 VO
     */
    public static class UserStatsVO {
        private Long totalUsers;
        private Long normalUsers;
        private Long workers;
        private Long disabledUsers;
        private Long todayNewUsers;

        // Getters and Setters
        public Long getTotalUsers() { return totalUsers; }
        public void setTotalUsers(Long totalUsers) { this.totalUsers = totalUsers; }

        public Long getNormalUsers() { return normalUsers; }
        public void setNormalUsers(Long normalUsers) { this.normalUsers = normalUsers; }

        public Long getWorkers() { return workers; }
        public void setWorkers(Long workers) { this.workers = workers; }

        public Long getDisabledUsers() { return disabledUsers; }
        public void setDisabledUsers(Long disabledUsers) { this.disabledUsers = disabledUsers; }

        public Long getTodayNewUsers() { return todayNewUsers; }
        public void setTodayNewUsers(Long todayNewUsers) { this.todayNewUsers = todayNewUsers; }
    }
}