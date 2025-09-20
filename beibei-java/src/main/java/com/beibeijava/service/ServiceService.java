package com.beibeijava.service;

import com.beibeijava.entity.ServiceEntity;
import com.beibeijava.entity.ServiceCategory;
import com.beibeijava.mapper.ServiceMapper;
import com.beibeijava.mapper.ServiceCategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 服务业务层
 */
@Service
@RequiredArgsConstructor
public class ServiceService {
    
    private final ServiceMapper serviceMapper;
    private final ServiceCategoryMapper serviceCategoryMapper;
    
    /**
     * 获取所有启用的服务
     */
    public List<ServiceEntity> getAllActiveServices() {
        return serviceMapper.findAllActive();
    }
    
    /**
     * 根据ID获取服务详情
     */
    public ServiceEntity getServiceById(Long id) {
        return serviceMapper.findById(id);
    }
    
    
    /**
     * 获取热门服务
     */
    public List<ServiceEntity> getHotServices(int limit) {
        return serviceMapper.findHotServices(limit);
    }
    
    /**
     * 搜索服务
     */
    public List<ServiceEntity> searchServices(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllActiveServices();
        }
        return serviceMapper.searchServices(keyword.trim());
    }
    
    /**
     * 获取所有服务类目
     */
    public List<ServiceCategory> getAllCategories() {
        return serviceCategoryMapper.findAll();
    }
    
    /**
     * 获取顶级类目
     */
    public List<ServiceCategory> getTopLevelCategories() {
        return serviceCategoryMapper.findTopLevel();
    }
    
    /**
     * 根据父类目ID获取子类目
     */
    public List<ServiceCategory> getCategoriesByParentId(Long parentId) {
        return serviceCategoryMapper.findByParentId(parentId);
    }
    
    /**
     * 根据ID获取类目详情
     */
    public ServiceCategory getCategoryById(Long id) {
        return serviceCategoryMapper.findById(id);
    }
}
