<template>
  <div class="home-container">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <div class="header-content">
        <div class="logo">
          <el-icon size="32" color="#409eff"><House /></el-icon>
          <h2>贝贝家政</h2>
        </div>
        
        <el-menu
          mode="horizontal"
          :default-active="activeMenu"
          class="nav-menu"
          @select="handleMenuSelect"
        >
          <el-menu-item index="/home">首页</el-menu-item>
          <el-menu-item index="/services">服务</el-menu-item>
          <el-menu-item index="/orders" v-if="authStore.isAuthenticated">我的订单</el-menu-item>
        </el-menu>
        
        <div class="user-actions">
          <template v-if="authStore.isAuthenticated">
            <el-dropdown>
              <span class="user-info">
                <el-avatar :size="32" :src="userProfile?.avatar" class="user-avatar">
                  <el-icon><User /></el-icon>
                </el-avatar>
                <span class="user-name">{{ authStore.user?.name || userProfile?.name || '用户' }}</span>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="$router.push('/profile')">个人中心</el-dropdown-item>
                  <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button type="primary" @click="$router.push('/login')">登录</el-button>
            <el-button @click="$router.push('/register')">注册</el-button>
          </template>
        </div>
      </div>
    </el-header>

    <!-- 主要内容 -->
    <el-main class="main-content">
      <!-- 轮播图 -->
      <div class="hero-section">
        <el-carousel height="500px" indicator-position="outside" arrow="hover">
          <el-carousel-item>
            <div class="carousel-item carousel-1">
              <div class="carousel-content">
                <h3>专业家政服务</h3>
                <p>让您的生活更轻松</p>
                <el-button type="primary" size="large" @click="$router.push('/services')">立即预约</el-button>
              </div>
            </div>
          </el-carousel-item>
          <el-carousel-item>
            <div class="carousel-item carousel-2">
              <div class="carousel-content">
                <h3>贴心阿姨服务</h3>
                <p>经验丰富，值得信赖</p>
                <el-button type="primary" size="large" @click="$router.push('/services')">了解详情</el-button>
              </div>
            </div>
          </el-carousel-item>
          <el-carousel-item>
            <div class="carousel-item carousel-3">
              <div class="carousel-content">
                <h3>24小时在线</h3>
                <p>随时为您提供服务</p>
                <el-button type="primary" size="large" @click="$router.push('/services')">开始使用</el-button>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>
      </div>

      <!-- 服务分类 -->
      <div class="services-section">
        <h2 class="section-title">我们的服务</h2>
        <el-row :gutter="30" class="services-grid">
          <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="service in services" :key="service.id">
            <el-card class="service-card" shadow="hover">
              <div class="service-icon">
                <el-icon size="50" color="#409eff">
                  <component :is="service.icon" />
                </el-icon>
              </div>
              <h3>{{ service.name }}</h3>
              <p>{{ service.description }}</p>
              <el-button type="primary" @click="$router.push('/services')">了解详情</el-button>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 特色介绍 -->
      <div class="features-section">
        <h2 class="section-title">为什么选择我们</h2>
        <el-row :gutter="40" class="features-grid">
          <el-col :xs="24" :sm="12" :md="6" v-for="feature in features" :key="feature.id">
            <div class="feature-item">
              <el-icon size="40" :color="feature.color">
                <component :is="feature.icon" />
              </el-icon>
              <h4>{{ feature.title }}</h4>
              <p>{{ feature.description }}</p>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 数据统计 -->
      <div class="stats-section">
        <el-row :gutter="40" class="stats-grid">
          <el-col :xs="12" :sm="6" v-for="stat in stats" :key="stat.id">
            <div class="stat-item">
              <div class="stat-number">{{ stat.number }}</div>
              <div class="stat-label">{{ stat.label }}</div>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-main>

    <!-- 页脚 -->
    <el-footer class="footer">
      <div class="footer-content">
        <el-row :gutter="40">
          <el-col :xs="24" :sm="8">
            <div class="footer-section">
              <h4>贝贝家政</h4>
              <p>专业的家政服务平台，为您提供优质的家政服务</p>
            </div>
          </el-col>
          <el-col :xs="24" :sm="8">
            <div class="footer-section">
              <h4>服务项目</h4>
              <p>家庭保洁 · 深度清洁 · 做饭服务 · 育儿服务</p>
            </div>
          </el-col>
          <el-col :xs="24" :sm="8">
            <div class="footer-section">
              <h4>联系我们</h4>
              <p>客服热线：400-123-4567</p>
              <p>工作时间：24小时在线</p>
            </div>
          </el-col>
        </el-row>
        <div class="footer-bottom">
          <p>&copy; 2024 贝贝家政服务平台. All rights reserved.</p>
        </div>
      </div>
    </el-footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  User, Star, Clock, House, Brush, KnifeFork
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import { userApi } from '@/api/user.js'

