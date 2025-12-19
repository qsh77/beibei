<template>
  <el-dialog
    v-model="visible"
    title="角色变更"
    width="500px"
    @close="handleClose"
  >
    <div class="role-change">
      <div class="user-info">
        <el-avatar :size="50" :src="user?.avatar" />
        <div class="user-details">
          <div class="user-name">{{ user?.name || '未设置' }}</div>
          <div class="user-phone">{{ user?.phone }}</div>
        </div>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        style="margin-top: 24px;"
      >
        <el-form-item label="当前角色:" prop="currentRole">
          <el-tag :type="getRoleTagType(user?.role)">
            {{ getRoleText(user?.role) }}
          </el-tag>
        </el-form-item>

        <el-form-item label="新角色:" prop="newRole">
          <el-select
            v-model="form.newRole"
            placeholder="请选择新角色"
            style="width: 100%"
          >
            <el-option label="普通用户" value="USER" />
            <el-option label="阿姨" value="WORKER" />
          </el-select>
        </el-form-item>

        <el-form-item label="变更原因:" prop="reason">
          <el-input
            v-model="form.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入角色变更原因..."
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>

      <div class="role-description">
        <el-alert
          title="角色说明"
          type="info"
          :closable="false"
        >
          <ul>
            <li><strong>普通用户:</strong> 可以浏览服务、下单、管理个人信息</li>
            <li><strong>阿姨:</strong> 除用户权限外，还可以接单、管理档期、查看工作统计</li>
          </ul>
        </el-alert>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button
          type="primary"
          :loading="loading"
          @click="handleSubmit"
        >
          确认变更
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch, computed, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { updateUserRole } from '@/api/admin'

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

const emit = defineEmits(['update:visible', 'success'])

const formRef = ref()
const loading = ref(false)

const form = reactive({
  newRole: '',
  reason: ''
})

const rules = {
  newRole: [
    { required: true, message: '请选择新角色', trigger: 'change' }
  ],
  reason: [
    { required: true, message: '请输入变更原因', trigger: 'blur' },
    { min: 10, message: '变更原因至少10个字符', trigger: 'blur' }
  ]
}

const visible = computed({
  get: () => props.visible,
  set: (value) => emit('update:visible', value)
})

// 监听用户变化，重置表单
watch(() => props.user, (newUser) => {
  if (newUser) {
    form.newRole = ''
    form.reason = ''
    if (formRef.value) {
      formRef.value.clearValidate()
    }
  }
})

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()

    // 确认变更
    await ElMessageBox.confirm(
      `确定要将用户 ${props.user.phone} 的角色从 ${getRoleText(props.user.role)} 变更为 ${getRoleText(form.newRole)} 吗？`,
      '确认角色变更',
      {
        type: 'warning',
        confirmButtonText: '确认变更',
        cancelButtonText: '取消'
      }
    )

    loading.value = true

    await updateUserRole(props.user.id, form.newRole)

    ElMessage.success('角色变更成功')
    emit('success')
    handleClose()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '角色变更失败')
    }
  } finally {
    loading.value = false
  }
}

// 关闭对话框
const handleClose = () => {
  visible.value = false
  form.newRole = ''
  form.reason = ''
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

// 工具函数
const getRoleText = (role) => {
  const roleMap = {
    'USER': '普通用户',
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
</script>

<style scoped>
.role-change {
  padding: 8px 0;
}

.user-info {
  display: flex;
  align-items: center;
  padding: 16px;
  background: #f9f9f9;
  border-radius: 8px;
}

.user-details {
  margin-left: 12px;
}

.user-name {
  font-weight: 500;
  font-size: 16px;
  color: #262626;
  margin-bottom: 4px;
}

.user-phone {
  font-size: 14px;
  color: #8c8c8c;
}

.role-description {
  margin-top: 16px;
}

.role-description ul {
  margin: 8px 0 0 0;
  padding-left: 16px;
}

.role-description li {
  margin-bottom: 8px;
  color: #666;
  line-height: 1.5;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
</style>