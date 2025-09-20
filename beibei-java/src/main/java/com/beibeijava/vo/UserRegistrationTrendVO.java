package com.beibeijava.vo;

import lombok.Data;

@Data
public class UserRegistrationTrendVO {

    private String date;

    private Long userCount;

    private Long workerCount;
}