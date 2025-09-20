import request from './request.js'

// 地址相关API
export const addressesApi = {
  // 获取地址列表
  getAddresses() {
    return request.get('/api/addresses')
  },

  // 根据ID获取地址详情
  getAddressById(id) {
    return request.get(`/api/addresses/${id}`)
  },

  // 获取默认地址
  getDefaultAddress() {
    return request.get('/api/addresses/default')
  },

  // 添加地址
  addAddress(data) {
    return request.post('/api/addresses', data)
  },

  // 更新地址
  updateAddress(id, data) {
    return request.put(`/api/addresses/${id}`, data)
  },

  // 删除地址
  deleteAddress(id) {
    return request.delete(`/api/addresses/${id}`)
  },

  // 设置默认地址
  setDefaultAddress(id) {
    return request.put(`/api/addresses/${id}/default`)
  }
}
