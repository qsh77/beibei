<template>
  <div class="password-change">
    <el-card shadow="never">
      <template #header>
        <span>修改密码</span>
      </template>

      <div class="password-form-container">
        <el-form
          ref="formRef"
          :model="formData"
          :rules="formRules"
          label-width="120px"
          class="password-form"
        >
          <el-form-item label="原密码" prop="oldPassword">
            <el-input
              v-model="formData.oldPassword"
              type="password"
              placeholder="请输入原密码"
              show-password
              clearable
            />
          </el-form-item>

          <el-form-item label="新密码" prop="newPassword">
            <el-input
              v-model="formData.newPassword"
              type="password"
              placeholder="请输入新密码"
              show-password
              clearable
            />
            <div class="password-tips">
              <el-text size="small" type="info">
                密码长度不少于6位，建议包含字母、数字和符号
              </el-text>
            </div>
          </el-form-item>

          <el-form-item label="确认新密码" prop="confirmPassword">
            <el-input
              v-model="formData.confirmPassword"
              type="password"
              placeholder="请再次输入新密码"
              show-password
              clearable
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              :loading="loading"
              @click="handleSubmit"
            >
              修改密码
            </el-button>
            <el-button @click="handleReset">
              重置
            </el-button>
          </el-form-item>
        </el-form>

        <!-- 安全提示 -->
        <el-card class="security-tips" shadow="never">
          <template #header>
            <div class="tips-header">
              <el-icon class="tips-icon"><InfoFilled /></el-icon>
              <span>安全提示</span>
            </div>
          </template>

          <ul class="tips-list">
            <li>定期更换密码可以提高账户安全性</li>
            <li>不要使用生日、手机号等易猜测的密码</li>
            <li>建议使用字母、数字和符号的组合</li>
            <li>不要在公共场所输入密码</li>
            <li>如果忘记密码，请通过手机号重置</li>
          </ul>
        </el-card>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { InfoFilled } from '@element-plus/icons-vue'
import { userApi } from '@/api/user.js'

const formRef = ref()
const loading = ref(false)

const formData = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 自定义验证器
const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入新密码'))
  } else if (value !== formData.newPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const validateNewPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入新密码'))
  } else if (value.length < 6) {
    callback(new Error('密码长度不能少于6位'))
  } else if (value === formData.oldPassword) {
    callback(new Error('新密码不能与原密码相同'))
  } else {
    // 如果确认密码已填写，需要重新验证确认密码
    if (formData.confirmPassword !== '') {
      formRef.value.validateField('confirmPassword')
    }
    callback()
  }
}

const formRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { validator: validateNewPassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()

    // 二次确认
    await ElMessageBox.confirm(
      '确定要修改密码吗？修改后需要重新登录。',
      '确认修改',
      {
        confirmButtonText: '确定修改',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    loading.value = true
    const response = await userApi.changePassword(formData)

    if (response.code === 200) {
      ElMessage.success('密码修改成功，请重新登录')

      // 清空表单
      handleReset()

      // 这里可以添加跳转到登录页面的逻辑
      setTimeout(() => {
        // 示例：跳转到登录页面
        // router.push('/login')
        console.log('应该跳转到登录页面')
      }, 2000)

    } else {
      ElMessage.error(response.message || '密码修改失败')
    }
  } catch (error) {
    if (error === 'cancel') {
      return
    }
    if (error.errors) {
      return
    }
    ElMessage.error('密码修改失败：' + error.message)
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  Object.assign(formData, {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  })

  if (formRef.value) {
    formRef.value.resetFields()
  }
}
</script>

<style scoped>
.password-change {
  max-width: 600px;
}

.password-form-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.password-form {
  padding: 20px 0;
}

.password-tips {
  margin-top: 8px;
}

.security-tips {
  background-color: #f8f9fa;
  border: 1px solid #e9ecef;
}

.tips-header {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #606266;
}

.tips-icon {
  color: #409eff;
}

.tips-list {
  margin: 0;
  padding-left: 20px;
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
}

.tips-list li {
  margin-bottom: 8px;
}

.tips-list li:last-child {
  margin-bottom: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .password-change {
    max-width: 100%;
  }

  :deep(.el-form-item__label) {
    width: 100px !important;
  }
}
</style>