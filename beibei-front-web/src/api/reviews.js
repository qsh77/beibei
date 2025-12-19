import request from './request'

/**
 * 创建评价
 */
export const createReview = (data) => {
  return request({
    url: '/api/reviews',
    method: 'post',
    data
  })
}

/**
 * 获取订单评价
 * @param {number} orderId - 订单ID
 * @param {object} options - 选项配置
 * @param {boolean} options.silent - 是否静默处理错误（不弹出错误提示）
 */
export const getReviewByOrderId = (orderId, options = {}) => {
  return request({
    url: `/api/reviews/order/${orderId}`,
    method: 'get',
    silent: options.silent
  })
}

/**
 * 获取阿姨的评价列表
 */
export const getWorkerReviews = (workerId) => {
  return request({
    url: `/api/reviews/worker/${workerId}`,
    method: 'get'
  })
}

/**
 * 获取服务的评价列表
 */
export const getServiceReviews = (serviceId) => {
  return request({
    url: `/api/reviews/service/${serviceId}`,
    method: 'get'
  })
}
