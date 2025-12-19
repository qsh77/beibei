<template>
  <div class="min-h-screen bg-slate-50 pb-20">
    <!-- 顶部导航 -->
    <header class="bg-white border-b border-gray-100 sticky top-0 z-40">
      <div class="container-responsive h-16 flex items-center justify-between">
        <div class="flex items-center gap-4">
          <button @click="goToHome" class="p-2 hover:bg-gray-50 rounded-lg text-gray-500 transition-colors">
            <el-icon size="20"><Back /></el-icon>
          </button>
          <h1 class="text-xl font-bold text-gray-900">个人中心</h1>
        </div>
        <button @click="goToHome" class="text-sm text-gray-500 hover:text-primary-600 font-medium">
          返回首页
        </button>
      </div>
    </header>

    <div class="container-responsive max-w-5xl py-8">
      <!-- 个人信息头部卡片 -->
      <div class="bg-white rounded-xl border border-gray-100 p-6 flex flex-col md:flex-row items-center gap-6 mb-8 shadow-sm">
        <div class="relative">
          <el-avatar :size="80" :src="userProfile?.avatar" class="bg-primary-50 text-primary-300 text-3xl font-bold border-4 border-white shadow-md">
            <el-icon><User /></el-icon>
          </el-avatar>
        </div>
        
        <div class="text-center md:text-left flex-1">
          <h2 class="text-2xl font-bold text-gray-900 mb-1">{{ userProfile?.name || '未设置姓名' }}</h2>
          <p class="text-gray-500 mb-2">{{ userInfo?.phone }}</p>
          <div class="flex items-center justify-center md:justify-start gap-2">
            <span 
              v-if="userProfile?.gender" 
              class="px-2 py-0.5 rounded text-xs font-medium border"
              :class="userProfile.gender === 'M' ? 'bg-blue-50 text-blue-600 border-blue-100' : 'bg-pink-50 text-pink-600 border-pink-100'"
            >
              {{ genderText }}
            </span>
          </div>
        </div>
      </div>

      <!-- 内容 Tabs -->
      <div class="bg-white rounded-xl border border-gray-100 shadow-sm overflow-hidden min-h-[500px]">
        <el-tabs v-model="activeTab" class="profile-tabs">
          <el-tab-pane label="个人信息" name="profile">
            <div class="p-6">
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
            </div>
          </el-tab-pane>

          <el-tab-pane label="地址管理" name="address">
            <div class="p-6">
              <AddressManage />
            </div>
          </el-tab-pane>

          <el-tab-pane label="修改密码" name="password">
            <div class="p-6 max-w-lg mx-auto">
              <PasswordChange />
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Back } from '@element-plus/icons-vue'
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

      const userInfoData = infoResponse.data || infoResponse
      const userProfileData = profileResponse.data || profileResponse

      userInfo.value = { ...userInfo.value, ...userInfoData }
      userProfile.value = { ...userProfile.value, ...userProfileData }

    } catch (apiError) {
      console.error('Profile: 获取用户信息失败:', apiError.message)
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
/* Tabs customization */
:deep(.el-tabs__header) {
  margin: 0;
  border-bottom: 1px solid #f3f4f6;
}

:deep(.el-tabs__nav-wrap) {
  padding: 0 24px;
}

:deep(.el-tabs__item) {
  height: 60px;
  font-size: 15px;
  color: #6b7280;
}

:deep(.el-tabs__item.is-active) {
  color: #059669; /* primary-600 */
  font-weight: 600;
}

:deep(.el-tabs__active-bar) {
  background-color: #059669;
  height: 3px;
  border-radius: 3px;
}
</style>
