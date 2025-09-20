<template>
  <div class="book-service-page">
    <div class="container">
      <!-- 页面头部 -->
      <div class="page-header">
        <el-button @click="goBack" icon="ArrowLeft">返回</el-button>
        <h1>预约服务</h1>
      </div>

      <div class="booking-content" v-if="service">
        <!-- 服务信息 -->
        <div class="service-info-card">
          <div class="service-image">
            <el-icon><Tools /></el-icon>
          </div>
          <div class="service-details">
            <h2>{{ service.name }}</h2>
            <p>{{ service.description }}</p>
            <div class="price-info">
              <span class="price">¥{{ service.basePrice }}</span>
              <span class="unit">/{{ service.unit }}</span>
            </div>
          </div>
        </div>

        <!-- 预约表单 -->
        <el-form
          ref="bookingFormRef"
          :model="bookingForm"
          :rules="bookingRules"
          label-width="100px"
          class="booking-form"
        >
          <!-- 预约日期 -->
          <el-form-item label="预约日期" prop="scheduleDate">
            <el-date-picker
              v-model="bookingForm.scheduleDate"
              type="date"
              placeholder="选择预约日期"
              :disabled-date="disabledDate"
              style="width: 100%"
            />
          </el-form-item>

          <!-- 时间段 -->
          <el-form-item label="时间段" prop="timeSlot">
            <el-select
              v-model="bookingForm.timeSlot"
              placeholder="选择时间段"
              style="width: 100%"
            >
              <el-option label="上午 (9:00-12:00)" value="上午" />
              <el-option label="下午 (14:00-17:00)" value="下午" />
              <el-option label="晚上 (18:00-21:00)" value="晚上" />
            </el-select>
          </el-form-item>

          <!-- 服务地址 -->
          <el-form-item label="服务地址" prop="addressId">
            <div class="address-section">
              <el-select
                v-model="bookingForm.addressId"
                placeholder="选择服务地址"
                style="width: 100%"
                @change="handleAddressChange"
              >
                <el-option
                  v-for="address in addresses"
                  :key="address.id"
                  :label="`${address.contactName} - ${address.province}${address.city}${address.district}${address.detail}`"
                  :value="address.id"
                />
              </el-select>
              <el-button
                type="primary"
                link
                @click="showAddressDialog = true"
                style="margin-top: 10px"
              >
                <el-icon><Plus /></el-icon>
                添加新地址
              </el-button>
            </div>
          </el-form-item>

          <!-- 备注 -->
          <el-form-item label="备注" prop="remark">
            <el-input
              v-model="bookingForm.remark"
              type="textarea"
              :rows="3"
              placeholder="请输入特殊要求或备注信息"
            />
          </el-form-item>

          <!-- 提交按钮 -->
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              @click="submitBooking"
              :loading="submitting"
              style="width: 100%"
            >
              确认预约
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 地址管理对话框 -->
      <el-dialog
        v-model="showAddressDialog"
        title="地址管理"
        width="600px"
      >
        <AddressManager @address-added="handleAddressAdded" />
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Tools, Plus } from '@element-plus/icons-vue'
import { servicesApi } from '@/api/services.js'
import { addressesApi } from '@/api/addresses.js'
import { ordersApi } from '@/api/orders.js'
import AddressManager from '@/components/AddressManager.vue'

const router = useRouter()
const route = useRoute()

// 响应式数据
const service = ref(null)
const addresses = ref([])
const bookingForm = ref({
  scheduleDate: null,
  timeSlot: '',
  addressId: null,
  remark: ''
})
const bookingFormRef = ref(null)
const submitting = ref(false)
const showAddressDialog = ref(false)

// 表单验证规则
const bookingRules = {
  scheduleDate: [
    { required: true, message: '请选择预约日期', trigger: 'change' }
  ],
  timeSlot: [
    { required: true, message: '请选择时间段', trigger: 'change' }
  ],
  addressId: [
    { required: true, message: '请选择服务地址', trigger: 'change' }
  ]
}

// 禁用过去的日期
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7 // 禁用今天之前的日期
}

