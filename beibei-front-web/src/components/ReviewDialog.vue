<template>
  <el-dialog
    v-model="dialogVisible"
    title="订单评价"
    width="600px"
    :before-close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="80px"
      label-position="top"
    >
      <el-form-item label="订单信息">
        <div class="order-info">
          <div class="info-row">
            <span class="label">订单编号：</span>
            <span class="value">{{ order?.orderNo }}</span>
          </div>
          <div class="info-row">
            <span class="label">服务项目：</span>
            <span class="value">{{ order?.serviceName }}</span>
          </div>
          <div class="info-row">
            <span class="label">订单金额：</span>
            <span class="value amount">¥{{ formatAmount(order?.amount) }}</span>
          </div>
        </div>
      </el-form-item>

      <!-- 阿姨信息 -->
      <el-form-item v-if="order?.workerName" label="服务阿姨">
        <div class="worker-info-card">
          <div class="worker-avatar">
            <el-avatar :size="48" :src="order?.workerAvatar">
              <el-icon :size="24"><UserFilled /></el-icon>
            </el-avatar>
          </div>
          <div class="worker-details">
            <div class="worker-name">{{ order?.workerName }}</div>
            <div class="worker-meta">
              <el-rate
                v-if="order?.workerLevel"
                :model-value="order.workerLevel"
                disabled
                :max="5"
                size="small"
              />
              <span v-if="order?.workerYears" class="worker-years">{{ order.workerYears }}年经验</span>
            </div>
          </div>
        </div>
        <div class="rating-hint">
          <el-icon><Star /></el-icon>
          <span>您即将为此阿姨评分，您的评价将帮助其他用户选择服务</span>
        </div>
      </el-form-item>

      <el-form-item label="服务评分" prop="rating" required>
        <el-rate
          v-model="form.rating"
          :texts="ratingTexts"
          show-text
          size="large"
          :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
        />
      </el-form-item>

      <el-form-item label="评价内容" prop="content">
        <el-input
          v-model="form.content"
          type="textarea"
          :rows="5"
          placeholder="分享您的服务体验，帮助其他用户更好地选择（选填）"
          maxlength="500"
          show-word-limit
        />
      </el-form-item>

      <el-form-item label="上传图片">
        <div class="upload-tips">
          <el-icon><InfoFilled /></el-icon>
          <span>最多上传3张图片，支持JPG、PNG格式</span>
        </div>
        <div class="image-list">
          <div
            v-for="(img, index) in form.imgs"
            :key="index"
            class="image-item"
          >
            <el-image
              :src="resolveImageUrl(img)"
              fit="cover"
              class="uploaded-image"
              :preview-src-list="previewImages"
              :initial-index="index"
            />
            <div class="image-actions">
              <el-button
                type="danger"
                size="small"
                circle
                @click="removeImage(index)"
              >
                <el-icon><Close /></el-icon>
              </el-button>
            </div>
          </div>
          <el-upload
            v-if="form.imgs.length < 3"
            :action="uploadUrl"
            :headers="uploadHeaders"
            name="file"
            :show-file-list="false"
            :before-upload="beforeUpload"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            accept="image/jpeg,image/png,image/jpg"
            class="image-uploader"
          >
            <div class="upload-trigger">
              <el-icon><Plus /></el-icon>
              <span>上传图片</span>
            </div>
          </el-upload>
        </div>
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          提交评价
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Close, InfoFilled, UserFilled, Star } from '@element-plus/icons-vue'
import { createReview } from '@/api/reviews.js'
import { useAuthStore } from '@/stores/auth'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  order: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

const authStore = useAuthStore()
const dialogVisible = ref(props.modelValue)
const formRef = ref(null)
const submitting = ref(false)

const form = reactive({
  orderId: null,
  rating: 5,
  content: '',
  imgs: []
})

const ratingTexts = ['非常差', '差', '一般', '满意', '非常满意']

const rules = {
  rating: [
    { required: true, message: '请选择评分', trigger: 'change' }
  ]
}

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
const uploadUrl = `${API_BASE_URL}/api/files/upload`
const uploadHeaders = computed(() => {
  const token = authStore.token || localStorage.getItem('beibei-web-token') || ''
  return token ? { Authorization: `Bearer ${token}` } : {}
})

