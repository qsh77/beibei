<template>
  <el-dialog
    v-model="visible"
    title="编辑阿姨信息"
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
      <!-- 基本信息（只读） -->
      <div class="form-section">
        <h4 class="section-title">基本信息（只读）</h4>

        <el-form-item label="姓名">
          <el-input :model-value="workerInfo?.name || '未设置'" disabled />
        </el-form-item>

        <el-form-item label="手机号">
          <el-input :model-value="workerInfo?.phone" disabled />
        </el-form-item>

        <el-form-item label="性别">
          <el-input :model-value="getGenderText(workerInfo?.gender)" disabled />
        </el-form-item>

        <el-form-item label="等级">
          <el-rate
            :model-value="form.level"
            disabled
            show-text
            :texts="['一星', '二星', '三星', '四星', '五星']"
          />
        </el-form-item>
        
        <el-form-item label="评分">
          <div class="score-display">
            <el-rate
              :model-value="form.score"
              disabled
              allow-half
              show-score
              score-template="{value}分"
            />
          </div>
        </el-form-item>
      </div>

      <!-- 专业信息（可编辑） -->
      <div class="form-section">
        <h4 class="section-title">专业信息</h4>

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

        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
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
          保存更改
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { updateWorker } from '@/api/admin'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  workerInfo: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

const visible = ref(props.modelValue)
const loading = ref(false)
const formRef = ref()

// 表单数据
const form = reactive({
  level: 1,
  years: 0,
  bio: '',
  score: 5.0,
  status: 1
})

// 表单验证规则
const rules = {
  years: [
    { required: true, message: '请输入工作年限', trigger: 'blur' },
    { type: 'number', min: 0, max: 50, message: '工作年限范围0-50年', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ],
  bio: [
    { max: 500, message: '个人简介不能超过500字符', trigger: 'blur' }
  ]
}

// 监听 modelValue 变化
watch(() => props.modelValue, (newVal) => {
  visible.value = newVal
  if (newVal && props.workerInfo) {
    initForm()
  }
})

// 监听 visible 变化
watch(visible, (newVal) => {
  emit('update:modelValue', newVal)
})

// 监听 workerInfo 变化
watch(() => props.workerInfo, (newVal) => {
  if (newVal && visible.value) {
    initForm()
  }
})

// 初始化表单
const initForm = () => {
  if (!props.workerInfo) return

  Object.assign(form, {
    level: props.workerInfo.level || 1,
    years: props.workerInfo.years || 0,
    bio: props.workerInfo.bio || '',
    score: props.workerInfo.score || 5.0,
    status: props.workerInfo.status !== undefined ? props.workerInfo.status : 1
  })
}

// 获取性别文本
const getGenderText = (gender) => {
  const genderMap = {
    'M': '男',
    'F': '女',
    'U': '未设置'
  }
  return genderMap[gender] || '未知'
}

// 确认保存
const handleConfirm = async () => {
  if (!formRef.value) return

  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  if (!props.workerInfo?.id) {
    ElMessage.error('阿姨信息不完整')
    return
  }

  loading.value = true
  try {
    await updateWorker(props.workerInfo.id, form)

    ElMessage.success('阿姨信息更新成功')
    emit('success')
    handleClose()
  } catch (error) {
    ElMessage.error('更新阿姨信息失败：' + (error.message || '请求失败'))
  } finally {
    loading.value = false
  }
}

// 关闭对话框
const handleClose = () => {
  visible.value = false
  if (formRef.value) {
    formRef.value.resetFields()
  }
}
</script>

<style scoped>
.form-section {
  margin-bottom: 24px;
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

:deep(.el-form-item__content) {
  align-items: center;
}
</style>