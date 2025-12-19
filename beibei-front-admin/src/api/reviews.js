import request from './request'

/**
 * 获取订单评价（静默模式，不显示错误提示）
 */
export const getReviewByOrderId = (orderId) => {
  return request({
    url: `/api/reviews/order/${orderId}`,
    method: 'get',
    // 添加自定义配置，用于标识这是一个静默请求
    silent: true
  }).catch(error => {
    // 静默处理错误，返回null而不是抛出异常
    return null
  })
}

/**
 * 删除评价
 */
export const deleteReview = (reviewId) => {
  return request({
    url: `/api/reviews/${reviewId}`,
    method: 'delete'
  })
}

/**
 * 根据订单ID删除评价
 */
export const deleteReviewByOrderId = (orderId) => {
  return request({
    url: `/api/reviews/order/${orderId}`,
    method: 'delete'
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
