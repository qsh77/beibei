package com.beibeijava.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 阿姨列表视图对象
 */
@Data
public class WorkerListVO {

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
     * 阿姨姓名
     */
    private String name;

    /**
     * 性别
     */
    private String gender;

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
     * 评分
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
     * 注册时间
     */
    private LocalDateTime createdAt;

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
}