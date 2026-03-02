<template>
  <view class="empty" v-if="show">
    <image class="empty-image" :src="image" mode="aspectFit" />
    <text class="empty-text">{{ text }}</text>
    <button v-if="buttonText" class="empty-btn" @click="$emit('action')">
      {{ buttonText }}
    </button>
  </view>
</template>

<script setup lang="ts">
defineProps<{
  show: boolean
  text?: string
  image?: string
  buttonText?: string
}>()

defineEmits<{
  (e: 'action'): void
}>()
</script>

<style lang="scss" scoped>
.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 40rpx;
  
  .empty-image {
    width: 240rpx;
    height: 240rpx;
    margin-bottom: 30rpx;
  }
  
  .empty-text {
    font-size: 28rpx;
    color: #999;
    margin-bottom: 30rpx;
  }
  
  .empty-btn {
    background-color: #4CAF50;
    color: #fff;
    font-size: 28rpx;
    padding: 16rpx 60rpx;
    border-radius: 40rpx;
    border: none;
    
    &::after {
      border: none;
    }
  }
}
</style>
