import axios from 'axios'
import { ElMessage } from 'element-plus'

const TOKEN_KEY = 'beibei-worker-token'

// 创建 axios 实例
const request = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 从 localStorage 直接获取 token，避免循环依赖
    const token = localStorage.getItem(TOKEN_KEY)
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    const { data, config } = response
    console.log('响应拦截器收到数据:', data)

    // 如果后端返回的数据结构是 { code, message, data }
    if (data.code !== undefined) {
      if (data.code === 200) {
        // 直接返回完整的响应数据，让组件自己处理
        return data
      } else {
        // 如果是静默请求，不显示错误提示
        if (!config?.silent) {
          ElMessage.error(data.message || '请求失败')
        }
        return Promise.reject(new Error(data.message || '请求失败'))
      }
    }

    return data
  },
  (error) => {
    const { response, config } = error

    // 如果是静默请求，不显示错误提示
    if (config?.silent) {
      return Promise.reject(error)
    }

    if (response) {
      const { status, data } = response

      switch (status) {
        case 401:
          ElMessage.error('登录已过期，请重新登录')
          // 清除本地存储的 token
          localStorage.removeItem(TOKEN_KEY)
          // 跳转到登录页
          window.location.href = '/login'
          break
        case 403:
          ElMessage.error('没有权限访问')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error(data?.message || '请求失败')
      }
    } else {
      ElMessage.error('网络错误，请检查网络连接')
    }

    return Promise.reject(error)
  }
)

export default request
