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

// 管理员路由
const adminRoutes = [
  {
    path: '/admin',
    name: 'AdminLayout',
    component: () => import('@/layouts/AdminLayout.vue'),
    redirect: '/admin/users',
    meta: { title: '管理后台', requiresAuth: true, requiresRole: 'ADMIN' },
    children: [
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/Users.vue'),
        meta: { title: '用户管理', requiresAuth: true, requiresRole: 'ADMIN' }
      },
      {
        path: 'workers',
        name: 'AdminWorkers',
        component: () => import('@/views/admin/Workers.vue'),
        meta: { title: '阿姨管理', requiresAuth: true, requiresRole: 'ADMIN' }
      },
      {
        path: 'orders',
        name: 'AdminOrders',
        component: () => import('@/views/admin/Orders.vue'),
        meta: { title: '订单管理', requiresAuth: true, requiresRole: 'ADMIN' }
      },
      {
        path: 'services',
        name: 'AdminServices',
        component: () => import('@/views/admin/Services.vue'),
        meta: { title: '服务管理', requiresAuth: true, requiresRole: 'ADMIN' }
      },
      {
        path: 'statistics',
        name: 'AdminStatistics',
        component: () => import('@/views/admin/Statistics.vue'),
        meta: { title: '数据统计', requiresAuth: true, requiresRole: 'ADMIN' }
      },
    ]
  }
]

// 阿姨工作台路由（未来扩展）
const workerRoutes = [
  {
    path: '/worker',
    name: 'WorkerLayout',
    component: () => import('@/layouts/WorkerLayout.vue'),
    redirect: '/worker/orders',
    meta: { title: '阿姨工作台', requiresAuth: true, requiresRole: 'WORKER' },
    children: [
      {
        path: 'orders',
        name: 'WorkerOrders',
        component: () => import('@/views/worker/Orders.vue'),
        meta: { title: '我的订单', requiresAuth: true, requiresRole: 'WORKER' }
      },
      {
        path: 'schedule',
        name: 'WorkerSchedule',
        component: () => import('@/views/worker/Schedule.vue'),
        meta: { title: '档期管理', requiresAuth: true, requiresRole: 'WORKER' }
      },
      {
        path: 'earnings',
        name: 'WorkerEarnings',
        component: () => import('@/views/worker/Earnings.vue'),
        meta: { title: '收入统计', requiresAuth: true, requiresRole: 'WORKER' }
      },
      {
        path: 'profile',
        name: 'WorkerProfile',
        component: () => import('@/views/worker/Profile.vue'),
        meta: { title: '个人资料', requiresAuth: true, requiresRole: 'WORKER' }
      }
    ]
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
  ...adminRoutes,
  ...workerRoutes,
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

  // 检查角色权限
  if (to.meta.requiresRole && authStore.isAuthenticated) {
    const userRole = authStore.userRole
    const requiredRole = to.meta.requiresRole

    if (userRole !== requiredRole) {
      console.warn(`权限不足：需要${requiredRole}角色，当前用户角色：${userRole}`)
      next('/unauthorized')
      return
    }
  }

  // 检查是否需要游客状态（已登录用户不能访问登录/注册页）
  if (to.meta.requiresGuest && authStore.isAuthenticated) {
    // 根据用户角色跳转到对应的首页
    const userRole = authStore.userRole
    switch (userRole) {
      case 'ADMIN':
        next('/admin/users')
        break
      case 'WORKER':
        next('/worker/orders')
        break
      case 'USER':
      default:
        next('/home')
        break
    }
    return
  }

  next()
})

// 路由跳转工具函数
export const getHomePageByRole = (role) => {
  switch (role) {
    case 'ADMIN':
      return '/admin/users'
    case 'WORKER':
      return '/worker/orders'
    case 'USER':
    default:
      return '/home'
  }
}

export default router
