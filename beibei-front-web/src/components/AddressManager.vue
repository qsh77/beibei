<template>
  <div class="space-y-8">
    <div v-loading="loading">
      <div v-if="addresses.length === 0" class="py-10 text-center bg-gray-50 rounded-xl border border-dashed border-gray-200">
        <el-empty description="暂无地址信息" :image-size="100" />
      </div>

      <div v-else class="grid gap-4">
        <div
          v-for="address in addresses"
          :key="address.id"
          class="bg-white rounded-xl border border-gray-200 p-4 transition-all hover:shadow-md hover:border-primary-200 group"
          :class="{ 'ring-1 ring-primary-500 bg-primary-50/10 border-primary-500': address.isDefault }"
        >
          <div class="flex justify-between items-start mb-3">
            <div class="flex items-center gap-3">
              <span class="font-bold text-gray-800">{{ address.contactName }}</span>
              <span class="text-gray-600 font-mono text-sm">{{ address.contactPhone }}</span>
              <span v-if="address.isDefault" class="px-2 py-0.5 bg-primary-50 text-primary-600 text-xs rounded border border-primary-100 font-medium">
                默认
              </span>
            </div>
            
            <div class="flex gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
              <button
                v-if="!address.isDefault"
                @click="handleSetDefault(address.id)"
                class="text-xs text-gray-500 hover:text-primary-600 px-2 py-1 rounded hover:bg-gray-100 transition-colors"
              >
                设为默认
              </button>
              <button
                @click="handleEdit(address)"
                class="text-xs text-blue-500 hover:text-blue-600 px-2 py-1 rounded hover:bg-blue-50 transition-colors"
              >
                编辑
              </button>
              <button
                @click="handleDelete(address.id)"
                class="text-xs text-red-500 hover:text-red-600 px-2 py-1 rounded hover:bg-red-50 transition-colors"
              >
                删除
              </button>
            </div>
          </div>
          
          <div class="text-sm text-gray-600 leading-relaxed bg-gray-50 p-3 rounded-lg border border-gray-100">
            {{ address.province }}{{ address.city }}{{ address.district }}{{ address.detail }}
          </div>
        </div>
      </div>
    </div>

    <div class="border-t border-gray-100 pt-6">
      <h3 class="text-lg font-bold text-gray-900 mb-6 flex items-center gap-2">
        <el-icon class="text-primary-500"><Plus /></el-icon>
        添加新地址
      </h3>
      
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-position="top"
        class="space-y-4"
      >
        <div class="grid grid-cols-2 gap-4">
          <el-form-item label="联系人" prop="contactName">
            <el-input
              v-model="formData.contactName"
              placeholder="请输入姓名"
              class="w-full"
            />
          </el-form-item>
          <el-form-item label="手机号" prop="contactPhone">
            <el-input
              v-model="formData.contactPhone"
              placeholder="请输入手机号"
              class="w-full"
            />
          </el-form-item>
        </div>

        <div class="grid grid-cols-3 gap-4">
          <el-form-item label="省份" prop="province">
            <el-input v-model="formData.province" placeholder="省份" />
          </el-form-item>
          <el-form-item label="城市" prop="city">
            <el-input v-model="formData.city" placeholder="城市" />
          </el-form-item>
          <el-form-item label="区县" prop="district">
            <el-input v-model="formData.district" placeholder="区县" />
          </el-form-item>
        </div>

        <el-form-item label="详细地址" prop="detail">
          <el-input
            v-model="formData.detail"
            type="textarea"
            :rows="2"
            placeholder="街道、楼牌号等"
          />
        </el-form-item>

        <el-form-item>
          <el-checkbox v-model="formData.isDefault">设为默认地址</el-checkbox>
        </el-form-item>

        <div class="flex items-center gap-4 pt-2">
          <button
            type="button"
            @click="handleSubmit"
            class="btn-primary px-6 py-2"
            :disabled="submitLoading"
          >
            <span v-if="submitLoading">提交中...</span>
            <span v-else>{{ formData.id ? '更新地址' : '添加地址' }}</span>
          </button>
          
          <button
            v-if="formData.id"
            type="button"
            @click="resetForm"
            class="px-4 py-2 text-gray-600 hover:text-gray-900 text-sm font-medium"
          >
            取消编辑
          </button>
          <button
             v-else
             type="button"
             @click="resetForm"
             class="px-4 py-2 text-gray-500 hover:text-gray-700 text-sm"
          >
            重置
          </button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { addressesApi } from '@/api/addresses.js'

// 定义事件
const emit = defineEmits(['address-added'])

const loading = ref(false)
const submitLoading = ref(false)
const addresses = ref([])
const formRef = ref()

const formData = reactive({
  id: null,
  contactName: '',
  contactPhone: '',
  province: '',
  city: '',
  district: '',
  detail: '',
  isDefault: false
})

const formRules = {
  contactName: [
    { required: true, message: '请输入联系人姓名', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ],
  province: [
    { required: true, message: '请输入省份', trigger: 'blur' }
  ],
  city: [
    { required: true, message: '请输入城市', trigger: 'blur' }
  ],
  district: [
    { required: true, message: '请输入区县', trigger: 'blur' }
  ],
  detail: [
    { required: true, message: '请输入详细地址', trigger: 'blur' }
  ]
}

const fetchAddresses = async () => {
  try {
    loading.value = true
    const response = await addressesApi.getAddresses()
    if (response.code === 200) {
      const processedAddresses = (response.data || []).map(address => ({
        ...address,
        isDefault: Boolean(address.isDefault)
      }))
      addresses.value = processedAddresses
    }
  } catch (error) {
    console.error('获取地址列表失败:', error)
    ElMessage.error('获取地址列表失败：' + error.message)
  } finally {
    loading.value = false
  }
}

const handleEdit = (address) => {
  Object.assign(formData, {
    ...address,
    isDefault: Boolean(address.isDefault)
  })
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()

    submitLoading.value = true

    const submitData = {
      ...formData,
      isDefault: formData.isDefault
    }

    let response
    if (formData.id) {
      response = await addressesApi.updateAddress(formData.id, submitData)
    } else {
      response = await addressesApi.addAddress(submitData)
    }

    ElMessage.success(formData.id ? '地址更新成功' : '地址添加成功')

    // 发射事件通知父组件
    emit('address-added', response.data || response)

    await fetchAddresses()
    resetForm()
  } catch (error) {
    if (error.errors) {
      return
    }
    console.error('地址操作失败详情:', error)
    ElMessage.error('操作失败：' + error.message)
  } finally {
    submitLoading.value = false
  }
}

const handleSetDefault = async (id) => {
  try {
    const response = await addressesApi.setDefaultAddress(id)
    if (response.code === 200) {
      ElMessage.success('默认地址设置成功')
      await fetchAddresses()
    } else {
      ElMessage.error(response.message || '设置失败')
    }
  } catch (error) {
    ElMessage.error('设置失败：' + error.message)
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这个地址吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await addressesApi.deleteAddress(id)
    if (response.code === 200) {
      ElMessage.success('地址删除成功')
      await fetchAddresses()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error === 'cancel') {
      return
    }
    ElMessage.error('删除失败：' + error.message)
  }
}

const resetForm = () => {
  Object.assign(formData, {
    id: null,
    contactName: '',
    contactPhone: '',
    province: '',
    city: '',
    district: '',
    detail: '',
    isDefault: false
  })

  if (formRef.value) {
    formRef.value.resetFields()
  }
}

onMounted(() => {
  fetchAddresses()
})
</script>

<style scoped>
/* 移除 scoped 样式，依赖 Tailwind CSS */
</style>