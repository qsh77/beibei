package com.beibeijava.service;

import com.beibeijava.dto.ServiceCreateRequest;
import com.beibeijava.dto.ServiceQueryRequest;
import com.beibeijava.dto.ServiceUpdateRequest;
import com.beibeijava.entity.ServiceEntity;
import com.beibeijava.mapper.ServiceMapper;
import com.beibeijava.vo.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceService {

    private final ServiceMapper serviceMapper;

    /**
     * 获取所有服务（管理员视图）
     */
    public List<ServiceEntity> getAllServices() {
        return serviceMapper.findAll();
    }

    /**
     * 分页查询服务列表
     */
    public PageResult<ServiceEntity> getServiceList(ServiceQueryRequest request) {
        // 计算偏移量
        int offset = (request.getPage() - 1) * request.getPageSize();

        // 查询数据
        List<ServiceEntity> services = serviceMapper.findAllWithPaging(
            request.getKeyword(),
            request.getStatus(),
            request.getIsHot(),
            offset,
            request.getPageSize()
        );

        // 查询总数
        Long total = serviceMapper.countAll(
            request.getKeyword(),
            request.getStatus(),
            request.getIsHot()
        );

        return new PageResult<>(services, total, request.getPage(), request.getPageSize());
    }

    /**
     * 根据ID获取服务
     */
    public ServiceEntity getServiceById(Long id) {
        return serviceMapper.findByIdForAdmin(id);
    }

    /**
     * 创建服务
     */
    @Transactional
    public ServiceEntity createService(ServiceCreateRequest request) {
        ServiceEntity service = new ServiceEntity();
        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setBasePrice(request.getBasePrice());
        service.setUnit(request.getUnit());
        service.setHot(request.getHot());
        service.setStatus(1);
        service.setCreatedAt(LocalDateTime.now());

        serviceMapper.insert(service);
        return service;
    }

    /**
     * 更新服务
     */
    @Transactional
    public ServiceEntity updateService(ServiceUpdateRequest request) {
        ServiceEntity existing = serviceMapper.findByIdForAdmin(request.getId());
        if (existing == null) {
            throw new RuntimeException("服务不存在");
        }

        ServiceEntity service = new ServiceEntity();
        service.setId(request.getId());
        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setBasePrice(request.getBasePrice());
        service.setUnit(request.getUnit());
        service.setHot(request.getHot());
        service.setStatus(request.getStatus());

        serviceMapper.update(service);
        return serviceMapper.findByIdForAdmin(request.getId());
    }

    /**
     * 删除服务
     */
    @Transactional
    public void deleteService(Long id) {
        ServiceEntity service = serviceMapper.findByIdForAdmin(id);
        if (service == null) {
            throw new RuntimeException("服务不存在");
        }
        serviceMapper.deleteById(id);
    }

    /**
     * 更新服务状态
     */
    @Transactional
    public void updateServiceStatus(Long id, Integer status) {
        ServiceEntity service = serviceMapper.findByIdForAdmin(id);
        if (service == null) {
            throw new RuntimeException("服务不存在");
        }
        serviceMapper.updateStatus(id, status);
    }

    /**
     * 更新热门状态
     */
    @Transactional
    public void updateServiceHot(Long id, Integer hot) {
        ServiceEntity service = serviceMapper.findByIdForAdmin(id);
        if (service == null) {
            throw new RuntimeException("服务不存在");
        }
        serviceMapper.updateHot(id, hot);
    }

}