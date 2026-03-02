<template>
  <view class="page-detail">
    <swiper class="product-swiper" indicator-dots circular v-if="product">
      <swiper-item v-for="(img, index) in productImages" :key="index">
        <image class="swiper-image" :src="img" mode="aspectFill" />
      </swiper-item>
    </swiper>
    
    <view class="product-info" v-if="product">
      <view class="price-row">
        <text class="price">
          <text class="price-symbol">¥</text>
          {{ product.price.toFixed(2) }}
        </text>
        <text class="sales">已售 {{ product.sales }} 件</text>
      </view>
      
      <view class="title-row">
        <text class="product-name">{{ product.name }}</text>
      </view>
      
      <view class="merchant-row" v-if="product.merchantName">
        <text class="merchant-label">商家：</text>
        <text class="merchant-name">{{ product.merchantName }}</text>
      </view>
    </view>
    
    <view class="product-section" v-if="product">
      <view class="section-title">商品详情</view>
      <view class="section-content">
        <text class="description">{{ product.description || '暂无描述' }}</text>
        <view class="detail-images" v-if="product.images?.length">
          <image 
            v-for="(img, index) in product.images" 
            :key="index"
            class="detail-image"
            :src="img"
            mode="widthFix"
          />
        </view>
      </view>
    </view>
    
    <!-- 底部操作栏 -->
    <view class="bottom-bar">
      <view class="action-icons">
        <view class="action-item" @click="goHome">
          <text class="icon">🏠</text>
          <text class="text">首页</text>
        </view>
        <view class="action-item" @click="goCart">
          <text class="icon">🛒</text>
          <text class="text">购物车</text>
          <view class="badge" v-if="cartCount > 0">{{ cartCount > 99 ? '99+' : cartCount }}</view>
        </view>
      </view>
      
      <view class="action-buttons">
        <button class="btn-cart" @click="handleAddCart">加入购物车</button>
        <button class="btn-buy" @click="handleBuyNow">立即购买</button>
      </view>
    </view>
    
    <!-- 数量选择弹窗 -->
    <view class="quantity-popup" v-if="showQuantityPopup" @click="showQuantityPopup = false">
      <view class="popup-content" @click.stop>
        <view class="popup-header">
          <image class="popup-image" :src="product?.imageUrl" mode="aspectFill" />
          <view class="popup-info">
            <text class="popup-price">¥{{ product?.price.toFixed(2) }}</text>
            <text class="popup-stock">库存: {{ product?.stock }}件</text>
          </view>
          <text class="popup-close" @click="showQuantityPopup = false">✕</text>
        </view>
        <view class="popup-body">
          <view class="quantity-row">
            <text class="quantity-label">数量</text>
            <view class="quantity-control">
              <text class="btn-minus" :class="{ disabled: quantity <= 1 }" @click="decreaseQuantity">-</text>
              <text class="quantity-value">{{ quantity }}</text>
              <text class="btn-plus" :class="{ disabled: quantity >= (product?.stock || 1) }" @click="increaseQuantity">+</text>
            </view>
          </view>
        </view>
        <view class="popup-footer">
          <button class="btn-confirm" @click="confirmAction">确定</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getProductDetail } from '@/api/product'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'
import type { Product } from '@/types'

const productId = ref(0)
const product = ref<Product | null>(null)
const loading = ref(false)
const quantity = ref(1)
const showQuantityPopup = ref(false)
const actionType = ref<'cart' | 'buy'>('cart')
const cartCount = ref(0)

const userStore = useUserStore()
const cartStore = useCartStore()

// 商品图片
const productImages = computed(() => {
  if (!product.value) return []
  return product.value.images?.length ? product.value.images : [product.value.imageUrl]
})

