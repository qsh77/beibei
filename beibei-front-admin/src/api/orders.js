import request from './request'

// 用户端订单API

/**
 * 创建订单
 */
export const createOrder = (data) => {
  return request({
    url: '/api/orders',
    method: 'post',
    data
  })
}

/**
 * 获取订单列表
 */
export const getUserOrders = () => {
  return request({
    url: '/api/orders',
    method: 'get'
  })
}

/**
 * 根据ID获取订单详情
 */
export const getOrderById = (id) => {
  return request({
    url: `/api/orders/${id}`,
    method: 'get'
  })
}

/**
 * 取消订单
 */
export const cancelOrder = (id) => {
  return request({
    url: `/api/orders/${id}/cancel`,
    method: 'put'
  })
}

/**
 * 获取待分配订单（管理员/阿姨使用）
 */
export const getPendingOrders = () => {
  return request({
    url: '/api/orders/pending',
    method: 'get'
  })
}

/**
 * 分配订单
 */
export const assignOrder = (id, workerId) => {
  return request({
    url: `/api/orders/${id}/assign`,
    method: 'put',
    params: { workerId }
  })
}

// 兼容旧的导出格式 - 管理员订单API
export const adminOrdersApi = {
  // 分页查询订单列表
  getOrderList(params) {
    return request({
      url: '/api/admin/orders',
      method: 'get',
      params
    })
  },

  // 获取订单详情
  getOrderDetail(id) {
    return request({
      url: `/api/admin/orders/${id}`,
      method: 'get'
    })
  },

  // 获取订单统计
  getOrderStats() {
    return request({
      url: '/api/admin/orders/stats',
      method: 'get'
    })
  },

}

// 兼容旧的导出格式 - 阿姨端订单API
export const workerOrdersApi = {
  // 获取阿姨的订单列表
  getWorkerOrders() {
    return request({
      url: '/api/worker/orders',
      method: 'get'
    })
  },

  // 获取可接单的订单列表
  getAvailableOrders() {
    return request({
      url: '/api/worker/orders/available',
      method: 'get'
    })
  },

  // 获取订单详情
  getOrderDetail(id) {
    return request({
      url: `/api/worker/orders/${id}`,
      method: 'get'
    })
  },

  // 接受订单
  acceptOrder(id) {
    return request({
      url: `/api/worker/orders/${id}/accept`,
      method: 'put'
    })
  },

  // 阿姨抢单
  takeOrder(id) {
    return request({
      url: `/api/worker/orders/${id}/take`,
      method: 'put'
    })
  },

  // 完成订单
  completeOrder(id) {
    return request({
      url: `/api/worker/orders/${id}/complete`,
      method: 'put'
    })
  }
}