const router = useRouter()
const route = useRoute()

// 获取 authStore
const authStore = useAuthStore()
const userProfile = ref(null)

const activeMenu = computed(() => route.path)

const services = ref([
  {
    id: 1,
    name: '家庭保洁',
    description: '专业保洁服务，让您的家焕然一新',
    icon: House
  },
  {
    id: 2,
    name: '深度清洁',
    description: '深度清洁服务，彻底清洁每个角落',
    icon: Brush
  },
  {
    id: 3,
    name: '做饭服务',
    description: '营养美味的家常菜，让您享受家的味道',
    icon: KnifeFork
  },
  {
    id: 4,
    name: '育儿服务',
    description: '专业育儿嫂，细心照顾您的宝宝',
    icon: User
  }
])

const features = ref([
  {
    id: 1,
    title: '专业认证',
    description: '所有阿姨都经过专业培训和认证',
    icon: Star,
    color: '#409eff'
  },
  {
    id: 2,
    title: '安全保障',
    description: '完善的保险体系，让您放心',
    icon: User,
    color: '#67c23a'
  },
  {
    id: 3,
    title: '准时服务',
    description: '严格按照约定时间提供服务',
    icon: Clock,
    color: '#e6a23c'
  },
  {
    id: 4,
    title: '贴心服务',
    description: '24小时客服，随时为您解答疑问',
    icon: House,
    color: '#f56c6c'
  }
])

const stats = ref([
  {
    id: 1,
    number: '10000+',
    label: '服务用户'
  },
  {
    id: 2,
    number: '500+',
    label: '专业阿姨'
  },
  {
    id: 3,
    number: '98%',
    label: '好评率'
  },
  {
    id: 4,
    number: '24h',
    label: '在线服务'
  }
])

const handleMenuSelect = (index) => {
  router.push(index)
}

const handleLogout = () => {
  authStore.logout()
  ElMessage.success('已退出登录')
  router.push('/')
}

// 获取用户资料信息
const fetchUserProfile = async () => {
  if (!authStore.isAuthenticated) return

  try {
    const response = await userApi.getUserProfile()
    userProfile.value = response.data || response
  } catch (error) {
    console.error('获取用户资料失败:', error)
  }
}

// 监听认证状态变化
watch(() => authStore.isAuthenticated, (newVal, oldVal) => {
  console.log('认证状态发生变化:', {
    from: oldVal,
    to: newVal,
    user: authStore.user,
    token: authStore.token
  })

  // 如果已登录，获取用户资料
  if (newVal) {
    fetchUserProfile()
  } else {
    userProfile.value = null
  }
}, { immediate: true })

watch(() => authStore.user, (newVal, oldVal) => {
  console.log('用户信息发生变化:', {
    from: oldVal,
    to: newVal
  })
}, { immediate: true, deep: true })

onMounted(() => {
  console.log('Home.vue mounted successfully')
  console.log('当前认证状态:', {
    isAuthenticated: authStore.isAuthenticated,
    token: authStore.token,
    user: authStore.user
  })
})
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  width: 100%;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
}

