<template>
  <div class="profile-view">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
          <el-button type="primary" @click="handleEdit">
            <el-icon><Edit /></el-icon>
            编辑资料
          </el-button>
        </div>
      </template>

      <div v-loading="loading" class="profile-content">
        <div class="profile-section">
          <div class="avatar-section">
            <el-avatar :size="120" :src="userProfile?.avatar" class="large-avatar">
              <el-icon><User /></el-icon>
            </el-avatar>
            <div class="avatar-info">
              <h3>{{ userProfile?.name || '未设置姓名' }}</h3>
              <p class="phone">{{ userInfo?.phone }}</p>
              <el-tag v-if="userProfile?.gender" :type="genderType" size="large">
                {{ genderText }}
              </el-tag>
            </div>
          </div>

          <el-divider />

          <div class="info-grid">
            <div class="info-item">
              <div class="label">
                <el-icon><User /></el-icon>
                <span>姓名</span>
              </div>
              <div class="value">{{ userProfile?.name || '未设置' }}</div>
            </div>

            <div class="info-item">
              <div class="label">
                <el-icon><Phone /></el-icon>
                <span>手机号</span>
              </div>
              <div class="value">{{ userInfo?.phone || '未设置' }}</div>
            </div>

            <div class="info-item">
              <div class="label">
                <el-icon><Male /></el-icon>
                <span>性别</span>
              </div>
              <div class="value">{{ genderText || '未设置' }}</div>
            </div>

            <div class="info-item">
              <div class="label">
                <el-icon><Calendar /></el-icon>
                <span>生日</span>
              </div>
              <div class="value">{{ formattedBirthday || '未设置' }}</div>
            </div>

            <div class="info-item">
              <div class="label">
                <el-icon><Message /></el-icon>
                <span>邮箱</span>
              </div>
              <div class="value">{{ userProfile?.email || '未设置' }}</div>
            </div>

            <div class="info-item full-width">
              <div class="label">
                <el-icon><Location /></el-icon>
                <span>地址</span>
              </div>
              <div class="value">{{ userProfile?.address || '未设置' }}</div>
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import {
  User, Edit, Phone, Male, Calendar, Message, Location
} from '@element-plus/icons-vue'
import { getAdminProfile } from '@/api/admin.js'
import { useAuthStore } from '@/stores/auth'

// 定义事件
const emit = defineEmits(['edit'])

const authStore = useAuthStore()
const loading = ref(false)
const userInfo = ref(null)
const userProfile = ref(null)

const genderType = computed(() => {
  if (!userProfile.value?.gender) return ''
  return userProfile.value.gender === 'M' ? 'primary' : 'success'
})

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
      console.log('AdminProfileView: 使用auth store中的用户数据:', authStore.user)
    }

    // 获取管理员资料
    try {
      const response = await getAdminProfile()

      if (response.code === 200 && response.data) {
        // 合并数据，优先使用API返回的数据
        userInfo.value = { ...userInfo.value, ...response.data }
        userProfile.value = { ...userProfile.value, ...response.data }
        console.log('AdminProfileView: 获取到API数据:', response.data)
      }
    } catch (apiError) {
      console.warn('AdminProfileView: 获取管理员资料失败，使用基础信息:', apiError.message)
      // 如果API调用失败，但auth store有数据，不要报错
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
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.profile-content {
  min-height: 400px;
}

.profile-section {
  padding: 20px 0;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-bottom: 24px;
}

.large-avatar {
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.avatar-info h3 {
  margin: 0 0 8px 0;
  font-size: 24px;
  color: #303133;
}

.phone {
  color: #606266;
  margin: 0 0 12px 0;
  font-size: 16px;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  margin-top: 24px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-item.full-width {
  grid-column: 1 / -1;
}

.label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #606266;
  font-size: 14px;
}

.value {
  color: #303133;
  font-size: 16px;
  padding: 12px 16px;
  background-color: #f8f9fa;
  border-radius: 6px;
  border: 1px solid #e4e7ed;
  min-height: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .avatar-section {
    flex-direction: column;
    text-align: center;
    gap: 16px;
  }

  .info-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .info-item.full-width {
    grid-column: 1;
  }
}
</style>
