package com.beibeijava.entity;

import lombok.Data;

/**
 * 服务类目表
 */
@Data
public class ServiceCategory {
    
    private Long id;
    
    /**
     * 父类目ID
     */
    private Long parentId;
    
    /**
     * 类目名称
     */
    private String name;
    
    /**
     * 排序
     */
    private Integer sort;
}
