<template>
  <view class="loading-more">
    <view v-if="status === 'loading'" class="loading-content">
      <view class="loading-spinner"></view>
      <text class="loading-text">加载中...</text>
    </view>
    <text v-else-if="status === 'nomore'" class="loading-text">没有更多了</text>
  </view>
</template>

<script setup lang="ts">
defineProps<{
  status: 'loading' | 'nomore' | 'more'
}>()
</script>

<style lang="scss" scoped>
.loading-more {
  padding: 30rpx 0;
  text-align: center;
  
  .loading-content {
    display: flex;
    align-items: center;
    justify-content: center;
    
    .loading-spinner {
      width: 32rpx;
      height: 32rpx;
      border: 4rpx solid #ddd;
      border-top-color: #4CAF50;
      border-radius: 50%;
      animation: spin 0.8s linear infinite;
    }
    
    @keyframes spin {
      to {
        transform: rotate(360deg);
      }
    }
  }
  
  .loading-text {
    font-size: 24rpx;
    color: #999;
    margin-left: 16rpx;
  }
}
</style>
