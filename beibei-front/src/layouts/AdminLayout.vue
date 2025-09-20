<template>
  <div class="admin-layout">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside width="240px" class="admin-sidebar">
        <div class="admin-header">
          <img src="/vite.svg" alt="Logo" class="admin-logo" />
          <h3>贝贝家政管理后台</h3>
        </div>

        <el-menu
          :default-active="activeMenu"
          class="admin-menu"
          router
          unique-opened
        >

          <el-menu-item index="/admin/users">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>

          <el-menu-item index="/admin/workers">
            <el-icon><Avatar /></el-icon>
            <span>阿姨管理</span>
          </el-menu-item>

          <el-menu-item index="/admin/orders">
            <el-icon><Document /></el-icon>
            <span>订单管理</span>
          </el-menu-item>

          <el-menu-item index="/admin/services">
            <el-icon><Grid /></el-icon>
            <span>服务管理</span>
          </el-menu-item>

          <el-menu-item index="/admin/statistics">
            <el-icon><DataLine /></el-icon>
            <span>数据统计</span>
          </el-menu-item>

        </el-menu>
      </el-aside>

      <!-- 主内容区 -->
      <el-container>
        <!-- 顶部导航 -->
        <el-header class="admin-header-bar">
          <div class="header-left">
            <el-breadcrumb separator="/">
              <el-breadcrumb-item to="/admin">管理后台</el-breadcrumb-item>
              <el-breadcrumb-item>{{ breadcrumbTitle }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>

          <div class="header-right">
            <!-- 用户信息下拉菜单 -->
            <el-dropdown @command="handleCommand">
              <span class="user-info">
                <el-avatar :size="32" :src="user?.avatar" />
                <span class="username">{{ user?.name || '管理员' }}</span>
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
        <el-main class="admin-main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import {
  User,
  Avatar,
  Document,
  Grid,
  DataLine,
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
  '/admin/users': '用户管理',
  '/admin/workers': '阿姨管理',
  '/admin/orders': '订单管理',
  '/admin/services': '服务管理',
  '/admin/statistics': '数据统计'
}

const breadcrumbTitle = computed(() => {
  return breadcrumbMap[route.path] || '管理后台'
})

// 处理用户菜单命令
const handleCommand = async (command) => {
  switch (command) {
    case 'profile':
      // 跳转到个人设置
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
.admin-layout {
  height: 100vh;
}

.admin-sidebar {
  background-color: #001529;
  color: white;
}

.admin-header {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid #303030;
}

.admin-logo {
  width: 40px;
  height: 40px;
  margin-bottom: 10px;
}

.admin-header h3 {
  color: white;
  margin: 0;
  font-size: 16px;
  font-weight: normal;
}

.admin-menu {
  border: none;
  background-color: transparent;
}

.admin-menu :deep(.el-menu-item) {
  color: rgba(255, 255, 255, 0.8);
}

.admin-menu :deep(.el-menu-item:hover),
.admin-menu :deep(.el-menu-item.is-active) {
  color: white;
  background-color: #1890ff;
}

.admin-header-bar {
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

.admin-main {
  background-color: #f0f2f5;
  min-height: calc(100vh - 60px);
}
</style>