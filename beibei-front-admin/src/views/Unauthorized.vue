<template>
  <div class="unauthorized">
    <div class="error-container">
      <div class="error-icon">
        <el-icon><Lock /></el-icon>
      </div>
      <h1>403</h1>
      <h2>权限不足</h2>
      <p>抱歉，您没有权限访问该页面</p>

      <div class="actions">
        <el-button type="primary" @click="goBack">返回上一页</el-button>
        <el-button @click="goHome">回到首页</el-button>
        <el-button type="danger" plain @click="logout">重新登录</el-button>
      </div>

      <div class="help-info">
        <p>如果您认为这是一个错误，请联系管理员</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { getHomePageByRole } from '@/router'
import { ElMessage } from 'element-plus'
import { Lock } from '@element-plus/icons-vue'

const router = useRouter()
const authStore = useAuthStore()

const goBack = () => {
  router.back()
}

const goHome = () => {
  const userRole = authStore.userRole
  const homePage = getHomePageByRole(userRole)
  if (homePage === '/unauthorized') {
    router.push('/login')
    return
  }
  router.push(homePage)
}

const logout = () => {
  authStore.logout()
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.unauthorized {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.error-container {
  text-align: center;
  background: white;
  padding: 60px 40px;
  border-radius: 12px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  max-width: 500px;
  width: 100%;
}

.error-icon {
  font-size: 80px;
  color: #ff4757;
  margin-bottom: 20px;
}

h1 {
  font-size: 120px;
  color: #ff4757;
  margin: 0;
  font-weight: 700;
  line-height: 1;
}

h2 {
  font-size: 32px;
  color: #333;
  margin: 16px 0;
  font-weight: 600;
}

p {
  color: #666;
  font-size: 16px;
  margin-bottom: 40px;
  line-height: 1.5;
}

.actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-bottom: 30px;
  flex-wrap: wrap;
}

.help-info {
  border-top: 1px solid #eee;
  padding-top: 24px;
}

.help-info p {
  font-size: 14px;
  color: #999;
  margin: 0;
}

@media (max-width: 768px) {
  .error-container {
    padding: 40px 20px;
  }

  h1 {
    font-size: 80px;
  }

  h2 {
    font-size: 24px;
  }

  .actions {
    flex-direction: column;
    align-items: center;
  }

  .actions .el-button {
    width: 200px;
  }
}
</style>
