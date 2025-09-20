<template>
  <el-dialog
    v-model="visible"
    title="用户详情"
    width="600px"
    @close="handleClose"
  >
    <div v-loading="loading" class="user-detail">
      <template v-if="userDetail">
        <!-- 基本信息 -->
        <div class="detail-section">
          <h3>基本信息</h3>
          <div class="info-grid">
            <div class="info-item">
              <label>头像:</label>
              <el-avatar :size="60" :src="userDetail.avatar" />
            </div>
            <div class="info-item">
              <label>姓名:</label>
              <span>{{ userDetail.name || '未设置' }}</span>
            </div>
            <div class="info-item">
              <label>手机号:</label>
              <span>{{ userDetail.phone }}</span>
            </div>
            <div class="info-item">
              <label>角色:</label>
              <el-tag :type="getRoleTagType(userDetail.role)">
                {{ getRoleText(userDetail.role) }}
              </el-tag>
            </div>
            <div class="info-item">
              <label>状态:</label>
              <el-tag :type="userDetail.status === 1 ? 'success' : 'danger'">
                {{ userDetail.status === 1 ? '正常' : '禁用' }}
              </el-tag>
            </div>
            <div class="info-item">
              <label>性别:</label>
              <span>{{ getGenderText(userDetail.gender) }}</span>
            </div>
            <div class="info-item">
              <label>生日:</label>
              <span>{{ userDetail.birthday || '未设置' }}</span>
            </div>
            <div class="info-item">
              <label>邮箱:</label>
              <span>{{ userDetail.email || '未设置' }}</span>
            </div>
          </div>
        </div>

        <!-- 地址信息 -->
        <div class="detail-section">
          <h3>地址信息</h3>
          <div class="address-info">
            <span>{{ userDetail.address || '未设置地址' }}</span>
          </div>
        </div>

        <!-- 订单统计 -->
        <div class="detail-section">
          <h3>订单统计</h3>
          <div class="stats-grid">
            <div class="stat-card">
              <div class="stat-number">{{ userDetail.totalOrders || 0 }}</div>
              <div class="stat-label">总订单数</div>
            </div>
            <div class="stat-card">
              <div class="stat-number">{{ userDetail.completedOrders || 0 }}</div>
              <div class="stat-label">完成订单</div>
            </div>
            <div class="stat-card">
              <div class="stat-number">{{ getCompletionRate() }}%</div>
              <div class="stat-label">完成率</div>
            </div>
          </div>
        </div>

        <!-- 时间信息 -->
        <div class="detail-section">
          <h3>时间信息</h3>
          <div class="info-grid">
            <div class="info-item">
              <label>注册时间:</label>
              <span>{{ formatDate(userDetail.createdAt) }}</span>
            </div>
            <div class="info-item">
              <label>最后更新:</label>
              <span>{{ formatDate(userDetail.updatedAt) }}</span>
            </div>
          </div>
        </div>
      </template>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
        <el-button type="primary" @click="handleEdit">编辑用户</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserDetail } from '@/api/admin'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  userId: {
    type: Number,
    default: null
  }
})

const emit = defineEmits(['update:visible', 'refresh'])

const loading = ref(false)
const userDetail = ref(null)

const visible = computed({
  get: () => props.visible,
  set: (value) => emit('update:visible', value)
})

// 监听用户ID变化
watch(() => props.userId, (newId) => {
  if (newId && props.visible) {
    loadUserDetail(newId)
  }
})

// 监听对话框显示状态
watch(() => props.visible, (newVisible) => {
  if (newVisible && props.userId) {
    loadUserDetail(props.userId)
  }
})

// 加载用户详情
const loadUserDetail = async (userId) => {
  loading.value = true
  try {
    const response = await getUserDetail(userId)
    userDetail.value = response.data
  } catch (error) {
    ElMessage.error('加载用户详情失败')
    handleClose()
  } finally {
    loading.value = false
  }
}

// 计算完成率
const getCompletionRate = () => {
  if (!userDetail.value || !userDetail.value.totalOrders) {
    return 0
  }
  return Math.round((userDetail.value.completedOrders / userDetail.value.totalOrders) * 100)
}

// 关闭对话框
const handleClose = () => {
  visible.value = false
  userDetail.value = null
}

// 编辑用户
const handleEdit = () => {
  ElMessage.info('编辑功能开发中...')
}

// 工具函数
const formatDate = (dateStr) => {
  return new Date(dateStr).toLocaleString('zh-CN')
}

const getRoleText = (role) => {
  const roleMap = {
    'USER': '用户',
    'WORKER': '阿姨',
    'ADMIN': '管理员'
  }
  return roleMap[role] || '未知'
}

const getRoleTagType = (role) => {
  const typeMap = {
    'USER': '',
    'WORKER': 'success',
    'ADMIN': 'danger'
  }
  return typeMap[role] || ''
}

const getGenderText = (gender) => {
  const genderMap = {
    'M': '男',
    'F': '女',
    'U': '未设置'
  }
  return genderMap[gender] || '未设置'
}
</script>

<style scoped>
.user-detail {
  min-height: 400px;
}

.detail-section {
  margin-bottom: 24px;
}

.detail-section h3 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #262626;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 8px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.info-item {
  display: flex;
  align-items: center;
}

.info-item label {
  font-weight: 500;
  color: #8c8c8c;
  width: 80px;
  margin-right: 12px;
}

.address-info {
  padding: 12px;
  background: #f9f9f9;
  border-radius: 6px;
  color: #262626;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.stat-card {
  background: #f9f9f9;
  padding: 16px;
  border-radius: 8px;
  text-align: center;
}

.stat-number {
  font-size: 24px;
  font-weight: 600;
  color: #1890ff;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #8c8c8c;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

@media (max-width: 600px) {
  .info-grid,
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>