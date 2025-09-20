<template>
  <el-dialog
    v-model="visible"
    title="添加阿姨"
    width="650px"
    :before-close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="100px"
      @submit.prevent
    >
      <!-- 基本信息 -->
      <div class="form-section">
        <h4 class="section-title">基本信息</h4>

        <el-form-item label="手机号" prop="phone">
          <el-input
            v-model="form.phone"
            placeholder="请输入阿姨手机号"
            maxlength="11"
          />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入登录密码"
            maxlength="20"
            show-password
          />
        </el-form-item>

        <el-form-item label="姓名" prop="name">
          <el-input
            v-model="form.name"
            placeholder="请输入阿姨姓名"
            maxlength="50"
          />
        </el-form-item>

        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio label="F">女</el-radio>
            <el-radio label="M">男</el-radio>
            <el-radio label="U">未设置</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="生日">
          <el-date-picker
            v-model="form.birthday"
            type="date"
            placeholder="选择生日"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="邮箱">
          <el-input
            v-model="form.email"
            placeholder="请输入邮箱地址（可选）"
            type="email"
          />
        </el-form-item>

        <el-form-item label="地址">
          <el-input
            v-model="form.address"
            placeholder="请输入家庭住址（可选）"
            maxlength="200"
          />
        </el-form-item>
      </div>

      <!-- 专业信息 -->
      <div class="form-section">
        <h4 class="section-title">专业信息</h4>

        <el-form-item label="等级" prop="level">
          <el-rate
            v-model="form.level"
            show-text
            :texts="['一星', '二星', '三星', '四星', '五星']"
          />
        </el-form-item>

        <el-form-item label="工作年限" prop="years">
          <el-input-number
            v-model="form.years"
            :min="0"
            :max="50"
            placeholder="请输入工作年限"
            style="width: 100%"
          />
          <span class="form-hint">年</span>
        </el-form-item>

        <el-form-item label="个人简介" prop="bio">
          <el-input
            v-model="form.bio"
            type="textarea"
            :rows="4"
            placeholder="请输入阿姨的个人简介、擅长服务等"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="初始评分">
          <el-input-number
            v-model="form.score"
            :min="0"
            :max="5"
            :step="0.1"
            :precision="2"
            style="width: 100%"
          />
          <span class="form-hint">分（默认5.0分）</span>
        </el-form-item>
      </div>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button
          type="primary"
          :loading="loading"
          @click="handleConfirm"
        >
          确认添加
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { createWorker } from '@/api/admin'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

const visible = ref(props.modelValue)
const loading = ref(false)
const formRef = ref()

// 表单数据
const form = reactive({
  phone: '',
  password: '',
  name: '',
  gender: 'F',
  birthday: null,
  email: '',
  address: '',
  level: 1,
  years: 0,
  bio: '',
  score: 5.0
})

// 表单验证规则
const rules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度必须在6-20位之间', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { max: 50, message: '姓名不能超过50个字符', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  level: [
    { required: true, message: '请选择等级', trigger: 'change' }
  ],
  years: [
    { required: true, message: '请输入工作年限', trigger: 'blur' },
    { type: 'number', min: 0, max: 50, message: '工作年限范围0-50年', trigger: 'blur' }
  ],
  email: [
    {
      validator: (rule, value, callback) => {
        if (!value) {
          callback() // 空值时不验证
        } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
          callback(new Error('请输入正确的邮箱地址'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 监听 modelValue 变化
watch(() => props.modelValue, (newVal) => {
  visible.value = newVal
  if (newVal) {
    resetForm()
  }
})

// 监听 visible 变化
watch(visible, (newVal) => {
  emit('update:modelValue', newVal)
})

// 重置表单
const resetForm = () => {
  // 先清除验证状态
  if (formRef.value) {
    formRef.value.clearValidate()
  }

  // 再设置默认值
  Object.assign(form, {
    phone: '',
    password: '',
    name: '',
    gender: 'F',
    birthday: null,
    email: '',
    address: '',
    level: 1,
    years: 0,
    bio: '',
    score: 5.0
  })
}


// 获取错误提示信息
const getErrorMessage = (error) => {
  // 检查是否是网络错误
  if (!error.response) {
    return '网络连接失败，请检查网络后重试'
  }

  // 检查HTTP状态码
  const status = error.response.status
  const message = error.response.data?.message || error.message

  if (status === 400) {
    if (message.includes('手机号') || message.includes('已被注册')) {
      return '手机号已被注册，请更换其他手机号'
    }
    if (message.includes('邮箱') || message.includes('邮箱已存在')) {
      return '邮箱地址已被使用，请更换其他邮箱'
    }
    return '数据验证失败：' + message
  }

  if (status === 403) {
    return '没有操作权限，请联系管理员'
  }

  if (status === 500) {
    return '服务器内部错误，请稍后重试'
  }

  return '添加失败：' + (message || '未知错误')
}

// 确认添加
const handleConfirm = async () => {
  if (!formRef.value) return

  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await createWorker(form)

    ElMessage.success('阿姨添加成功')
    emit('success')
    handleClose()
  } catch (error) {
    ElMessage.error(getErrorMessage(error))
  } finally {
    loading.value = false
  }
}

// 关闭对话框
const handleClose = () => {
  visible.value = false
}
</script>

<style scoped>
.form-section {
  margin-bottom: 24px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.form-section:last-child {
  margin-bottom: 0;
}

.section-title {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  border-bottom: 2px solid #409eff;
  padding-bottom: 8px;
}

.form-hint {
  margin-left: 8px;
  color: #8c8c8c;
  font-size: 12px;
}

.dialog-footer {
  text-align: right;
}

:deep(.el-rate) {
  align-items: center;
}

:deep(.el-rate__text) {
  color: #409eff;
  font-weight: 500;
}
</style>