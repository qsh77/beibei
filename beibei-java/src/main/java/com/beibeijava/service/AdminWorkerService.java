package com.beibeijava.service;

import com.beibeijava.dto.CreateWorkerRequest;
import com.beibeijava.dto.UpdateWorkerRequest;
import com.beibeijava.dto.WorkerQueryRequest;
import com.beibeijava.entity.User;
import com.beibeijava.entity.UserProfile;
import com.beibeijava.entity.Worker;
import com.beibeijava.mapper.UserMapper;
import com.beibeijava.mapper.UserProfileMapper;
import com.beibeijava.mapper.WorkerMapper;
import com.beibeijava.vo.PageResult;
import com.beibeijava.vo.WorkerDetailVO;
import com.beibeijava.vo.WorkerListVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理员阿姨管理业务层
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminWorkerService {

    private final WorkerMapper workerMapper;
    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 分页查询阿姨列表
     */
    public PageResult<WorkerListVO> getWorkerList(WorkerQueryRequest request) {
        // 参数验证
        request.validate();

        // 计算偏移量
        int offset = (request.getPage() - 1) * request.getSize();

        // 处理关键字搜索
        String keyword = StringUtils.hasText(request.getKeyword()) ? request.getKeyword().trim() : null;

        // 查询阿姨列表
        List<WorkerListVO> workers = workerMapper.findWorkerList(
                keyword,
                request.getLevel(),
                request.getStatus(),
                request.getMinScore(),
                request.getMaxScore(),
                request.getSortBy(),
                offset,
                request.getSize());

        // 查询总数
        Long total = workerMapper.countWorkers(
                keyword,
                request.getLevel(),
                request.getStatus(),
                request.getMinScore(),
                request.getMaxScore());

        return new PageResult<>(workers, total, request.getPage(), request.getSize());
    }

    /**
     * 获取阿姨详情
     */
    public WorkerDetailVO getWorkerDetail(Long workerId) {
        if (workerId == null) {
            throw new RuntimeException("阿姨ID不能为空");
        }

        WorkerDetailVO workerDetail = workerMapper.findWorkerDetail(workerId);
        if (workerDetail == null) {
            throw new RuntimeException("阿姨不存在");
        }

        return workerDetail;
    }

    /**
     * 更新阿姨信息
     */
    @Transactional
    public void updateWorker(Long workerId, UpdateWorkerRequest request) {
        if (workerId == null) {
            throw new RuntimeException("阿姨ID不能为空");
        }

        // 检查阿姨是否存在
        Worker worker = workerMapper.findById(workerId);
        if (worker == null) {
            throw new RuntimeException("阿姨不存在");
        }

        // 更新阿姨信息
        // level和score由系统根据评价自动计算，不允许手动修改
        worker.setYears(request.getYears());
        worker.setBio(request.getBio());
        worker.setStatus(request.getStatus());

        int result = workerMapper.update(worker);
        if (result <= 0) {
            throw new RuntimeException("更新阿姨信息失败");
        }
    }

    /**
     * 更新阿姨状态
     */
    @Transactional
    public void updateWorkerStatus(Long workerId, Integer status) {
        if (workerId == null) {
            throw new RuntimeException("阿姨ID不能为空");
        }

        if (status == null || (status != 0 && status != 1)) {
            throw new RuntimeException("无效的状态值");
        }

        // 检查阿姨是否存在
        Worker worker = workerMapper.findById(workerId);
        if (worker == null) {
            throw new RuntimeException("阿姨不存在");
        }

        // 更新状态
        int result = workerMapper.updateStatus(workerId, status);
        if (result <= 0) {
            throw new RuntimeException("更新阿姨状态失败");
        }
    }

    /**
     * 批量更新阿姨状态
     */
    @Transactional
    public void batchUpdateWorkerStatus(List<Long> workerIds, Integer status) {
        if (workerIds == null || workerIds.isEmpty()) {
            throw new RuntimeException("阿姨ID列表不能为空");
        }

        if (status == null || (status != 0 && status != 1)) {
            throw new RuntimeException("无效的状态值");
        }

        // 批量更新状态
        int result = workerMapper.batchUpdateStatus(workerIds, status);
        if (result <= 0) {
            throw new RuntimeException("批量更新阿姨状态失败");
        }
    }

    /**
     * 直接创建阿姨账号（包含用户账号、资料和阿姨信息）
     */
    @Transactional
    public void createWorkerDirectly(CreateWorkerRequest request) {
        // 检查手机号是否已存在
        int phoneCount = userMapper.countByPhone(request.getPhone());
        if (phoneCount > 0) {
            throw new RuntimeException("手机号已被注册");
        }

        // 检查邮箱是否已存在（如果提供了邮箱）
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            // TODO: 添加邮箱重复检查逻辑（需要在UserMapper中添加相应方法）
        }

        LocalDateTime now = LocalDateTime.now();

        // 1. 创建用户账号
        User user = new User();
        user.setPhone(request.getPhone());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole("WORKER");
        user.setStatus(request.getStatus());
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        int userResult = userMapper.insert(user);
        if (userResult <= 0) {
            throw new RuntimeException("创建用户账号失败");
        }

        // 2. 创建用户资料（使用ON DUPLICATE KEY UPDATE避免主键冲突）
        UserProfile userProfile = new UserProfile();
        userProfile.setUserId(user.getId());
        userProfile.setName(request.getName());
        userProfile.setGender(request.getGender());
        userProfile.setBirthday(request.getBirthday());
        userProfile.setEmail(request.getEmail());
        userProfile.setAddress(request.getAddress());

        try {
            int profileResult = userProfileMapper.insert(userProfile);
            if (profileResult <= 0) {
                throw new RuntimeException("创建用户资料失败");
            }
            log.info("成功创建用户资料，userId: {}, name: {}", user.getId(), request.getName());
        } catch (Exception e) {
            log.error("创建用户资料失败，userId: {}, error: {}", user.getId(), e.getMessage());
            throw new RuntimeException("创建用户资料失败: " + e.getMessage());
        }

        // 3. 创建阿姨专业信息
        Worker worker = new Worker();
        worker.setUserId(user.getId());
        worker.setLevel(request.getLevel());
        worker.setYears(request.getYears());
        worker.setBio(request.getBio());
        worker.setScore(request.getScore() != null ? request.getScore() : new BigDecimal("5.0"));
        worker.setStatus(request.getStatus() != null ? request.getStatus() : 1);

        try {
            // 检查该用户是否已经是阿姨
            Worker existingWorker = workerMapper.findByUserId(user.getId());
            if (existingWorker != null) {
                log.warn("用户已经是阿姨，userId: {}", user.getId());
                throw new RuntimeException("该用户已经是阿姨");
            }

            int workerResult = workerMapper.insert(worker);
            if (workerResult <= 0) {
                throw new RuntimeException("创建阿姨信息失败");
            }
            log.info("成功创建阿姨信息，userId: {}, level: {}, years: {}",
                    user.getId(), request.getLevel(), request.getYears());
        } catch (Exception e) {
            log.error("创建阿姨信息失败，userId: {}, error: {}", user.getId(), e.getMessage());
            throw new RuntimeException("创建阿姨信息失败: " + e.getMessage());
        }
    }

    /**
     * 创建阿姨账号（将普通用户转为阿姨） - 保留原有功能
     */
    @Transactional
    public void createWorkerFromUser(Long userId, Integer level, Integer years, String bio) {
        if (userId == null) {
            throw new RuntimeException("用户ID不能为空");
        }

        // 检查用户是否存在
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查用户是否已经是阿姨
        Worker existingWorker = workerMapper.findByUserId(userId);
        if (existingWorker != null) {
            throw new RuntimeException("该用户已经是阿姨");
        }

        // 创建阿姨记录
        Worker worker = new Worker();
        worker.setUserId(userId);
        worker.setLevel(level != null ? level : 0);
        worker.setYears(years != null ? years : 0);
        worker.setBio(bio);
        worker.setScore(new BigDecimal("0.00")); // 初始评分为0，由客户评价决定
        worker.setStatus(1); // 默认启用

        int result = workerMapper.insert(worker);
        if (result <= 0) {
            throw new RuntimeException("创建阿姨账号失败");
        }

        // 更新用户角色为WORKER
        userMapper.updateRole(userId, "WORKER", LocalDateTime.now());
    }

    /**
     * 删除阿姨（将阿姨转为普通用户）
     */
    @Transactional
    public void removeWorker(Long workerId) {
        if (workerId == null) {
            throw new RuntimeException("阿姨ID不能为空");
        }

        // 检查阿姨是否存在
        Worker worker = workerMapper.findById(workerId);
        if (worker == null) {
            throw new RuntimeException("阿姨不存在");
        }

        // 检查是否有进行中的订单
        // TODO: 添加订单状态检查逻辑

        // 禁用阿姨状态（软删除）
        int result = workerMapper.updateStatus(workerId, 0);
        if (result <= 0) {
            throw new RuntimeException("删除阿姨失败");
        }

        // 将用户角色改回USER
        userMapper.updateRole(worker.getUserId(), "USER", null);
    }

    /**
     * 获取阿姨统计信息
     */
    public WorkerStatsVO getWorkerStats() {
        java.util.Map<String, Object> statsMap = workerMapper.getWorkerStatsMap();

        WorkerStatsVO stats = new WorkerStatsVO();
        stats.setTotalWorkers(((Number) statsMap.get("total_workers")).longValue());
        stats.setActiveWorkers(((Number) statsMap.get("active_workers")).longValue());
        stats.setInactiveWorkers(((Number) statsMap.get("inactive_workers")).longValue());
        stats.setLevel1Count(((Number) statsMap.get("level1_count")).longValue());
        stats.setLevel2Count(((Number) statsMap.get("level2_count")).longValue());
        stats.setLevel3Count(((Number) statsMap.get("level3_count")).longValue());
        stats.setLevel4Count(((Number) statsMap.get("level4_count")).longValue());
        stats.setLevel5Count(((Number) statsMap.get("level5_count")).longValue());

        if (statsMap.get("avg_score") != null) {
            stats.setAvgScore(((Number) statsMap.get("avg_score")).doubleValue());
        }
        if (statsMap.get("max_score") != null) {
            stats.setMaxScore(((Number) statsMap.get("max_score")).doubleValue());
        }
        if (statsMap.get("min_score") != null) {
            stats.setMinScore(((Number) statsMap.get("min_score")).doubleValue());
        }

        return stats;
    }

    /**
     * 搜索阿姨
     */
    public List<WorkerListVO> searchWorkers(String keyword, Integer limit) {
        if (!StringUtils.hasText(keyword)) {
            throw new RuntimeException("搜索关键字不能为空");
        }

        if (limit == null || limit <= 0) {
            limit = 10;
        }

        return workerMapper.findWorkerList(
                keyword.trim(),
                null, null, null, null, null,
                0, limit);
    }

    /**
     * 验证阿姨等级调整
     */
    public boolean canUpgradeLevel(Long workerId, Integer newLevel) {
        if (workerId == null || newLevel == null) {
            return false;
        }

        WorkerDetailVO worker = getWorkerDetail(workerId);
        if (worker == null) {
            return false;
        }

        // 等级提升规则验证
        if (newLevel <= worker.getLevel()) {
            return true; // 降级或保持不变总是允许的
        }

        // 升级规则：
        // - 必须有一定的工作经验
        // - 必须有好的评价
        // - 必须有足够的完成订单数

        int requiredYears = (newLevel - 1) * 2; // 每个等级需要2年经验
        double requiredScore = 3.0 + (newLevel - 1) * 0.5; // 等级越高要求评分越高
        long requiredOrders = newLevel * 10L; // 等级越高需要完成更多订单

        return worker.getYears() >= requiredYears
                && worker.getScore().doubleValue() >= requiredScore
                && worker.getCompletedOrders() >= requiredOrders;
    }

    /**
     * 阿姨统计VO
     */
    public static class WorkerStatsVO {
        private Long totalWorkers;
        private Long activeWorkers;
        private Long inactiveWorkers;
        private Long level1Count;
        private Long level2Count;
        private Long level3Count;
        private Long level4Count;
        private Long level5Count;
        private Double avgScore;
        private Double maxScore;
        private Double minScore;

        // Getters and Setters
        public Long getTotalWorkers() {
            return totalWorkers;
        }

        public void setTotalWorkers(Long totalWorkers) {
            this.totalWorkers = totalWorkers;
        }

        public Long getActiveWorkers() {
            return activeWorkers;
        }

        public void setActiveWorkers(Long activeWorkers) {
            this.activeWorkers = activeWorkers;
        }

        public Long getInactiveWorkers() {
            return inactiveWorkers;
        }

        public void setInactiveWorkers(Long inactiveWorkers) {
            this.inactiveWorkers = inactiveWorkers;
        }

        public Long getLevel1Count() {
            return level1Count;
        }

        public void setLevel1Count(Long level1Count) {
            this.level1Count = level1Count;
        }

        public Long getLevel2Count() {
            return level2Count;
        }

        public void setLevel2Count(Long level2Count) {
            this.level2Count = level2Count;
        }

        public Long getLevel3Count() {
            return level3Count;
        }

        public void setLevel3Count(Long level3Count) {
            this.level3Count = level3Count;
        }

        public Long getLevel4Count() {
            return level4Count;
        }

        public void setLevel4Count(Long level4Count) {
            this.level4Count = level4Count;
        }

        public Long getLevel5Count() {
            return level5Count;
        }

        public void setLevel5Count(Long level5Count) {
            this.level5Count = level5Count;
        }

        public Double getAvgScore() {
            return avgScore;
        }

        public void setAvgScore(Double avgScore) {
            this.avgScore = avgScore;
        }

        public Double getMaxScore() {
            return maxScore;
        }

        public void setMaxScore(Double maxScore) {
            this.maxScore = maxScore;
        }

        public Double getMinScore() {
            return minScore;
        }

        public void setMinScore(Double minScore) {
            this.minScore = minScore;
        }
    }
}