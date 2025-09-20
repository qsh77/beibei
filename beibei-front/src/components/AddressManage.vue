<template>
  <div class="address-manage">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>地址管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加地址
          </el-button>
        </div>
      </template>

      <div v-loading="loading" class="address-list">
        <div v-if="addresses.length === 0" class="empty-state">
          <el-empty description="暂无地址信息" />
        </div>

        <div v-else class="address-cards">
          <el-card
            v-for="address in addresses"
            :key="address.id"
            class="address-card"
            :class="{ 'default-address': address.isDefault }"
            shadow="hover"
          >
            <div class="address-content">
              <div class="address-header">
                <div class="contact-info">
                  <span class="name">{{ address.contactName }}</span>
                  <span class="phone">{{ address.contactPhone }}</span>
                  <el-tag v-if="address.isDefault" type="success" size="small">
                    默认
                  </el-tag>
                </div>
                <div class="actions">
                  <el-button
                    v-if="!address.isDefault"
                    type="text"
                    size="small"
                    @click="handleSetDefault(address.id)"
                  >
                    设为默认
                  </el-button>
                  <el-button
                    type="text"
                    size="small"
                    @click="handleEdit(address)"
                  >
                    编辑
                  </el-button>
                  <el-button
                    type="text"
                    size="small"
                    class="delete-btn"
                    @click="handleDelete(address.id)"
                  >
                    删除
                  </el-button>
                </div>
              </div>
              <div class="address-detail">
                {{ address.province }}{{ address.city }}{{ address.district }}{{ address.detail }}
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </el-card>

    <!-- 添加/编辑地址对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑地址' : '添加地址'"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactName">
              <el-input
                v-model="formData.contactName"
                placeholder="请输入联系人姓名"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="contactPhone">
              <el-input
                v-model="formData.contactPhone"
                placeholder="请输入手机号"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="省份" prop="province">
              <el-input
                v-model="formData.province"
                placeholder="请输入省份"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="城市" prop="city">
              <el-input
                v-model="formData.city"
                placeholder="请输入城市"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="区县" prop="district">
              <el-input
                v-model="formData.district"
                placeholder="请输入区县"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="详细地址" prop="detail">
          <el-input
            v-model="formData.detail"
            type="textarea"
            :rows="3"
            placeholder="请输入详细地址"
          />
        </el-form-item>

        <el-form-item>
          <el-checkbox v-model="formData.isDefault">设为默认地址</el-checkbox>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { addressesApi } from '@/api/addresses.js'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
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
    console.log('获取地址列表响应:', response)
    if (response.code === 200) {
      // 处理数据类型转换，确保 isDefault 是布尔值
      const processedAddresses = (response.data || []).map(address => ({
        ...address,
        isDefault: Boolean(address.isDefault)
      }))
      addresses.value = processedAddresses
      console.log('处理后的地址列表:', processedAddresses)
    }
  } catch (error) {
    console.error('获取地址列表失败:', error)
    ElMessage.error('获取地址列表失败：' + error.message)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (address) => {
  isEdit.value = true
  Object.assign(formData, {
    ...address,
    isDefault: Boolean(address.isDefault)
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()

    submitLoading.value = true

    const submitData = {
      ...formData,
      isDefault: formData.isDefault
    }

    console.log('提交地址数据:', submitData)

    let response
    if (isEdit.value) {
      response = await addressesApi.updateAddress(formData.id, submitData)
    } else {
      response = await addressesApi.addAddress(submitData)
    }

    console.log('地址操作响应:', response)

    // 由于响应拦截器处理，成功时直接是数据
    ElMessage.success(isEdit.value ? '地址更新成功' : '地址添加成功')
    dialogVisible.value = false
    await fetchAddresses()
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
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.address-list {
  min-height: 200px;
}

.empty-state {
  padding: 40px 0;
}

.address-cards {
  display: grid;
  gap: 16px;
}

.address-card {
  transition: all 0.3s;
}

.address-card.default-address {
  border-color: #67c23a;
}

.address-content {
  padding: 0;
}

.address-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.contact-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.name {
  font-weight: 600;
  color: #303133;
}

.phone {
  color: #606266;
}

.actions {
  display: flex;
  gap: 8px;
}

.delete-btn {
  color: #f56c6c;
}

.delete-btn:hover {
  color: #f56c6c;
}

.address-detail {
  color: #606266;
  line-height: 1.6;
  background-color: #f8f9fa;
  padding: 12px;
  border-radius: 6px;
}
</style>