watch(() => props.modelValue, (val) => {
  dialogVisible.value = val
  if (val && props.order) {
    form.orderId = props.order.id
    form.rating = 5
    form.content = ''
    form.imgs = []
  }
})

watch(dialogVisible, (val) => {
  emit('update:modelValue', val)
})

const formatAmount = (amount) => {
  if (amount === undefined || amount === null) return '0.00'
  return Number(amount).toFixed(2)
}

const beforeUpload = (file) => {
  const isImage = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/jpg'
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传 JPG/PNG 格式的图片!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!')
    return false
  }
  return true
}

const resolveImageUrl = (url) => {
  if (!url) return url
  if (/^https?:\/\//i.test(url)) return url
  if (url.startsWith('/')) return `${API_BASE_URL}${url}`
  return url
}

const previewImages = computed(() => form.imgs.map(resolveImageUrl))

const handleUploadSuccess = (response) => {
  let payload = response
  if (typeof response === 'string') {
    try {
      payload = JSON.parse(response)
    } catch {
      payload = null
    }
  }

  if (payload && payload.code === 200 && payload.data) {
    const imageUrl = payload.data.url || payload.data
    form.imgs.push(imageUrl)
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(payload?.message || '图片上传失败')
  }
}

const handleUploadError = () => {
  ElMessage.error('图片上传失败，请重试')
}

const removeImage = (index) => {
  form.imgs.splice(index, 1)
}

const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()

    submitting.value = true

    const reviewData = {
      orderId: form.orderId,
      rating: form.rating,
      content: form.content || undefined,
      imgs: form.imgs.length > 0 ? form.imgs : undefined
    }

    await createReview(reviewData)

    ElMessage.success('评价提交成功')
    emit('success')
    handleClose()
  } catch (error) {
    if (error && typeof error === 'object' && 'message' in error) {
      ElMessage.error(error.message || '提交评价失败')
    }
  } finally {
    submitting.value = false
  }
}

const handleClose = () => {
  dialogVisible.value = false
  formRef.value?.resetFields()
  form.imgs = []
}
</script>

<style scoped>
.order-info {
  background: #f9fafb;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #e5e7eb;
}

.info-row {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.info-row:last-child {
  margin-bottom: 0;
}

.info-row .label {
  font-size: 14px;
  color: #6b7280;
  font-weight: 500;
}

.info-row .value {
  font-size: 14px;
  color: #1f2937;
  flex: 1;
}

.info-row .value.amount {
  color: #2563eb;
  font-weight: 600;
}

.upload-tips {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #6b7280;
  font-size: 13px;
  margin-bottom: 12px;
}

.image-list {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.image-item {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e5e7eb;
}

.uploaded-image {
  width: 100%;
  height: 100%;
}

.image-actions {
  position: absolute;
  top: 4px;
  right: 4px;
}

.image-uploader {
  width: 120px;
  height: 120px;
}

.upload-trigger {
  width: 120px;
  height: 120px;
  border: 2px dashed #d1d5db;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
  color: #9ca3af;
  font-size: 14px;
  gap: 8px;
}

.upload-trigger:hover {
  border-color: #2563eb;
  color: #2563eb;
}

.upload-trigger .el-icon {
  font-size: 28px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

:deep(.el-rate) {
  height: 40px;
}

:deep(.el-rate__text) {
  font-size: 16px;
  font-weight: 500;
  color: #1f2937;
}

/* 阿姨信息卡片 */
.worker-info-card {
  display: flex;
  align-items: center;
  gap: 16px;
  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
  border-radius: 12px;
  padding: 16px;
  border: 1px solid #bbf7d0;
}

.worker-details {
  flex: 1;
}

.worker-name {
  font-size: 16px;
  font-weight: 600;
  color: #166534;
  margin-bottom: 4px;
}

.worker-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.worker-years {
  font-size: 12px;
  color: #16a34a;
}

.rating-hint {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 12px;
  padding: 10px 12px;
  background: #fef3c7;
  border-radius: 8px;
  font-size: 13px;
  color: #92400e;
}

.rating-hint .el-icon {
  color: #f59e0b;
}
</style>