// 返回上一页
const goBack = () => {
  router.go(-1)
}

// 加载服务信息
const loadService = async () => {
  try {
    const serviceId = route.params.serviceId
    if (serviceId) {
      const data = await servicesApi.getServiceById(serviceId)
      service.value = data
    } else if (route.query.service) {
      // 从路由参数中获取服务信息
      service.value = JSON.parse(route.query.service)
    }
  } catch (error) {
    console.error('加载服务信息失败:', error)
    ElMessage.error('加载服务信息失败')
    router.push('/services')
  }
}

// 加载地址列表
const loadAddresses = async () => {
  try {
    const data = await addressesApi.getAddresses()
    addresses.value = data || []
    
    // 如果有默认地址，自动选择
    const defaultAddress = addresses.value.find(addr => addr.isDefault === 1)
    if (defaultAddress) {
      bookingForm.value.addressId = defaultAddress.id
    }
  } catch (error) {
    console.error('加载地址列表失败:', error)
    ElMessage.error('加载地址列表失败')
  }
}

// 处理地址变化
const handleAddressChange = (addressId) => {
  const selectedAddress = addresses.value.find(addr => addr.id === addressId)
  if (selectedAddress) {
    console.log('选择的地址:', selectedAddress)
  }
}

// 处理地址添加
const handleAddressAdded = (newAddress) => {
  addresses.value.push(newAddress)
  bookingForm.value.addressId = newAddress.id
  showAddressDialog.value = false
  ElMessage.success('地址添加成功')
}

// 提交预约
const submitBooking = async () => {
  if (!bookingFormRef.value) return
  
  try {
    await bookingFormRef.value.validate()
    
    // 确认预约
    await ElMessageBox.confirm(
      '确认提交预约申请吗？',
      '确认预约',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'info'
      }
    )
    
    submitting.value = true
    
    const orderData = {
      serviceId: service.value.id,
      scheduleDate: bookingForm.value.scheduleDate,
      timeSlot: bookingForm.value.timeSlot,
      addressId: bookingForm.value.addressId,
      remark: bookingForm.value.remark
    }
    
    const result = await ordersApi.createOrder(orderData)
    
    ElMessage.success('预约成功！')
    
    // 跳转到订单详情页面
    router.push({
      name: 'OrderDetail',
      params: { orderId: result.id }
    })
    
  } catch (error) {
    if (error !== 'cancel') {
      console.error('预约失败:', error)
      ElMessage.error('预约失败，请重试')
    }
  } finally {
    submitting.value = false
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadService()
  loadAddresses()
})
</script>

<style scoped>
.book-service-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20px 0;
}

.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
}

.page-header h1 {
  font-size: 2rem;
  color: #2c3e50;
  margin: 0;
}

.booking-content {
  display: grid;
  gap: 30px;
}

.service-info-card {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  display: flex;
  gap: 20px;
  align-items: center;
}

.service-image {
  flex-shrink: 0;
  width: 80px;
  height: 80px;
  background: #f0f9ff;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.service-image .el-icon {
  font-size: 2.5rem;
  color: #409eff;
}

.service-details {
  flex: 1;
}

.service-details h2 {
  font-size: 1.5rem;
  color: #2c3e50;
  margin: 0 0 10px 0;
}

.service-details p {
  color: #7f8c8d;
  margin: 0 0 15px 0;
  line-height: 1.5;
}

.price-info {
  display: flex;
  align-items: baseline;
  gap: 5px;
}

.price {
  font-size: 1.8rem;
  font-weight: bold;
  color: #e74c3c;
}

.unit {
  color: #7f8c8d;
  font-size: 1rem;
}

.booking-form {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.address-section {
  width: 100%;
}

@media (max-width: 768px) {
  .container {
    padding: 0 15px;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .page-header h1 {
    font-size: 1.5rem;
  }
  
  .service-info-card {
    flex-direction: column;
    text-align: center;
    padding: 20px;
  }
  
  .service-image {
    width: 60px;
    height: 60px;
  }
  
  .service-image .el-icon {
    font-size: 2rem;
  }
  
  .booking-form {
    padding: 20px;
  }
}
</style>
