package com.beibeijava.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class CategoryCreateRequest {

    private Long parentId;

    @NotBlank(message = "类目名称不能为空")
    private String name;

    private Integer sort = 0;
}