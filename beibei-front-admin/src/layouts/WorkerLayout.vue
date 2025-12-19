<template>
  <div class="flex h-screen w-full bg-slate-50">
    <!-- 侧边栏 -->
    <aside 
      class="fixed inset-y-0 left-0 z-50 w-64 bg-white shadow-soft transition-transform duration-300 transform lg:translate-x-0 lg:static lg:inset-0 flex flex-col"
    >
      <!-- Logo 区域 -->
      <div class="h-20 flex items-center gap-3 px-8 border-b border-slate-100">
        <div class="w-8 h-8 rounded-lg bg-gradient-to-br from-indigo-500 to-purple-600 flex items-center justify-center shadow-lg shadow-indigo-500/30">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
          </svg>
        </div>
        <span class="text-xl font-bold text-slate-800 tracking-tight">阿姨工作台</span>
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
            业务中心
          </div>

          <el-menu-item index="/worker/orders" class="menu-item-custom">
            <el-icon><Document /></el-icon>
            <span>我的订单</span>
          </el-menu-item>

          <el-menu-item index="/worker/earnings" class="menu-item-custom">
            <el-icon><Money /></el-icon>
            <span>收入管理</span>
          </el-menu-item>

          <el-menu-item index="/worker/schedule" class="menu-item-custom">
            <el-icon><Calendar /></el-icon>
            <span>档期管理</span>
          </el-menu-item>

          <div class="text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2 px-4 mt-6">
            个人设置
          </div>

          <el-menu-item index="/worker/profile" class="menu-item-custom">
            <el-icon><User /></el-icon>
            <span>个人资料</span>
          </el-menu-item>
        </el-menu>
      </div>

      <!-- 底部用户信息 -->
      <div class="p-4 border-t border-slate-100">
        <div class="flex items-center gap-3 p-3 rounded-xl bg-slate-50 hover:bg-slate-100 transition-colors cursor-pointer" @click="router.push('/worker/profile')">
          <el-avatar :size="36" :src="user?.avatar" class="border-2 border-white shadow-sm" />
          <div class="flex-1 min-w-0">
            <p class="text-sm font-medium text-slate-900 truncate">{{ user?.name || '阿姨' }}</p>
            <p class="text-xs text-slate-500 truncate">专业服务人员</p>
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
            <el-breadcrumb-item :to="{ path: '/worker' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ breadcrumbTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="flex items-center gap-4">
          <!-- 消息通知 -->
          <el-tooltip content="消息通知" placement="bottom">
            <button class="p-2 text-slate-400 hover:text-indigo-600 transition-colors relative">
              <el-icon :size="20"><Bell /></el-icon>
              <!-- 示例红点 -->
              <!-- <span class="absolute top-1.5 right-1.5 w-2 h-2 bg-red-500 rounded-full border-2 border-white"></span> -->
            </button>
          </el-tooltip>
          
          <div class="h-6 w-px bg-slate-200 mx-1"></div>

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
  Document,
  User,
  Money,
  Calendar,
  ArrowDown,
  Bell,
  SwitchButton
} from '@element-plus/icons-vue'
const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const user = computed(() => authStore.user)
const activeMenu = computed(() => route.path)

const breadcrumbMap = {
  '/worker/orders': '我的订单',
  '/worker/earnings': '收入管理',
  '/worker/schedule': '档期管理',
  '/worker/profile': '个人资料'
}

const breadcrumbTitle = computed(() => {
  return breadcrumbMap[route.path] || '工作台'
})

const handleCommand = async (command) => {
  switch (command) {
    case 'profile':
      router.push('/worker/profile')
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
/* 自定义菜单项样式 - 与 Admin 保持一致 */
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