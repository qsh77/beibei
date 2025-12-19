<template>
  <div class="min-h-screen bg-slate-50 pb-20">
    <!-- 顶部导航 -->
    <header class="bg-white border-b border-gray-100 sticky top-0 z-40">
      <div class="container-responsive h-16 flex items-center justify-between">
        <div class="flex items-center gap-4">
          <button @click="goBack" class="p-2 hover:bg-gray-50 rounded-lg text-gray-500 transition-colors">
            <el-icon size="20"><Back /></el-icon>
          </button>
          <h1 class="text-xl font-bold text-gray-900">预约服务</h1>
        </div>
      </div>
    </header>

    <div class="container-responsive max-w-4xl py-8">
      <!-- 步骤条 -->
      <div class="mb-8 px-4">
        <el-steps :active="1" finish-status="success" align-center>
          <el-step title="选择服务" />
          <el-step title="填写信息" />
          <el-step title="提交订单" />
        </el-steps>
      </div>

      <div v-if="service" class="grid lg:grid-cols-3 gap-8">
        <!-- 左侧：服务信息概览 -->
        <div class="lg:col-span-1">
          <div class="bg-white rounded-xl border border-gray-100 p-6 sticky top-24">
            <h3 class="text-lg font-bold text-gray-900 mb-4">服务概览</h3>
            <div class="flex items-start gap-4 mb-6">
              <div class="w-16 h-16 bg-primary-50 rounded-lg flex items-center justify-center text-primary-600 flex-shrink-0">
                <el-icon size="32"><Tools /></el-icon>
              </div>
              <div>
                <div class="font-bold text-gray-900 mb-1">{{ service.name }}</div>
                <div class="text-sm text-gray-500 leading-snug line-clamp-2">{{ service.description }}</div>
              </div>
            </div>
            
            <div class="border-t border-gray-100 pt-4 mb-4">
              <div class="flex justify-between items-center mb-2">
                <span class="text-gray-600">服务单价</span>
                <span class="font-bold text-gray-900">¥{{ service.priceText }} / {{ service.unit }}</span>
              </div>
            </div>
            
            <div class="text-sm text-gray-600 leading-relaxed">
              <div class="font-medium text-gray-900 mb-2">服务介绍</div>
              {{ service.description }}
            </div>
          </div>
        </div>

        <!-- 右侧：预约表单 -->
        <div class="lg:col-span-2">
          <div class="bg-white rounded-xl border border-gray-100 p-6 lg:p-8">
            <h2 class="text-xl font-bold text-gray-900 mb-6">填写预约信息</h2>
            
            <el-form
              ref="bookingFormRef"
              :model="bookingForm"
              :rules="bookingRules"
              label-position="top"
              size="large"
            >
              <div class="grid md:grid-cols-2 gap-x-6">
                <!-- 预约日期 -->
                <el-form-item label="预约日期" prop="scheduleDate">
                  <el-date-picker
                    v-model="bookingForm.scheduleDate"
                    type="date"
                    placeholder="请选择日期"
                    :disabled-date="disabledDate"
                    class="w-full"
                    format="YYYY-MM-DD"
                    value-format="YYYY-MM-DD"
                  />
                </el-form-item>

                <!-- 时间段 -->
                <el-form-item label="上门时段" prop="timeSlot">
                  <el-select v-model="bookingForm.timeSlot" placeholder="请选择时段" class="w-full">
                    <el-option label="上午 (09:00 - 12:00)" value="上午" />
                    <el-option label="下午 (14:00 - 17:00)" value="下午" />
                    <el-option label="晚上 (18:00 - 21:00)" value="晚上" />
                  </el-select>
                </el-form-item>
              </div>

              <!-- 服务地址 -->
              <el-form-item label="服务地址" prop="addressId">
                <div class="w-full">
                  <el-select
                    v-model="bookingForm.addressId"
                    placeholder="请选择服务地址"
                    class="w-full mb-2"
                  >
                    <el-option
                      v-for="address in addresses"
                      :key="address.id"
                      :label="formatAddressLabel(address)"
                      :value="address.id"
                    />
                  </el-select>
                  <button 
                    type="button"
                    @click="showAddressDialog = true"
                    class="text-primary-600 text-sm hover:underline flex items-center gap-1"
                  >
                    <el-icon><Plus /></el-icon> 添加新地址
                  </button>
                </div>
              </el-form-item>

              <!-- 备注 -->
              <el-form-item label="备注要求" prop="remark">
                <el-input
                  v-model="bookingForm.remark"
                  type="textarea"
                  :rows="3"
                  placeholder="请输入您的特殊要求（选填）"
                  maxlength="200"
                  show-word-limit
                />
              </el-form-item>

              <!-- 提交按钮 -->
              <div class="mt-8 pt-6 border-t border-gray-100 flex flex-col md:flex-row items-center justify-between gap-4">
                <div class="text-sm text-gray-500">
                  提交即代表同意 <a href="#" class="text-primary-600 hover:underline">《用户服务协议》</a>
                </div>
                <button 
                  type="button"
                  @click="submitBooking" 
                  :disabled="submitting"
                  class="btn-primary w-full md:w-auto px-8 py-3 text-base flex items-center justify-center gap-2 min-w-[160px]"
                >
                   <span v-if="submitting">提交中...</span>
                   <span v-else>立即预约</span>
                </button>
              </div>
            </el-form>
          </div>
        </div>
      </div>
    </div>

    <!-- 地址管理对话框 -->
    <el-dialog
      v-model="showAddressDialog"
      title="添加新地址"
      width="600px"
      align-center
      class="rounded-xl"
    >
      <AddressManager @address-added="handleAddressAdded" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Back, Tools, Plus, InfoFilled
} from '@element-plus/icons-vue'
import { servicesApi } from '@/api/services.js'
import { addressesApi } from '@/api/addresses.js'
import { createOrder } from '@/api/orders.js'
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

