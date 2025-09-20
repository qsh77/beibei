<template>
  <div>
    <!-- 占位组件 -->
    <p>订单分配对话框组件开发中...</p>
  </div>
</template>

<script setup>
// 占位组件
defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  order: {
    type: Object,
    default: null
  }
})

defineEmits(['update:visible', 'success'])
</script>