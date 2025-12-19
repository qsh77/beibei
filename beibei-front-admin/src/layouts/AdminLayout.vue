<template>
  <div class="flex h-screen w-full bg-slate-50">
    <!-- 侧边栏 -->
    <aside 
      class="fixed inset-y-0 left-0 z-50 w-64 bg-white shadow-soft transition-transform duration-300 transform lg:translate-x-0 lg:static lg:inset-0 flex flex-col"
    >
      <!-- Logo 区域 -->
      <div class="h-20 flex items-center gap-3 px-8 border-b border-slate-100">
        <div class="w-8 h-8 rounded-lg bg-gradient-primary flex items-center justify-center shadow-lg shadow-indigo-500/30">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
          </svg>
        </div>
        <span class="text-xl font-bold text-slate-800 tracking-tight">贝贝家政</span>
      </div>

      <!-- 菜单区域 -->
      <div class="flex-1 overflow-y-auto py-6 px-4">
        <el-menu
          :default-active="activeMenu"
          class="!border-none w-full bg-transparent gap-1 flex flex-col"
          router
          unique-opened
        >
          <div class="text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2 px-4 mt-2">
            管理中心
          </div>

          <el-menu-item index="/admin/users" class="menu-item-custom">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>

          <el-menu-item index="/admin/workers" class="menu-item-custom">
            <el-icon><Avatar /></el-icon>
            <span>阿姨管理</span>
          </el-menu-item>

          <el-menu-item index="/admin/orders" class="menu-item-custom">
            <el-icon><Document /></el-icon>
            <span>订单管理</span>
          </el-menu-item>

          <el-menu-item index="/admin/services" class="menu-item-custom">
            <el-icon><Grid /></el-icon>
            <span>服务管理</span>
          </el-menu-item>

          <div class="text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2 px-4 mt-6">
            分析与设置
          </div>

          <el-menu-item index="/admin/statistics" class="menu-item-custom">
            <el-icon><DataAnalysis /></el-icon>
            <span>数据统计</span>
          </el-menu-item>
        </el-menu>
      </div>

      <!-- 底部用户信息 -->
      <div class="p-4 border-t border-slate-100">
        <div class="flex items-center gap-3 p-3 rounded-xl bg-slate-50 hover:bg-slate-100 transition-colors cursor-pointer" @click="router.push('/admin/profile')">
          <el-avatar :size="36" :src="user?.avatar" class="border-2 border-white shadow-sm" />
          <div class="flex-1 min-w-0">
            <p class="text-sm font-medium text-slate-900 truncate">{{ user?.name || '管理员' }}</p>
            <p class="text-xs text-slate-500 truncate">超级管理员</p>
          </div>
        </div>
      </div>
    </aside>

    <!-- 主内容区 -->
    <div class="flex-1 flex flex-col min-w-0 overflow-hidden bg-slate-50/50">
      <!-- 顶部导航 -->
      <header class="bg-white/80 backdrop-blur-md sticky top-0 z-40 border-b border-slate-200/60 px-8 h-16 flex items-center justify-between">
        <div class="flex items-center gap-4">
          <el-breadcrumb separator="/" class="text-sm">
            <el-breadcrumb-item :to="{ path: '/admin' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ breadcrumbTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="flex items-center gap-4">
          <el-dropdown @command="handleCommand">
            <span class="flex items-center gap-2 cursor-pointer outline-none group">
              <span class="text-sm font-medium text-slate-600 group-hover:text-indigo-600 transition-colors">账户设置</span>
              <el-icon class="text-slate-400 group-hover:text-indigo-600 transition-colors"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu class="min-w-[160px] p-2">
                <el-dropdown-item command="profile" class="rounded-md">
                  <el-icon><User /></el-icon>个人设置
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided class="text-red-500 hover:!bg-red-50 hover:!text-red-600 rounded-md">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 页面内容 -->
      <main class="flex-1 overflow-auto p-8 relative scroll-smooth">
        <router-view v-slot="{ Component }">
          <transition 
            name="fade-slide" 
            mode="out-in"
          >
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import {
  User,
  Avatar,
  Document,
  Grid,
  DataAnalysis,
  ArrowDown,
  SwitchButton
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const user = computed(() => authStore.user)
const activeMenu = computed(() => route.path)

const breadcrumbMap = {
  '/admin/users': '用户管理',
  '/admin/workers': '阿姨管理',
  '/admin/orders': '订单管理',
  '/admin/services': '服务管理',
  '/admin/statistics': '数据统计',
  '/admin/profile': '个人设置'
}

const breadcrumbTitle = computed(() => {
  return breadcrumbMap[route.path] || '仪表盘'
})

const handleCommand = async (command) => {
  switch (command) {
    case 'profile':
      router.push('/admin/profile')
      break
    case 'logout':
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
          center: true
        })
        authStore.logout()
        ElMessage.success('已退出登录')
        router.push('/login')
      } catch {}
      break
  }
}
</script>

<style scoped>
/* 自定义菜单项样式 */
.menu-item-custom {
  margin-bottom: 4px;
  border-radius: 0.75rem; /* rounded-xl */
  height: 50px;
  line-height: 50px;
  color: #64748b; /* slate-500 */
  border: none !important;
}

.menu-item-custom:hover {
  background-color: #f1f5f9 !important; /* slate-100 */
  color: #4f46e5 !important; /* indigo-600 */
}

.menu-item-custom.is-active {
  background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
  color: white !important;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
}

.menu-item-custom .el-icon {
  font-size: 20px;
  margin-right: 12px;
}

/* 页面切换动画 */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>