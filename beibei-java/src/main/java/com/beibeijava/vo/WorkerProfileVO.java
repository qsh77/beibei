package com.beibeijava.vo;

import lombok.Data;

import java.time.LocalDate;

/**
 * 阿姨个人资料视图对象
 */
@Data
public class WorkerProfileVO {

    private Long workerId;

    private Long userId;

    private String name;

    private String phone;

    private String gender;

    private LocalDate birthday;

    private String avatar;

    private String email;

    private String address;

    private Integer level;

    private Integer years;

    private String bio;

    private Double score;

    private Long totalOrders;

    private Long completedOrders;

    private Double avgRating;

    private Long totalReviews;
}
