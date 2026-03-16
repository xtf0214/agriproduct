<template>
  <view class="product-card" @click="handleClick">
    <image 
      class="product-image" 
      :src="imageSrc" 
      mode="aspectFill"
      @error="handleImageError"
    />
    <view class="product-info">
      <text class="product-name">{{ product?.name || '商品名称' }}</text>
      <view class="product-bottom">
        <text class="product-price">
          <text class="price-symbol">¥</text>
          {{ (product?.price || 0).toFixed(2) }}
        </text>
        <text class="product-sales">已售{{ product?.sales || 0 }}</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import type { Product } from '@/types'

const DEFAULT_PRODUCT_IMAGE = '/static/logo.png'

const props = defineProps<{
  product: Product
}>()

const emit = defineEmits<{
  (e: 'click', product: Product): void
}>()

const imageSrc = ref(DEFAULT_PRODUCT_IMAGE)

watch(
  () => props.product?.imageUrl,
  (url) => {
    imageSrc.value = url || DEFAULT_PRODUCT_IMAGE
  },
  { immediate: true }
)

function handleClick() {
  if (!props.product?.id) {
    console.error('商品数据无效:', props.product)
    return
  }
  
  emit('click', props.product)
  uni.navigateTo({
    url: `/pages/product/detail?id=${props.product.id}`
  })
}

function handleImageError(e: any) {
  imageSrc.value = DEFAULT_PRODUCT_IMAGE
  console.error('商品图片加载失败:', props.product?.imageUrl, e)
}
</script>

<style lang="scss" scoped>
.product-card {
  background-color: #fff;
  border-radius: 16rpx;
  overflow: hidden;
  
  .product-image {
    width: 100%;
    height: 320rpx;
    background-color: #f5f5f5;
  }
  
  .product-info {
    padding: 16rpx;
    
    .product-name {
      display: -webkit-box;
      -webkit-box-orient: vertical;
      -webkit-line-clamp: 2;
      overflow: hidden;
      font-size: 28rpx;
      color: #333;
      line-height: 1.4;
      min-height: 78rpx;
    }
    
    .product-bottom {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-top: 12rpx;
      
      .product-price {
        color: #f44336;
        font-size: 32rpx;
        font-weight: bold;
        
        .price-symbol {
          font-size: 24rpx;
        }
      }
      
      .product-sales {
        font-size: 24rpx;
        color: #999;
      }
    }
  }
}
</style>