// 获取商品详情
async function fetchProductDetail() {
  if (!productId.value) return
  loading.value = true
  try {
    const detail = await getProductDetail(productId.value)
    product.value = detail
  } catch (e) {
    console.error('获取商品详情失败', e)
    uni.showToast({ title: '获取商品失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

// 增加数量
function increaseQuantity() {
  if (product.value && quantity.value < product.value.stock) {
    quantity.value++
  }
}

// 减少数量
function decreaseQuantity() {
  if (quantity.value > 1) {
    quantity.value--
  }
}

// 加入购物车
function handleAddCart() {
  if (!userStore.checkLogin()) return
  actionType.value = 'cart'
  showQuantityPopup.value = true
}

// 立即购买
function handleBuyNow() {
  if (!userStore.checkLogin()) return
  actionType.value = 'buy'
  showQuantityPopup.value = true
}

// 确认操作
async function confirmAction() {
  if (!product.value) return
  
  showQuantityPopup.value = false
  
  if (actionType.value === 'cart') {
    const success = await cartStore.addProduct(product.value.id, quantity.value)
    if (success) {
      cartCount.value = await cartStore.fetchCartCount()
    }
  } else {
    // 立即购买，跳转到确认订单页
    uni.navigateTo({
      url: `/pages/order/confirm?productId=${product.value.id}&quantity=${quantity.value}`
    })
  }
}

// 返回首页
function goHome() {
  uni.switchTab({ url: '/pages/index/index' })
}

// 跳转购物车
function goCart() {
  uni.switchTab({ url: '/pages/cart/cart' })
}

onLoad((options) => {
  if (options?.id) {
    productId.value = Number(options.id)
    fetchProductDetail()
  }
})

onMounted(async () => {
  if (userStore.isLoggedIn) {
    cartCount.value = await cartStore.fetchCartCount()
  }
})
</script>

<style lang="scss" scoped>
.page-detail {
  min-height: 100vh;
  padding-bottom: 120rpx;
  background-color: #f5f5f5;
}

.product-swiper {
  width: 100%;
  height: 750rpx;
  
  .swiper-image {
    width: 100%;
    height: 100%;
  }
}

.product-info {
  background-color: #fff;
  padding: 24rpx 30rpx;
  margin-bottom: 20rpx;
  
  .price-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    
    .price {
      color: #f44336;
      font-size: 48rpx;
      font-weight: bold;
      
      .price-symbol {
        font-size: 28rpx;
      }
    }
    
    .sales {
      font-size: 24rpx;
      color: #999;
    }
  }
  
  .title-row {
    margin-top: 16rpx;
    
    .product-name {
      font-size: 32rpx;
      color: #333;
      line-height: 1.5;
    }
  }
  
  .merchant-row {
    margin-top: 16rpx;
    
    .merchant-label {
      font-size: 26rpx;
      color: #999;
    }
    
    .merchant-name {
      font-size: 26rpx;
      color: #666;
    }
  }
}

.product-section {
  background-color: #fff;
  
  .section-title {
    padding: 24rpx 30rpx;
    font-size: 30rpx;
    font-weight: bold;
    color: #333;
    border-bottom: 1rpx solid #eee;
  }
  
  .section-content {
    padding: 24rpx 30rpx;
    
    .description {
      font-size: 28rpx;
      color: #666;
      line-height: 1.6;
    }
    
    .detail-images {
      margin-top: 20rpx;
      
      .detail-image {
        width: 100%;
      }
    }
  }
}

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 100rpx;
  background-color: #fff;
  display: flex;
  align-items: center;
  padding: 0 20rpx;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
  
  .action-icons {
    display: flex;
    margin-right: 20rpx;
    
    .action-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 0 20rpx;
      position: relative;
      
      .icon {
        font-size: 40rpx;
      }
      
      .text {
        font-size: 20rpx;
        color: #666;
        margin-top: 4rpx;
      }
      
      .badge {
        position: absolute;
        top: 0;
        right: 10rpx;
        background-color: #f44336;
        color: #fff;
        font-size: 20rpx;
        padding: 2rpx 8rpx;
        border-radius: 20rpx;
        min-width: 30rpx;
        text-align: center;
      }
    }
  }
  
  .action-buttons {
    flex: 1;
    display: flex;
    
    button {
      flex: 1;
      height: 72rpx;
      line-height: 72rpx;
      border-radius: 36rpx;
      font-size: 28rpx;
      border: none;
      margin: 0 6rpx;
      
      &::after {
        border: none;
      }
    }
    
    .btn-cart {
      background-color: #ffa500;
      color: #fff;
    }
    
    .btn-buy {
      background-color: #f44336;
      color: #fff;
    }
  }
}

.quantity-popup {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 999;
  display: flex;
  align-items: flex-end;
  
  .popup-content {
    width: 100%;
    background-color: #fff;
    border-radius: 24rpx 24rpx 0 0;
  }
  
  .popup-header {
    display: flex;
    padding: 24rpx;
    border-bottom: 1rpx solid #eee;
    position: relative;
    
    .popup-image {
      width: 160rpx;
      height: 160rpx;
      border-radius: 8rpx;
    }
    
    .popup-info {
      flex: 1;
      padding-left: 20rpx;
      display: flex;
      flex-direction: column;
      justify-content: center;
      
      .popup-price {
        font-size: 36rpx;
        color: #f44336;
        font-weight: bold;
      }
      
      .popup-stock {
        font-size: 24rpx;
        color: #999;
        margin-top: 12rpx;
      }
    }
    
    .popup-close {
      position: absolute;
      top: 24rpx;
      right: 24rpx;
      font-size: 36rpx;
      color: #999;
    }
  }
  
  .popup-body {
    padding: 30rpx 24rpx;
    
    .quantity-row {
      display: flex;
      align-items: center;
      justify-content: space-between;
      
      .quantity-label {
        font-size: 28rpx;
        color: #333;
      }
      
      .quantity-control {
        display: flex;
        align-items: center;
        
        .btn-minus, .btn-plus {
          width: 56rpx;
          height: 56rpx;
          line-height: 56rpx;
          text-align: center;
          background-color: #f5f5f5;
          font-size: 36rpx;
          color: #333;
          border-radius: 8rpx;
          
          &.disabled {
            color: #ccc;
          }
        }
        
        .quantity-value {
          width: 80rpx;
          text-align: center;
          font-size: 28rpx;
        }
      }
    }
  }
  
  .popup-footer {
    padding: 20rpx 24rpx 40rpx;
    
    .btn-confirm {
      width: 100%;
      height: 80rpx;
      line-height: 80rpx;
      background-color: #4CAF50;
      color: #fff;
      border-radius: 40rpx;
      border: none;
      font-size: 30rpx;
      
      &::after {
        border: none;
      }
    }
  }
}
</style>