/* 头部样式 */
.header {
  background: white;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 0;
  height: 70px;
  position: sticky;
  top: 0;
  z-index: 1000;
  width: 100%;
  margin: 0;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 0 20px;
  width: 100%;
  box-sizing: border-box;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
}

.logo h2 {
  color: #409eff;
  margin: 0;
  font-size: 24px;
  font-weight: bold;
}

.nav-menu {
  border-bottom: none;
  flex: 1;
  justify-content: center;
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
  color: #333;
  padding: 8px 12px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f7fa;
}

/* 主要内容样式 */
.main-content {
  flex: 1;
  padding: 0;
  width: 100%;
  background-color: #f5f7fa;
}

/* 轮播图样式 */
.hero-section {
  margin-bottom: 80px;
  width: 100%;
}

.carousel-item {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: white;
  text-align: center;
  position: relative;
}

.carousel-1 {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.carousel-2 {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.carousel-3 {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.carousel-content h3 {
  font-size: 48px;
  margin-bottom: 20px;
  font-weight: bold;
}

.carousel-content p {
  font-size: 20px;
  margin-bottom: 30px;
  opacity: 0.9;
}

/* 服务分类样式 */
.services-section,
.features-section {
  width: 100%;
  margin: 0;
  padding: 80px 0;
  background-color: white;
}

.services-section > *,
.features-section > * {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.stats-section {
  width: 100%;
  margin: 0;
  padding: 60px 0;
}

.stats-section > * {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.section-title {
  text-align: center;
  font-size: 36px;
  color: #333;
  margin-bottom: 50px;
  font-weight: bold;
}

.services-grid {
  margin-top: 40px;
}

.service-card {
  text-align: center;
  padding: 30px 20px;
  height: 100%;
  transition: transform 0.3s ease;
}

.service-card:hover {
  transform: translateY(-5px);
}

.service-icon {
  margin-bottom: 20px;
}

.service-card h3 {
  color: #333;
  margin-bottom: 15px;
  font-size: 20px;
  font-weight: bold;
}

.service-card p {
  color: #666;
  margin-bottom: 20px;
  line-height: 1.6;
}

/* 特色介绍样式 */
.features-grid {
  margin-top: 40px;
}

.feature-item {
  text-align: center;
  padding: 30px 20px;
}

.feature-item h4 {
  color: #333;
  margin: 20px 0 15px;
  font-size: 18px;
  font-weight: bold;
}

.feature-item p {
  color: #666;
  line-height: 1.6;
}

/* 数据统计样式 */
.stats-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  margin: 0;
  padding: 60px 20px;
}

.stats-grid {
  max-width: 1200px;
  margin: 0 auto;
}

.stat-item {
  text-align: center;
  padding: 20px;
}

.stat-number {
  font-size: 48px;
  font-weight: bold;
  margin-bottom: 10px;
}

.stat-label {
  font-size: 16px;
  opacity: 0.9;
}

/* 页脚样式 */
.footer {
  background: #2c3e50;
  color: white;
  padding: 40px 0 20px;
  width: 100%;
  margin-top: auto;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.footer-section h4 {
  color: white;
  margin-bottom: 15px;
  font-size: 18px;
  font-weight: bold;
}

.footer-section p {
  color: #bdc3c7;
  line-height: 1.6;
  margin-bottom: 10px;
}

.footer-bottom {
  text-align: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #34495e;
  color: #bdc3c7;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    padding: 0 15px;
  }
  
  .logo h2 {
    font-size: 20px;
  }
  
  .carousel-content h3 {
    font-size: 32px;
  }
  
  .carousel-content p {
    font-size: 16px;
  }
  
  .section-title {
    font-size: 28px;
  }
  
  .services-section,
  .features-section {
    padding: 0 15px 60px;
  }
}
</style>
