<template>
  <el-dialog
    :model-value="visible"
    :title="category ? '编辑分类' : '添加分类'"
    width="500px"
    @update:model-value="handleClose"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="80px"
    >
      <el-form-item label="分类名称" prop="name">
        <el-input
          v-model="formData.name"
          placeholder="请输入分类名称"
          maxlength="50"
          show-word-limit
        />
      </el-form-item>

      <el-form-item label="父分类" prop="parentId">
        <el-select
          v-model="formData.parentId"
          placeholder="请选择父分类（可为空）"
          style="width: 100%"
          clearable
        >
          <el-option
            v-for="cat in availableParents"
            :key="cat.id"
            :label="cat.name"
            :value="cat.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="排序" prop="sort">
        <el-input-number
          v-model="formData.sort"
          :min="0"
          :max="999"
          placeholder="排序值，数字越小越靠前"
          style="width: 100%"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" :loading="loading" @click="handleSubmit">
          {{ category ? '更新' : '创建' }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, nextTick, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { createCategory, updateCategory, getAllCategories } from '@/api/admin'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  category: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:visible', 'success'])

const formRef = ref()
const loading = ref(false)
const allCategories = ref([])

const formData = reactive({
  name: '',
  parentId: null,
  sort: 0
})

const rules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 2, max: 50, message: '分类名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  sort: [
    { type: 'number', min: 0, max: 999, message: '排序值范围 0-999', trigger: 'blur' }
  ]
}

// 计算可选的父分类（排除自己和自己的子分类）
const availableParents = computed(() => {
  if (!props.category) {
    return allCategories.value
  }

  // 编辑模式：排除当前分类本身
  return allCategories.value.filter(cat => cat.id !== props.category.id)
})

// 监听对话框显示状态
watch(() => props.visible, async (newVal) => {
  if (newVal) {
    await loadCategories()
    resetForm()
    if (props.category) {
      nextTick(() => {
        Object.assign(formData, {
          name: props.category.name,
          parentId: props.category.parentId || null,
          sort: props.category.sort || 0
        })
      })
    }
  }
})

const loadCategories = async () => {
  try {
    const response = await getAllCategories()
    allCategories.value = response.data || []
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

const resetForm = () => {
  Object.assign(formData, {
    name: '',
    parentId: null,
    sort: 0
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

    if (props.category) {
      // 更新分类
      await updateCategory(props.category.id, formData)
      ElMessage.success('分类更新成功')
    } else {
      // 创建分类
      await createCategory(formData)
      ElMessage.success('分类创建成功')
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