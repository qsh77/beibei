<template>
  <div class="worker-layout">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside width="240px" class="worker-sidebar">
        <div class="worker-header">
          <img src="/vite.svg" alt="Logo" class="worker-logo" />
          <h3>阿姨工作台</h3>
        </div>

        <el-menu
          :default-active="activeMenu"
          class="worker-menu"
          router
          unique-opened
        >

          <el-menu-item index="/worker/orders">
            <el-icon><Document /></el-icon>
            <span>我的订单</span>
          </el-menu-item>

          <el-menu-item index="/worker/schedule">
            <el-icon><Calendar /></el-icon>
            <span>档期管理</span>
          </el-menu-item>

          <el-menu-item index="/worker/earnings">
            <el-icon><Money /></el-icon>
            <span>收入统计</span>
          </el-menu-item>

          <el-menu-item index="/worker/profile">
            <el-icon><User /></el-icon>
            <span>个人资料</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主内容区 -->
      <el-container>
        <!-- 顶部导航 -->
        <el-header class="worker-header-bar">
          <div class="header-left">
            <el-breadcrumb separator="/">
              <el-breadcrumb-item to="/worker">阿姨工作台</el-breadcrumb-item>
              <el-breadcrumb-item>{{ breadcrumbTitle }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>

          <div class="header-right">
            <!-- 用户信息下拉菜单 -->
            <el-dropdown @command="handleCommand">
              <span class="user-info">
                <el-avatar :size="32" :src="user?.avatar" />
                <span class="username">{{ user?.name || '阿姨' }}</span>
                <el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人设置</el-dropdown-item>
                  <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <!-- 页面内容 -->
        <el-main class="worker-main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import {
  Document,
  Calendar,
  Money,
  User,
  ArrowDown
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const user = computed(() => authStore.user)

// 当前激活的菜单
const activeMenu = computed(() => route.path)

// 面包屑标题映射
const breadcrumbMap = {
  '/worker/orders': '我的订单',
  '/worker/schedule': '档期管理',
  '/worker/earnings': '收入统计',
  '/worker/profile': '个人资料'
}

const breadcrumbTitle = computed(() => {
  return breadcrumbMap[route.path] || '阿姨工作台'
})

// 处理用户菜单命令
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
          type: 'warning'
        })

        authStore.logout()
        ElMessage.success('已退出登录')
        router.push('/login')
      } catch {
        // 用户取消
      }
      break
  }
}
</script>

<style scoped>
.worker-layout {
  height: 100vh;
}

.worker-sidebar {
  background-color: #1890ff;
  color: white;
}

.worker-header {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.worker-logo {
  width: 40px;
  height: 40px;
  margin-bottom: 10px;
}

.worker-header h3 {
  color: white;
  margin: 0;
  font-size: 16px;
  font-weight: normal;
}

.worker-menu {
  border: none;
  background-color: transparent;
}

.worker-menu :deep(.el-menu-item) {
  color: rgba(255, 255, 255, 0.8);
}

.worker-menu :deep(.el-menu-item:hover),
.worker-menu :deep(.el-menu-item.is-active) {
  color: white;
  background-color: rgba(255, 255, 255, 0.2);
}

.worker-header-bar {
  background-color: white;
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
}

.header-left {
  flex: 1;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f5f5;
}

.username {
  margin: 0 8px;
  font-size: 14px;
}

.worker-main {
  background-color: #f0f2f5;
  min-height: calc(100vh - 60px);
}
</style>