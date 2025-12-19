package com.beibeijava.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * 阿姨个人资料更新请求
 */
@Data
public class WorkerProfileUpdateRequest {

    /**
     * 姓名
     */
    @Size(max = 50, message = "姓名不能超过50个字符")
    private String name;

    /**
     * 性别：M/F/U
     */
    private String gender;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 邮箱
     */
    @Size(max = 100, message = "邮箱不能超过100个字符")
    private String email;

    /**
     * 地址
     */
    @Size(max = 200, message = "地址不能超过200个字符")
    private String address;

    /**
     * 头像地址
     */
    @Size(max = 300, message = "头像地址过长")
    private String avatar;

    /**
     * 工作年限
     */
    @Min(value = 0, message = "工作年限不能小于0")
    @Max(value = 50, message = "工作年限不能大于50")
    private Integer years;

    /**
     * 个人简介
     */
    @Size(max = 500, message = "个人简介不能超过500字符")
    private String bio;

    /**
     * 等级（可选）
     */
    @Min(value = 1, message = "等级不能小于1")
    @Max(value = 5, message = "等级不能大于5")
    private Integer level;
}
