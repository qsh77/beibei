<template>
  <div class="profile-view">
    <div class="bg-white rounded-xl overflow-hidden">
      <div class="p-6">
        <div class="flex flex-col md:flex-row items-center gap-8 mb-8 pb-8 border-b border-gray-100">
          <div class="relative">
            <el-avatar :size="120" :src="userProfile?.avatar" class="bg-primary-50 text-primary-200 border-4 border-white shadow-lg">
              <el-icon><User /></el-icon>
            </el-avatar>
          </div>
          
          <div class="text-center md:text-left flex-1">
            <h3 class="text-2xl font-bold text-gray-900 mb-2">{{ userProfile?.name || '未设置姓名' }}</h3>
            <p class="text-gray-500 mb-3 text-lg font-mono">{{ userInfo?.phone }}</p>
            <span 
              v-if="userProfile?.gender" 
              class="inline-block px-3 py-1 rounded-full text-xs font-medium border"
              :class="userProfile.gender === 'M' ? 'bg-blue-50 text-blue-600 border-blue-100' : 'bg-pink-50 text-pink-600 border-pink-100'"
            >
              {{ genderText }}
            </span>
          </div>
          
          <div class="md:self-start">
             <button @click="handleEdit" class="btn-primary flex items-center gap-2 text-sm px-4 py-2 rounded-lg">
               <el-icon><Edit /></el-icon> 编辑资料
             </button>
          </div>
        </div>

        <div class="grid md:grid-cols-2 gap-8">
          <div class="space-y-6">
            <div class="flex items-start gap-4 p-4 rounded-xl bg-gray-50/50 hover:bg-gray-50 transition-colors">
              <div class="w-10 h-10 rounded-lg bg-white flex items-center justify-center text-gray-400 shadow-sm border border-gray-100">
                <el-icon size="20"><User /></el-icon>
              </div>
              <div>
                <div class="text-xs font-medium text-gray-400 uppercase tracking-wider mb-1">姓名</div>
                <div class="font-medium text-gray-900">{{ userProfile?.name || '未设置' }}</div>
              </div>
            </div>

            <div class="flex items-start gap-4 p-4 rounded-xl bg-gray-50/50 hover:bg-gray-50 transition-colors">
              <div class="w-10 h-10 rounded-lg bg-white flex items-center justify-center text-gray-400 shadow-sm border border-gray-100">
                <el-icon size="20"><Phone /></el-icon>
              </div>
              <div>
                <div class="text-xs font-medium text-gray-400 uppercase tracking-wider mb-1">手机号</div>
                <div class="font-medium text-gray-900 font-mono">{{ userInfo?.phone || '未设置' }}</div>
              </div>
            </div>

            <div class="flex items-start gap-4 p-4 rounded-xl bg-gray-50/50 hover:bg-gray-50 transition-colors">
              <div class="w-10 h-10 rounded-lg bg-white flex items-center justify-center text-gray-400 shadow-sm border border-gray-100">
                <el-icon size="20"><Male /></el-icon>
              </div>
              <div>
                <div class="text-xs font-medium text-gray-400 uppercase tracking-wider mb-1">性别</div>
                <div class="font-medium text-gray-900">{{ genderText || '未设置' }}</div>
              </div>
            </div>
          </div>

          <div class="space-y-6">
            <div class="flex items-start gap-4 p-4 rounded-xl bg-gray-50/50 hover:bg-gray-50 transition-colors">
              <div class="w-10 h-10 rounded-lg bg-white flex items-center justify-center text-gray-400 shadow-sm border border-gray-100">
                <el-icon size="20"><Calendar /></el-icon>
              </div>
              <div>
                <div class="text-xs font-medium text-gray-400 uppercase tracking-wider mb-1">生日</div>
                <div class="font-medium text-gray-900">{{ formattedBirthday || '未设置' }}</div>
              </div>
            </div>

            <div class="flex items-start gap-4 p-4 rounded-xl bg-gray-50/50 hover:bg-gray-50 transition-colors">
              <div class="w-10 h-10 rounded-lg bg-white flex items-center justify-center text-gray-400 shadow-sm border border-gray-100">
                <el-icon size="20"><Message /></el-icon>
              </div>
              <div>
                <div class="text-xs font-medium text-gray-400 uppercase tracking-wider mb-1">邮箱</div>
                <div class="font-medium text-gray-900">{{ userProfile?.email || '未设置' }}</div>
              </div>
            </div>

            <div class="flex items-start gap-4 p-4 rounded-xl bg-gray-50/50 hover:bg-gray-50 transition-colors">
              <div class="w-10 h-10 rounded-lg bg-white flex items-center justify-center text-gray-400 shadow-sm border border-gray-100">
                <el-icon size="20"><Location /></el-icon>
              </div>
              <div>
                <div class="text-xs font-medium text-gray-400 uppercase tracking-wider mb-1">地址</div>
                <div class="font-medium text-gray-900 leading-relaxed">{{ userProfile?.address || '未设置' }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import {
  User, Edit, Phone, Male, Calendar, Message, Location
} from '@element-plus/icons-vue'
import { userApi } from '@/api/user.js'
import { useAuthStore } from '@/stores/auth'

// 定义事件
const emit = defineEmits(['edit'])

const authStore = useAuthStore()
const loading = ref(false)
const userInfo = ref(null)
const userProfile = ref(null)

const genderText = computed(() => {
  if (!userProfile.value?.gender || userProfile.value.gender === 'U') return '未设置'
  const genderMap = { M: '男', F: '女' }
  return genderMap[userProfile.value.gender] || '未设置'
})

const formattedBirthday = computed(() => {
  if (!userProfile.value?.birthday) return ''
  return new Date(userProfile.value.birthday).toLocaleDateString('zh-CN')
})

const fetchUserData = async () => {
  try {
    loading.value = true

    // 优先使用auth store中的用户数据
    if (authStore.user) {
      userInfo.value = authStore.user
      userProfile.value = authStore.user
    }

    // 尝试获取更详细的用户资料信息
    try {
      const [infoResponse, profileResponse] = await Promise.all([
        userApi.getUserInfo(),
        userApi.getUserProfile()
      ])

      userInfo.value = { ...userInfo.value, ...(infoResponse.data || infoResponse) }
      userProfile.value = { ...userProfile.value, ...(profileResponse.data || profileResponse) }
    } catch (apiError) {
      if (!authStore.user) {
        throw apiError
      }
    }
  } catch (error) {
    ElMessage.error('获取用户信息失败：' + error.message)
  } finally {
    loading.value = false
  }
}

const handleEdit = () => {
  emit('edit')
}

// 暴露刷新方法给父组件
const refresh = () => {
  fetchUserData()
}

defineExpose({
  refresh
})

onMounted(() => {
  fetchUserData()
})
</script>

<style scoped>
/* 移除 scoped 样式，依赖 Tailwind CSS */
</style>