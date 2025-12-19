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
 * 获取订单详情（含阿姨信息）
 */
export const getOrderDetail = (id) => {
  return request({
    url: `/api/orders/${id}/detail`,
    method: 'get'
  })
}
