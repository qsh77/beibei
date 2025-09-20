<template>
  <router-view />
  
</template>

<script setup>
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  height: 100%;
  width: 100%;
  margin: 0;
  padding: 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  line-height: 1.6;
  color: #333;
  overflow-x: hidden;
}

#app {
  min-height: 100vh;
  width: 100%;
  display: flex;
  flex-direction: column;
  margin: 0;
  padding: 0;
}

/* 重置Element Plus容器样式 */
.el-container {
  width: 100% !important;
  margin: 0 !important;
  padding: 0 !important;
}

.el-header {
  width: 100% !important;
  margin: 0 !important;
  padding: 0 !important;
}

.el-main {
  width: 100% !important;
  margin: 0 !important;
  padding: 0 !important;
}

.el-footer {
  width: 100% !important;
  margin: 0 !important;
  padding: 40px 0 20px !important;
}

.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  font-size: 18px;
  color: #666;
}
</style>
