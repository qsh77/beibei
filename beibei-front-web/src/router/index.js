import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

// 用户端路由
const userRoutes = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/services',
    name: 'Services',
    component: () => import('@/views/Services.vue'),
    meta: { title: '服务列表' }
  },
  {
    path: '/service-process',
    name: 'ServiceProcess',
    component: () => import('@/views/ServiceProcess.vue'),
    meta: { title: '服务流程' }
  },
  {
    path: '/book-service/:serviceId?',
    name: 'BookService',
    component: () => import('@/views/BookService.vue'),
    meta: { title: '预约服务', requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/Profile.vue'),
    meta: { title: '个人中心', requiresAuth: true }
  },
  {
    path: '/orders',
    name: 'Orders',
    component: () => import('@/views/Orders.vue'),
    meta: { title: '我的订单', requiresAuth: true }
  }
]

// 公共路由
const publicRoutes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', requiresGuest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册', requiresGuest: true }
  },
  {
    path: '/unauthorized',
    name: 'Unauthorized',
    component: () => import('@/views/Unauthorized.vue'),
    meta: { title: '权限不足' }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: { title: '页面不存在' }
  }
]

const routes = [
  ...userRoutes,
  ...publicRoutes
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()

  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 贝贝家政` : '贝贝家政'

  // 如果有token但没有用户信息，先尝试获取用户信息
  if (authStore.token && !authStore.user) {
    try {
      await authStore.fetchCurrentUser()
    } catch (error) {
      console.error('路由守卫：获取用户信息失败', error)
      // token可能过期，清除并跳转到登录页
      authStore.logout()
      if (to.meta.requiresAuth) {
        next('/login')
        return
      }
    }
  }

  // 检查是否需要登录
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
    return
  }

  // 检查是否需要游客状态（已登录用户不能访问登录/注册页）
  if (to.meta.requiresGuest && authStore.isAuthenticated) {
    // 根据用户角色跳转到对应的首页
    const userRole = authStore.userRole
    switch (userRole) {
      case 'USER':
        next('/home')
        break
      case 'ADMIN':
      case 'WORKER':
        next('/unauthorized')
        break
      default:
        next('/login')
        break
    }
    return
  }

  next()
})

// 路由跳转工具函数
export const getHomePageByRole = (role) => {
  switch (role) {
    case 'USER':
    default:
      return '/home'
  }
}

export default router
