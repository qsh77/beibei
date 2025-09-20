package com.beibeijava.service;

import com.beibeijava.entity.Order;
import com.beibeijava.mapper.OrderMapper;
import com.beibeijava.mapper.WorkerMapper;
import com.beibeijava.vo.OrderDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 阿姨端订单业务层
 */
@Service
@RequiredArgsConstructor
public class WorkerOrderService {

    private final OrderMapper orderMapper;
    private final WorkerMapper workerMapper;

    /**
     * 获取阿姨的订单列表
     */
    public List<Order> getWorkerOrders() {
        Long workerId = getCurrentWorkerId();
        return orderMapper.findByWorkerId(workerId);
    }

    /**
     * 获取订单详情（阿姨只能查看分配给自己的订单）
     */
    public OrderDetailVO getOrderDetail(Long orderId) {
        Long workerId = getCurrentWorkerId();

        // 验证订单是否分配给当前阿姨
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (!workerId.equals(order.getWorkerId())) {
            throw new RuntimeException("无权查看该订单");
        }

        return orderMapper.findOrderDetailById(orderId);
    }

    /**
     * 接受订单（更新状态为服务中）
     */
    @Transactional
    public void acceptOrder(Long orderId) {
        Long workerId = getCurrentWorkerId();

        Order order = orderMapper.findById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (!workerId.equals(order.getWorkerId())) {
            throw new RuntimeException("无权操作该订单");
        }

        if (!"ASSIGNED".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不允许接受");
        }

        orderMapper.updateStatus(orderId, "DOING", LocalDateTime.now());
    }

    /**
     * 完成订单
     */
    @Transactional
    public void completeOrder(Long orderId) {
        Long workerId = getCurrentWorkerId();

        Order order = orderMapper.findById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (!workerId.equals(order.getWorkerId())) {
            throw new RuntimeException("无权操作该订单");
        }

        if (!"DOING".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不允许完成");
        }

        orderMapper.updateStatus(orderId, "DONE", LocalDateTime.now());
    }

    /**
     * 获取当前阿姨的工人ID
     */
    private Long getCurrentWorkerId() {
        Long userId = getCurrentUserId();

        // 通过用户ID查找对应的工人记录
        var worker = workerMapper.findByUserId(userId);
        if (worker == null) {
            throw new RuntimeException("当前用户不是阿姨");
        }

        return worker.getId();
    }

    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("用户未登录");
        }

        Object details = authentication.getDetails();
        if (details instanceof Long) {
            return (Long) details;
        }

        throw new RuntimeException("无法获取当前用户ID");
    }
}