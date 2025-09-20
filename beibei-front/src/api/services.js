import request from './request.js'

// 服务相关API
export const servicesApi = {
  // 获取所有服务
  getAllServices() {
    return request.get('/api/services')
  },

  // 根据ID获取服务详情
  getServiceById(id) {
    return request.get(`/api/services/${id}`)
  },

  // 根据类目获取服务
  getServicesByCategory(categoryId) {
    return request.get(`/api/services/category/${categoryId}`)
  },

  // 获取热门服务
  getHotServices(limit = 6) {
    return request.get('/api/services/hot', { params: { limit } })
  },

  // 搜索服务
  searchServices(keyword) {
    return request.get('/api/services/search', { params: { keyword } })
  },

  // 获取所有类目
  getAllCategories() {
    return request.get('/api/services/categories')
  },

  // 获取顶级类目
  getTopLevelCategories() {
    return request.get('/api/services/categories/top')
  },

  // 根据父类目ID获取子类目
  getCategoriesByParentId(parentId) {
    return request.get(`/api/services/categories/${parentId}/children`)
  },

  // 根据ID获取类目详情
  getCategoryById(id) {
    return request.get(`/api/services/categories/${id}`)
  }
}
