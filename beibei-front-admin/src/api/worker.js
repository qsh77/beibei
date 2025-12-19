import request from './request'

// 阿姨端API

/**
 * 获取阿姨的订单列表
 */
export const getWorkerOrders = () => {
  return request({
    url: '/api/worker/orders',
    method: 'get'
  })
}

/**
 * 获取订单详情
 */
export const getWorkerOrderDetail = (id) => {
  return request({
    url: `/api/worker/orders/${id}`,
    method: 'get'
  })
}

/**
 * 接受订单
 */
export const acceptOrder = (id) => {
  return request({
    url: `/api/worker/orders/${id}/accept`,
    method: 'put'
  })
}

/**
 * 完成订单
 */
export const completeOrder = (id) => {
  return request({
    url: `/api/worker/orders/${id}/complete`,
    method: 'put'
  })
}

/**
 * 获取阿姨个人资料
 */
export const getWorkerProfile = () => {
  return request({
    url: '/api/worker/profile',
    method: 'get'
  })
}

/**
 * 更新阿姨个人资料
 */
export const updateWorkerProfile = (data) => {
  return request({
    url: '/api/worker/profile',
    method: 'put',
    data
  })
}

/**
 * 获取收入概览
 */
export const getEarningsOverview = () => {
  return request({
    url: '/api/worker/earnings/overview',
    method: 'get'
  })
}

/**
 * 获取收入趋势
 */
export const getEarningsTrend = (days = 7) => {
  return request({
    url: '/api/worker/earnings/trend',
    method: 'get',
    params: { days }
  })
}

/**
 * 获取收入明细
 */
export const getEarningsDetails = (params) => {
  return request({
    url: '/api/worker/earnings/details',
    method: 'get',
    params
  })
}

/**
 * 获取工作档期
 */
export const getWorkerSchedule = (params) => {
  return request({
    url: '/api/worker/schedule',
    method: 'get',
    params
  })
}

/**
 * 更新工作档期
 */
export const updateWorkerSchedule = (data) => {
  return request({
    url: '/api/worker/schedule',
    method: 'put',
    data
  })
}