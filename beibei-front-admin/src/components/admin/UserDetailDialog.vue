<template>
  <el-dialog
    v-model="dialogVisible"
    title="用户详情"
    width="600px"
    :close-on-click-modal="false"
    class="user-detail-dialog"
    @close="handleClose"
  >
    <div v-if="user" class="user-detail-content">
      <!-- 用户基本信息 -->
      <div class="user-header">
        <el-avatar :size="80" :src="getAvatarUrl(user.avatar)" class="user-avatar" />
        <div class="user-main-info">
          <h3 class="user-name">{{ user.name || '未设置昵称' }}</h3>
          <p class="user-phone">{{ user.phone }}</p>
          <div class="user-tags">
            <el-tag :type="getRoleTagType(user.role)" size="small">
              {{ getRoleText(user.role) }}
            </el-tag>
            <el-tag :type="user.status === 1 ? 'success' : 'danger'" size="small">
              {{ user.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </div>
        </div>
      </div>

      <!-- 详细信息列表 -->
      <el-divider />
      
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户ID">
          {{ user.id }}
        </el-descriptions-item>
        <el-descriptions-item label="订单数量">
          <span class="order-count">{{ user.totalOrders || 0 }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="角色">
          <el-tag :type="getRoleTagType(user.role)" size="small">
            {{ getRoleText(user.role) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="账号状态">
          <el-tag :type="user.status === 1 ? 'success' : 'danger'" size="small">
            {{ user.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间" :span="2">
          {{ formatDate(user.createdAt) }}
        </el-descriptions-item>
        <el-descriptions-item label="地址" :span="2" v-if="user.address">
          {{ user.address }}
        </el-descriptions-item>
      </el-descriptions>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
        <el-button
          v-if="user?.status === 1"
          type="danger"
          @click="handleStatusChange(0)"
        >
          禁用用户
        </el-button>
        <el-button
          v-else
          type="success"
          @click="handleStatusChange(1)"
        >
          启用用户
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  user: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:visible', 'status-change'])

const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

const handleClose = () => {
  emit('update:visible', false)
}

const handleStatusChange = (status) => {
  emit('status-change', { user: props.user, status })
}

// 工具函数
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
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

const getAvatarUrl = (avatar) => {
  if (!avatar) return ''
  if (avatar.startsWith('http://') || avatar.startsWith('https://')) {
    return avatar
  }
  return `http://localhost:8080${avatar}`
}
</script>

<style scoped>
.user-detail-content {
  padding: 10px 0;
}

.user-header {
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 16px 0;
}

.user-avatar {
  border: 3px solid #f1f5f9;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.user-main-info {
  flex: 1;
}

.user-name {
  margin: 0 0 8px 0;
  font-size: 20px;
  font-weight: 600;
  color: #262626;
}

.user-phone {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #8c8c8c;
}

.user-tags {
  display: flex;
  gap: 8px;
}

.order-count {
  font-weight: 600;
  color: #1890ff;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

:deep(.el-descriptions__label) {
  width: 100px;
  font-weight: 500;
}
</style>
