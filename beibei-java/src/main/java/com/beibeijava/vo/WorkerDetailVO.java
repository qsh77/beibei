package com.beibeijava.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 阿姨详情视图对象
 */
@Data
public class WorkerDetailVO {

    /**
     * 阿姨ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 用户角色
     */
    private String role;

    /**
     * 阿姨姓名
     */
    private String name;

    /**
     * 性别
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

    public String getAvatar() {
        if (avatar != null && !avatar.startsWith("http") && !avatar.startsWith("/")) {
            return "/" + avatar;
        }
        return avatar;
    }

    /**
     * 邮箱
     */
    private String email;

    /**
     * 地址
     */
    private String address;

    /**
     * 等级(1-5星)
     */
    private Integer level;

    /**
     * 工作年限
     */
    private Integer years;

    /**
     * 个人简介
     */
    private String bio;

    /**
     * 系统评分
     */
    private BigDecimal score;

    /**
     * 状态：1-启用，0-禁用
     */
    private Integer status;

    /**
     * 订单总数
     */
    private Long totalOrders;

    /**
     * 完成订单数
     */
    private Long completedOrders;

    /**
     * 平均评价分数
     */
    private Double avgRating;

    /**
     * 评价总数
     */
    private Long totalReviews;

    /**
     * 注册时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 状态描述
     */
    public String getStatusText() {
        return status == 1 ? "正常" : "禁用";
    }

    /**
     * 等级描述
     */
    public String getLevelText() {
        if (level == null)
            return "未评级";
        switch (level) {
            case 1:
                return "一星";
            case 2:
                return "二星";
            case 3:
                return "三星";
            case 4:
                return "四星";
            case 5:
                return "五星";
            default:
                return "未知等级";
        }
    }

    /**
     * 性别描述
     */
    public String getGenderText() {
        if (gender == null)
            return "未设置";
        switch (gender) {
            case "M":
                return "男";
            case "F":
                return "女";
            case "U":
                return "未设置";
            default:
                return "未知";
        }
    }

    /**
     * 完成率
     */
    public Double getCompletionRate() {
        if (totalOrders == null || totalOrders == 0)
            return 0.0;
        return (completedOrders == null ? 0 : completedOrders.doubleValue()) / totalOrders.doubleValue() * 100;
    }

    /**
     * 年龄
     */
    public Integer getAge() {
        if (birthday == null)
            return null;
        return LocalDate.now().getYear() - birthday.getYear();
    }

    /**
     * 工作经验描述
     */
    public String getExperienceText() {
        if (years == null || years == 0)
            return "新手";
        if (years < 2)
            return years + "年 (初级)";
        if (years < 5)
            return years + "年 (中级)";
        if (years < 10)
            return years + "年 (高级)";
        return years + "年 (资深)";
    }

    /**
     * 评价等级描述
     */
    public String getRatingLevel() {
        if (avgRating == null)
            return "暂无评价";
        if (avgRating >= 4.5)
            return "优秀";
        if (avgRating >= 4.0)
            return "良好";
        if (avgRating >= 3.5)
            return "一般";
        if (avgRating >= 3.0)
            return "较差";
        return "差";
    }
}