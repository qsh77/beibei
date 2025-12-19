<template>
  <div class="max-w-2xl mx-auto py-8">
    <div class="bg-white rounded-xl overflow-hidden">
      <div class="p-8">
        <h3 class="text-lg font-bold text-gray-900 mb-6 flex items-center gap-2">
          <el-icon class="text-primary-500"><Lock /></el-icon>
          修改登录密码
        </h3>

        <el-form
          ref="formRef"
          :model="formData"
          :rules="formRules"
          label-position="top"
          class="space-y-6"
        >
          <el-form-item label="当前密码" prop="oldPassword">
            <el-input
              v-model="formData.oldPassword"
              type="password"
              placeholder="请输入当前使用的密码"
              show-password
              class="w-full"
            />
          </el-form-item>

          <el-form-item label="新密码" prop="newPassword">
            <el-input
              v-model="formData.newPassword"
              type="password"
              placeholder="请输入新密码（至少6位）"
              show-password
              class="w-full"
            />
          </el-form-item>

          <el-form-item label="确认新密码" prop="confirmPassword">
            <el-input
              v-model="formData.confirmPassword"
              type="password"
              placeholder="请再次输入新密码"
              show-password
              class="w-full"
            />
          </el-form-item>

          <div class="pt-4 flex items-center gap-4">
            <button
              type="button"
              @click="handleSubmit"
              class="btn-primary w-full md:w-auto px-8 py-3 flex items-center justify-center gap-2"
              :disabled="loading"
            >
              <span v-if="loading">提交中...</span>
              <span v-else>确认修改</span>
            </button>
            
            <button
              type="button"
              @click="handleReset"
              class="w-full md:w-auto px-8 py-3 rounded-lg border border-gray-200 text-gray-600 hover:bg-gray-50 hover:text-gray-900 transition-colors"
            >
              重置
            </button>
          </div>
        </el-form>
      </div>
      
      <div class="bg-slate-50 p-6 border-t border-gray-100">
        <div class="flex items-start gap-3">
          <el-icon class="text-primary-500 mt-1"><InfoFilled /></el-icon>
          <div>
            <h4 class="text-sm font-bold text-gray-900 mb-2">安全提示</h4>
            <ul class="text-sm text-gray-500 space-y-1 list-disc list-inside">
              <li>定期更换密码可以有效保护账户安全</li>
              <li>建议使用字母、数字和符号的组合，提高密码强度</li>
              <li>请勿使用生日、手机号等容易被猜到的信息作为密码</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { InfoFilled, Lock } from '@element-plus/icons-vue'
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
/* 移除 scoped 样式，依赖 Tailwind CSS */
</style>