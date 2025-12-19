package com.beibeijava.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 创建阿姨请求对象
 */
@Data
public class CreateWorkerRequest {

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20位之间")
    private String password;

    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空")
    @Size(max = 50, message = "姓名不能超过50个字符")
    private String name;

    /**
     * 性别
     */
    @Pattern(regexp = "^[MFU]$", message = "性别只能是M(男)、F(女)、U(未设置)")
    private String gender = "F";

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 地址
     */
    @Size(max = 200, message = "地址不能超过200个字符")
    private String address;

    /**
     * 等级(1-5星)
     */
    @NotNull(message = "等级不能为空")
    @Min(value = 0, message = "等级不能小于0")
    @Max(value = 5, message = "等级不能大于5")
    private Integer level = 0;

    /**
     * 工作年限
     */
    @NotNull(message = "工作年限不能为空")
    @Min(value = 0, message = "工作年限不能小于0")
    @Max(value = 50, message = "工作年限不能大于50")
    private Integer years;

    /**
     * 个人简介
     */
    @Size(max = 500, message = "个人简介不能超过500字符")
    private String bio;

    /**
     * 初始评分
     */
    @DecimalMin(value = "0.00", message = "评分不能小于0")
    @DecimalMax(value = "5.00", message = "评分不能大于5")
    private BigDecimal score = new BigDecimal("0.00");

    /**
     * 状态：1-启用，0-禁用
     */
    private Integer status = 1;
}