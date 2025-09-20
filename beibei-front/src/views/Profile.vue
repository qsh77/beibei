<template>
  <div class="profile-page">
    <div class="profile-container">
      <el-card class="profile-header" shadow="hover">
        <div class="header-content">
          <div class="user-info">
            <el-avatar :size="80" :src="userProfile?.avatar" class="avatar">
              <el-icon><User /></el-icon>
            </el-avatar>
            <div class="user-details">
              <h2>{{ userProfile?.name || '未设置姓名' }}</h2>
              <p class="phone">{{ userInfo?.phone }}</p>
              <el-tag v-if="userProfile?.gender" :type="genderType">
                {{ genderText }}
              </el-tag>
            </div>
          </div>
          <div class="header-actions">
            <el-button
              type="primary"
              :icon="House"
              @click="goToHome"
              class="home-button"
              round
            >
              返回首页
            </el-button>
          </div>
        </div>
      </el-card>

      <div class="profile-content">
        <el-tabs v-model="activeTab" type="card" class="profile-tabs">
          <el-tab-pane label="个人信息" name="profile">
            <!-- 查看/编辑模式切换 -->
            <ProfileView
              v-if="!isEditing"
              ref="profileViewRef"
              @edit="handleStartEdit"
            />
            <ProfileForm
              v-else
              ref="profileFormRef"
              @save-success="handleSaveSuccess"
              @cancel="handleCancelEdit"
            />
          </el-tab-pane>

          <el-tab-pane label="地址管理" name="address">
            <AddressManage />
          </el-tab-pane>

          <el-tab-pane label="修改密码" name="password">
            <PasswordChange />
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { User, House } from '@element-plus/icons-vue'
import { userApi } from '@/api/user.js'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
import ProfileView from '@/components/ProfileView.vue'
import ProfileForm from '@/components/ProfileForm.vue'
import AddressManage from '@/components/AddressManage.vue'
import PasswordChange from '@/components/PasswordChange.vue'

const authStore = useAuthStore()
const router = useRouter()
const activeTab = ref('profile')
const userInfo = ref(null)
const userProfile = ref(null)
const loading = ref(false)
const isEditing = ref(false)

// 组件引用
const profileViewRef = ref()
const profileFormRef = ref()

const genderType = computed(() => {
  if (!userProfile.value?.gender) return ''
  return userProfile.value.gender === 'M' ? 'primary' : 'success'
})

const genderText = computed(() => {
  if (!userProfile.value?.gender) return ''
  const genderMap = { M: '男', F: '女', U: '未知' }
  return genderMap[userProfile.value.gender] || '未知'
})

const fetchUserData = async () => {
  try {
    loading.value = true

    // 初始化基础数据
    userInfo.value = authStore.user || {}
    userProfile.value = {}

    // 获取详细的用户资料信息
    try {
      const [infoResponse, profileResponse] = await Promise.all([
        userApi.getUserInfo(),
        userApi.getUserProfile()
      ])

      console.log('Profile: API返回数据:', { infoResponse, profileResponse })

      // 设置用户基本信息（来自User表：手机号等）
      // API返回的是 {code, message, data} 结构，需要提取data
      userInfo.value = { ...userInfo.value, ...(infoResponse.data || infoResponse) }

      // 设置用户资料信息（来自UserProfile表：姓名、性别、生日、邮箱等）
      userProfile.value = { ...userProfile.value, ...(profileResponse.data || profileResponse) }

      console.log('Profile: 最终数据:', { userInfo: userInfo.value, userProfile: userProfile.value })
    } catch (apiError) {
      console.error('Profile: 获取用户信息失败:', apiError.message)
      // 如果没有auth store数据，显示错误
      if (!authStore.user) {
        ElMessage.error('获取用户信息失败：' + apiError.message)
      }
    }
  } catch (error) {
    ElMessage.error('获取用户信息失败：' + error.message)
  } finally {
    loading.value = false
  }
}

// 处理编辑模式
const handleStartEdit = () => {
  isEditing.value = true
}

const handleSaveSuccess = () => {
  isEditing.value = false
  // 刷新查看页面数据
  fetchUserData()
  if (profileViewRef.value) {
    profileViewRef.value.refresh()
  }
}

const handleCancelEdit = () => {
  isEditing.value = false
}

// 返回首页
const goToHome = () => {
  router.push('/')
}

onMounted(() => {
  fetchUserData()
})
</script>

<style scoped>
.profile-page {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: calc(100vh - 120px);
}

.profile-container {
  max-width: 1200px;
  margin: 0 auto;
}

.profile-header {
  margin-bottom: 20px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 20px;
  flex: 1;
}

.header-actions {
  display: flex;
  align-items: center;
}

.avatar {
  flex-shrink: 0;
}

.user-details h2 {
  margin: 0 0 8px 0;
  color: #303133;
}

.phone {
  color: #606266;
  margin: 0 0 8px 0;
}

.profile-content {
  background: white;
  border-radius: 8px;
  overflow: hidden;
}

.profile-tabs {
  --el-tabs-header-height: 50px;
}

:deep(.el-tabs__content) {
  padding: 20px;
}

.home-button {
  font-size: 14px;
  padding: 12px 24px;
  background: linear-gradient(135deg, #409eff 0%, #67c23a 100%);
  border: none;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.home-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4);
  background: linear-gradient(135deg, #67c23a 0%, #409eff 100%);
}

.home-button:active {
  transform: translateY(0);
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }

  .header-actions {
    width: 100%;
    justify-content: center;
  }
}
</style>
