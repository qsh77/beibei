<template>
  <el-dialog
    :model-value="visible"
    :title="service ? '编辑服务' : '添加服务'"
    width="600px"
    @update:model-value="handleClose"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="100px"
    >
      <el-form-item label="服务名称" prop="name">
        <el-input
          v-model="formData.name"
          placeholder="请输入服务名称"
          maxlength="100"
          show-word-limit
        />
      </el-form-item>

      <el-form-item label="服务描述" prop="description">
        <el-input
          v-model="formData.description"
          type="textarea"
          :rows="3"
          placeholder="请输入服务描述"
          maxlength="500"
          show-word-limit
        />
      </el-form-item>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="基础价格" prop="basePrice">
            <el-input-number
              v-model="formData.basePrice"
              :min="0"
              :precision="2"
              placeholder="请输入基础价格"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="计价单位" prop="unit">
            <el-select
              v-model="formData.unit"
              placeholder="请选择计价单位"
              style="width: 100%"
            >
              <el-option label="小时" value="小时" />
              <el-option label="天" value="天" />
              <el-option label="单" value="单" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="热门服务">
            <el-switch
              v-model="formData.hot"
              :active-value="1"
              :inactive-value="0"
              active-text="是"
              inactive-text="否"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12" v-if="service">
          <el-form-item label="服务状态">
            <el-switch
              v-model="formData.status"
              :active-value="1"
              :inactive-value="0"
              active-text="启用"
              inactive-text="禁用"
            />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" :loading="loading" @click="handleSubmit">
          {{ service ? '更新' : '创建' }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { createService, updateService } from '@/api/admin'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  service: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:visible', 'success'])

const formRef = ref()
const loading = ref(false)

const formData = reactive({
  name: '',
  description: '',
  basePrice: null,
  unit: '',
  hot: 0,
  status: 1
})

const rules = {
  name: [
    { required: true, message: '请输入服务名称', trigger: 'blur' },
    { min: 2, max: 100, message: '服务名称长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  basePrice: [
    { required: true, message: '请输入基础价格', trigger: 'blur' },
    { type: 'number', min: 0, message: '价格不能小于0', trigger: 'blur' }
  ],
  unit: [
    { required: true, message: '请选择计价单位', trigger: 'change' }
  ]
}

// 监听对话框显示状态
watch(() => props.visible, (newVal) => {
  if (newVal) {
    resetForm()
    if (props.service) {
      nextTick(() => {
        Object.assign(formData, {
          name: props.service.name,
          description: props.service.description || '',
          basePrice: props.service.basePrice,
          unit: props.service.unit,
          hot: props.service.hot || 0,
          status: props.service.status || 1
        })
      })
    }
  }
})

const resetForm = () => {
  Object.assign(formData, {
    name: '',
    description: '',
    basePrice: null,
    unit: '',
    hot: 0,
    status: 1
  })
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

const handleClose = () => {
  emit('update:visible', false)
  resetForm()
}

const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    loading.value = true

    if (props.service) {
      // 更新服务
      await updateService(props.service.id, formData)
      ElMessage.success('服务更新成功')
    } else {
      // 创建服务
      await createService(formData)
      ElMessage.success('服务创建成功')
    }

    emit('success')
    handleClose()
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error(error.message || '操作失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.dialog-footer {
  text-align: right;
}
</style>