const extractPayload = (response) => {
  if (!response) {
    return null
  }
  if (typeof response === 'object' && response.code === 200) {
    return response.data ?? null
  }
  return response
}

const extractList = (response) => {
  const payload = extractPayload(response)
  return Array.isArray(payload) ? payload : []
}

const toNumber = (value, fallback = 0) => {
  const numberValue = Number(value)
  return Number.isFinite(numberValue) ? numberValue : fallback
}

const formatPrice = (value) => {
  return toNumber(value, 0).toFixed(2)
}

const normalizeServiceData = (rawService) => {
  if (!rawService) return null

  const priceValue = toNumber(rawService.basePrice, 0)
  const ratingValueRaw = toNumber(rawService.rating, 0)
  const ratingValue = Math.min(Math.max(ratingValueRaw, 0), 5)

  return {
    ...rawService,
    priceValue,
    priceText: formatPrice(rawService.basePrice),
    ratingValue: Number(ratingValue.toFixed(1)),
    unit: rawService.unit || '次',
    description: rawService.description || '暂无服务介绍'
  }
}

const normalizeAddress = (address) => ({
  ...address,
  isDefault: address?.isDefault === 1 || address?.isDefault === true
})

const formatAddressLabel = (address) => {
  return `${address.contactName} ${address.contactPhone} - ${address.province}${address.city}${address.district}${address.detail}`
}

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
      const response = await servicesApi.getServiceById(serviceId)
      const payload = extractPayload(response)
      service.value = normalizeServiceData(payload)
    }

    if (!service.value && route.query.service) {
      // 从路由参数中获取服务信息
      try {
        const parsed = JSON.parse(route.query.service)
        service.value = normalizeServiceData(parsed)
      } catch (parseError) {
        console.error('解析路由中的服务信息失败:', parseError)
      }
    }

    if (!service.value) {
      throw new Error('未找到服务信息')
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
    const response = await addressesApi.getAddresses()
    addresses.value = extractList(response).map(normalizeAddress)

    // 如果有默认地址，自动选择
    const defaultAddress = addresses.value.find(addr => addr.isDefault)
    if (defaultAddress) {
      bookingForm.value.addressId = defaultAddress.id
    } else if (addresses.value.length > 0) {
      bookingForm.value.addressId = addresses.value[0].id
    }
  } catch (error) {
    console.error('加载地址列表失败:', error)
    ElMessage.error('加载地址列表失败')
  }
}

// 处理地址添加
const handleAddressAdded = (newAddress) => {
  const normalized = normalizeAddress(newAddress)
  addresses.value.push(normalized)
  bookingForm.value.addressId = normalized.id
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
    
    const result = await createOrder(orderData)
    const createdOrder = extractPayload(result)
    const successMessage = result?.message || '预约成功！'

    ElMessage.success(successMessage)

    // 跳转到订单列表，可在页面中高亮最新订单
    const query = createdOrder?.id ? { orderId: createdOrder.id } : {}
    router.push({
      name: 'Orders',
      query
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
/* 移除所有自定义 CSS，依赖 Tailwind */
</style>
