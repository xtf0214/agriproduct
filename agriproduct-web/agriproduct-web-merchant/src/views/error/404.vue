<template>
  <div class="error-container">
    <div class="error-content">
      <h1 class="error-code">404</h1>
      <h2 class="error-message">页面不存在</h2>
      <p class="error-desc">抱歉，您访问的页面不存在或已被删除</p>
      <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped lang="scss">
.error-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f5f7fa;
  
  .error-content {
    text-align: center;
    
    .error-code {
      font-size: 120px;
      font-weight: 600;
      color: #409EFF;
      line-height: 1;
      margin-bottom: 20px;
    }
    
    .error-message {
      font-size: 24px;
      color: #303133;
      margin-bottom: 10px;
    }
    
    .error-desc {
      color: #909399;
      margin-bottom: 20px;
    }
  }
}
</style>
