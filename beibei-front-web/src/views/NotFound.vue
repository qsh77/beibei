<template>
  <div class="not-found">
    <div class="error-container">
      <div class="error-icon">
        <el-icon><DocumentDelete /></el-icon>
      </div>
      <h1>404</h1>
      <h2>页面不存在</h2>
      <p>抱歉，您访问的页面不存在或已被删除</p>

      <div class="actions">
        <el-button type="primary" @click="goBack">返回上一页</el-button>
        <el-button @click="goHome">回到首页</el-button>
      </div>

      <div class="suggestions">
        <h3>您可以尝试：</h3>
        <ul>
          <li>检查URL是否正确</li>
          <li>返回首页重新导航</li>
          <li>联系客服获得帮助</li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { getHomePageByRole } from '@/router'
import { DocumentDelete } from '@element-plus/icons-vue'

const router = useRouter()
const authStore = useAuthStore()

const goBack = () => {
  router.back()
}

const goHome = () => {
  if (authStore.isAuthenticated) {
    const userRole = authStore.userRole
    const homePage = getHomePageByRole(userRole)
    router.push(homePage)
  } else {
    router.push('/home')
  }
}
</script>

<style scoped>
.not-found {
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
  max-width: 600px;
  width: 100%;
}

.error-icon {
  font-size: 80px;
  color: #ffa502;
  margin-bottom: 20px;
}

h1 {
  font-size: 120px;
  color: #ffa502;
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
  margin-bottom: 40px;
  flex-wrap: wrap;
}

.suggestions {
  border-top: 1px solid #eee;
  padding-top: 32px;
  text-align: left;
}

.suggestions h3 {
  color: #333;
  font-size: 18px;
  margin-bottom: 16px;
  text-align: center;
}

.suggestions ul {
  color: #666;
  font-size: 14px;
  line-height: 1.8;
  margin: 0;
  padding-left: 20px;
}

.suggestions li {
  margin-bottom: 8px;
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

  .suggestions {
    text-align: center;
  }

  .suggestions ul {
    text-align: left;
    display: inline-block;
  }
}
</style>