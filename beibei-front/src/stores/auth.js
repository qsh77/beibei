import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, register, getCurrentUser } from '@/api/auth'

export const useAuthStore = defineStore('auth', () => {
  // 状态
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(null)
  const loading = ref(false)

  // 调试token初始化
  console.log('Auth store 初始化，从localStorage获取token:', localStorage.getItem('token'))
  console.log('token.value:', token.value)

  // 计算属性
  const isAuthenticated = computed(() => !!token.value)
  const userRole = computed(() => user.value?.role || '')

  // 登录
  const loginUser = async (credentials) => {
    loading.value = true
    try {
      const response = await login(credentials)
      console.log('登录响应:', response)

      // 响应拦截器返回的是完整的 Result 对象 { code, message, data }
      const { token: responseToken, userInfo } = response.data

      token.value = responseToken
      user.value = userInfo
      localStorage.setItem('token', responseToken)

      console.log('用户登录状态更新:', { token: responseToken, user: userInfo })
      return response
    } catch (error) {
      console.error('登录失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 注册
  const registerUser = async (userData) => {
    loading.value = true
    try {
      await register(userData)
      // 注册成功后自动登录
      return await loginUser({
        phone: userData.phone,
        password: userData.password
      })
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  // 获取当前用户信息
  const fetchCurrentUser = async () => {
    if (!token.value) return

    try {
      const response = await getCurrentUser()
      console.log('获取当前用户响应:', response)

      // 后端 /auth/me 接口返回的是完整的 Result 对象，用户数据在 data 字段中
      user.value = response.data
      console.log('当前用户信息更新:', response.data)
      return response.data
    } catch (error) {
      console.error('获取用户信息失败:', error)
      // Token 可能已过期，清除本地存储
      logout()
      throw error
    }
  }

  // 登出
  const logout = () => {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
  }

  // 初始化时获取用户信息
  const init = async () => {
    console.log('Auth store init 被调用，当前token:', token.value)
    if (token.value) {
      try {
        await fetchCurrentUser()
        console.log('fetchCurrentUser 成功，用户信息:', user.value)
      } catch (error) {
        console.error('Failed to fetch current user:', error)
      }
    } else {
      console.log('没有token，跳过获取用户信息')
    }
  }

  return {
    // 状态
    token,
    user,
    loading,
    // 计算属性
    isAuthenticated,
    userRole,
    // 方法
    loginUser,
    registerUser,
    fetchCurrentUser,
    logout,
    init
  }
})
