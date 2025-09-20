package com.beibeijava.service;

import com.beibeijava.dto.AddressRequest;
import com.beibeijava.entity.UserAddress;
import com.beibeijava.mapper.UserAddressMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 地址业务层
 */
@Service
@RequiredArgsConstructor
public class AddressService {
    
    private final UserAddressMapper userAddressMapper;
    
    /**
     * 获取当前用户的地址列表
     */
    public List<UserAddress> getUserAddresses() {
        Long userId = getCurrentUserId();
        return userAddressMapper.findByUserId(userId);
    }
    
    /**
     * 根据ID获取地址详情
     */
    public UserAddress getAddressById(Long id) {
        UserAddress address = userAddressMapper.findById(id);
        if (address == null) {
            throw new RuntimeException("地址不存在");
        }
        
        // 验证地址是否属于当前用户
        Long userId = getCurrentUserId();
        if (!address.getUserId().equals(userId)) {
            throw new RuntimeException("无权访问该地址");
        }
        
        return address;
    }
    
    /**
     * 获取当前用户的默认地址
     */
    public UserAddress getDefaultAddress() {
        Long userId = getCurrentUserId();
        return userAddressMapper.findDefaultByUserId(userId);
    }
    
    /**
     * 添加地址
     */
    @Transactional
    public void addAddress(AddressRequest request) {
        System.out.println("开始添加地址服务");

        Long userId = getCurrentUserId();
        System.out.println("当前用户ID: " + userId);

        UserAddress address = new UserAddress();
        address.setUserId(userId);
        address.setContactName(request.getContactName());
        address.setContactPhone(request.getContactPhone());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setDistrict(request.getDistrict());
        address.setDetail(request.getDetail());
        address.setIsDefault(request.getIsDefault() ? 1 : 0);
        address.setCreatedAt(LocalDateTime.now());

        System.out.println("准备插入的地址: " + address);

        // 如果设置为默认地址，先取消其他默认地址
        if (request.getIsDefault()) {
            System.out.println("清除其他默认地址");
            int clearResult = userAddressMapper.clearDefaultByUserId(userId);
            System.out.println("清除默认地址结果: " + clearResult);
        }

        int result = userAddressMapper.insert(address);
        System.out.println("插入地址结果: " + result);

        if (result <= 0) {
            throw new RuntimeException("地址插入失败");
        }

        System.out.println("地址添加完成，生成的ID: " + address.getId());
    }
    
    /**
     * 更新地址
     */
    @Transactional
    public void updateAddress(Long id, AddressRequest request) {
        UserAddress address = getAddressById(id);
        
        address.setContactName(request.getContactName());
        address.setContactPhone(request.getContactPhone());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setDistrict(request.getDistrict());
        address.setDetail(request.getDetail());
        
        // 如果设置为默认地址，先取消其他默认地址
        if (request.getIsDefault() && address.getIsDefault() == 0) {
            userAddressMapper.clearDefaultByUserId(address.getUserId());
        }
        
        address.setIsDefault(request.getIsDefault() ? 1 : 0);
        
        userAddressMapper.update(address);
    }
    
    /**
     * 删除地址
     */
    public void deleteAddress(Long id) {
        getAddressById(id); // 验证地址存在且属于当前用户
        userAddressMapper.deleteById(id);
    }
    
    /**
     * 设置默认地址
     */
    @Transactional
    public void setDefaultAddress(Long id) {
        UserAddress address = getAddressById(id);
        
        // 先取消其他默认地址
        userAddressMapper.clearDefaultByUserId(address.getUserId());
        
        // 设置当前地址为默认
        userAddressMapper.setDefaultById(id);
    }
    
    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("用户未登录");
        }

        // 从JWT认证过滤器设置的details中获取用户ID
        Object details = authentication.getDetails();
        if (details instanceof Long) {
            return (Long) details;
        }

        throw new RuntimeException("无法获取当前用户ID");
    }
}
