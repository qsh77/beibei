package com.beibeijava.service;

import com.beibeijava.dto.CreateOrderRequest;
import com.beibeijava.entity.Order;
import com.beibeijava.entity.ServiceEntity;
import com.beibeijava.entity.UserAddress;
import com.beibeijava.mapper.OrderMapper;
import com.beibeijava.mapper.ServiceMapper;
import com.beibeijava.mapper.UserAddressMapper;
import com.beibeijava.vo.OrderDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 订单业务层
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;
    private final ServiceMapper serviceMapper;
    private final UserAddressMapper userAddressMapper;

    /**
     * 创建订单
     */
    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        Long userId = getCurrentUserId();

        // 验证服务是否存在
        ServiceEntity service = serviceMapper.findById(request.getServiceId());
        if (service == null) {
            throw new RuntimeException("服务不存在");
        }

        // 验证地址是否属于当前用户
        UserAddress address = userAddressMapper.findById(request.getAddressId());
        if (address == null || !address.getUserId().equals(userId)) {
            throw new RuntimeException("地址不存在或不属于当前用户");
        }

        // 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setCustomerId(userId);
        order.setWorkerId(null); // 新订单暂未分配阿姨
        order.setServiceId(request.getServiceId());
        order.setScheduleDate(request.getScheduleDate());
        order.setTimeSlot(request.getTimeSlot());
        order.setAddressId(request.getAddressId());
        order.setAddressText(buildAddressText(address));
        order.setAmount(service.getBasePrice());
        order.setStatus("CREATED");
        order.setRemark(request.getRemark());
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        orderMapper.insert(order);
        return order;
    }

    /**
     * 获取当前用户的订单列表
     */
    public List<Order> getUserOrders() {
        Long userId = getCurrentUserId();
        return orderMapper.findByCustomerId(userId);
    }

    /**
     * 根据ID获取订单详情
     */
    public Order getOrderById(Long id) {
        Order order = orderMapper.findById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 验证订单是否属于当前用户
        Long userId = getCurrentUserId();
        if (!order.getCustomerId().equals(userId)) {
            throw new RuntimeException("无权访问该订单");
        }

        return order;
    }

    /**
     * 获取订单完整详情（含阿姨信息）
     */
    public OrderDetailVO getOrderDetail(Long id) {
        OrderDetailVO detail = orderMapper.findOrderDetailById(id);
        if (detail == null) {
            throw new RuntimeException("订单不存在");
        }

        // 验证订单是否属于当前用户
        Long userId = getCurrentUserId();
        if (!detail.getCustomerId().equals(userId)) {
            throw new RuntimeException("无权访问该订单");
        }

        return detail;
    }

    /**
     * 取消订单
     */
    @Transactional
    public void cancelOrder(Long id) {
        Order order = getOrderById(id);

        if (!"CREATED".equals(order.getStatus()) && !"ASSIGNED".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不允许取消");
        }

        orderMapper.updateStatus(id, "CANCELED", LocalDateTime.now());
    }

    /**
     * 获取待分配订单列表（管理员/阿姨使用）
     */
    public List<Order> getPendingOrders() {
        return orderMapper.findPendingOrders();
    }

    /**
     * 分配订单给阿姨
     */
    @Transactional
    public void assignOrder(Long orderId, Long workerId) {
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (!"CREATED".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不允许分配");
        }

        orderMapper.assignWorker(orderId, workerId, "ASSIGNED", LocalDateTime.now());
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "BB" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    /**
     * 构建地址文本
     */
    private String buildAddressText(UserAddress address) {
        return address.getProvince() + address.getCity() + address.getDistrict() + address.getDetail();
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
