<template>
  <view class="page-cart">
    <!-- 购物车列表 -->
    <view class="cart-list" v-if="cartList.length > 0">
      <view class="cart-item" v-for="item in cartList" :key="item.cartId">
        <view class="item-check" @click="toggleSelect(item.cartId)">
          <text class="check-icon" :class="{ checked: selectedIds.includes(item.cartId) }">
            {{ selectedIds.includes(item.cartId) ? '✓' : '' }}
          </text>
        </view>
        <image class="item-image" :src="item.productImage" mode="aspectFill" @click="goDetail(item.productId)" />
        <view class="item-info">
          <text class="item-name" @click="goDetail(item.productId)">{{ item.productName }}</text>
          <view class="item-bottom">
            <text class="item-price">¥{{ item.price.toFixed(2) }}</text>
            <view class="quantity-control">
              <text class="btn-minus" :class="{ disabled: item.quantity <= 1 }" @click="handleDecrease(item)">-</text>
              <text class="quantity-value">{{ item.quantity }}</text>
              <text class="btn-plus" @click="handleIncrease(item)">+</text>
            </view>
          </view>
        </view>
        <view class="item-delete" @click="handleDelete(item.cartId)">
          <text class="delete-icon">🗑️</text>
        </view>
      </view>
    </view>
    
    <!-- 空状态 -->
    <EmptyState 
      v-else 
      :show="true" 
      text="购物车是空的" 
      buttonText="去逛逛"
      @action="goShopping"
    />
    
    <!-- 底部结算栏 -->
    <view class="bottom-bar" v-if="cartList.length > 0">
      <view class="select-all" @click="toggleSelectAll">
        <text class="check-icon" :class="{ checked: isAllSelected }">
          {{ isAllSelected ? '✓' : '' }}
        </text>
        <text class="select-text">全选</text>
      </view>
      <view class="total-info">
        <text class="total-label">合计：</text>
        <text class="total-price">¥{{ selectedAmount.toFixed(2) }}</text>
      </view>
      <button class="btn-settle" :class="{ disabled: selectedCount === 0 }" @click="handleSettle">
        结算({{ selectedCount }})
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useCartStore } from '@/stores/cart'
import { useUserStore } from '@/stores/user'
import EmptyState from '@/components/EmptyState.vue'
import type { CartItem } from '@/types'

const userStore = useUserStore()
const cartStore = useCartStore()

// 使用 store 中的状态
const cartList = computed(() => cartStore.cartList)
const selectedIds = computed(() => cartStore.selectedIds)
const isAllSelected = computed(() => cartStore.isAllSelected)
const selectedCount = computed(() => cartStore.selectedCount)
const selectedAmount = computed(() => cartStore.selectedAmount)

// 选择/取消选择
function toggleSelect(cartId: number) {
  cartStore.toggleSelect(cartId)
}

// 全选/取消全选
function toggleSelectAll() {
  cartStore.toggleSelectAll()
}

// 减少数量
function handleDecrease(item: CartItem) {
  if (item.quantity <= 1) return
  cartStore.updateQuantity(item.cartId, item.quantity - 1)
}

// 增加数量
function handleIncrease(item: CartItem) {
  cartStore.updateQuantity(item.cartId, item.quantity + 1)
}

// 删除商品
function handleDelete(cartId: number) {
  uni.showModal({
    title: '提示',
    content: '确定删除该商品吗？',
    success: (res) => {
      if (res.confirm) {
        cartStore.removeProduct(cartId)
      }
    }
  })
}

// 结算
function handleSettle() {
  if (selectedCount.value === 0) {
    uni.showToast({ title: '请选择商品', icon: 'none' })
    return
  }
  
  const ids = selectedIds.value.join(',')
  uni.navigateTo({
    url: `/pages/order/confirm?cartIds=${ids}`
  })
}

// 跳转商品详情
function goDetail(productId: number) {
  uni.navigateTo({
    url: `/pages/product/detail?id=${productId}`
  })
}

// 去购物
function goShopping() {
  uni.switchTab({ url: '/pages/index/index' })
}

onShow(() => {
  if (userStore.isLoggedIn) {
    cartStore.fetchCartList()
  }
})

onMounted(() => {
  if (!userStore.isLoggedIn) {
    cartStore.cartList = []
  }
})
</script>

<style lang="scss" scoped>
.page-cart {
  min-height: 100vh;
  padding-bottom: 220rpx;  // 为底部结算栏 + tabBar 预留空间
  background-color: #f5f5f5;
}

.cart-list {
  padding: 20rpx;
  
  .cart-item {
    display: flex;
    align-items: center;
    background-color: #fff;
    border-radius: 16rpx;
    padding: 20rpx;
    margin-bottom: 20rpx;
    
    .item-check {
      padding: 0 16rpx;
      
      .check-icon {
        width: 44rpx;
        height: 44rpx;
        border: 2rpx solid #ddd;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 24rpx;
        color: #fff;
        
        &.checked {
          background-color: #4CAF50;
          border-color: #4CAF50;
        }
      }
    }
    
    .item-image {
      width: 160rpx;
      height: 160rpx;
      border-radius: 8rpx;
      margin-right: 20rpx;
    }
    
    .item-info {
      flex: 1;
      min-height: 160rpx;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      
      .item-name {
        font-size: 28rpx;
        color: #333;
        display: -webkit-box;
        -webkit-box-orient: vertical;
        -webkit-line-clamp: 2;
        overflow: hidden;
      }
      
      .item-bottom {
        display: flex;
        align-items: center;
        justify-content: space-between;
        
        .item-price {
          color: #f44336;
          font-size: 32rpx;
          font-weight: bold;
        }
        
        .quantity-control {
          display: flex;
          align-items: center;
          
          .btn-minus, .btn-plus {
            width: 48rpx;
            height: 48rpx;
            line-height: 48rpx;
            text-align: center;
            background-color: #f5f5f5;
            font-size: 32rpx;
            color: #333;
            border-radius: 8rpx;
            
            &.disabled {
              color: #ccc;
            }
          }
          
          .quantity-value {
            width: 60rpx;
            text-align: center;
            font-size: 28rpx;
          }
        }
      }
    }
    
    .item-delete {
      padding: 20rpx;
      
      .delete-icon {
        font-size: 36rpx;
      }
    }
  }
}

.bottom-bar {
  position: fixed;
  bottom: calc(100rpx + env(safe-area-inset-bottom));  // 位于 tabBar 之上
  left: 0;
  right: 0;
  height: 100rpx;
  background-color: #fff;
  display: flex;
  align-items: center;
  padding: 0 20rpx;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
  
  .select-all {
    display: flex;
    align-items: center;
    
    .check-icon {
      width: 44rpx;
      height: 44rpx;
      border: 2rpx solid #ddd;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 24rpx;
      color: #fff;
      
      &.checked {
        background-color: #4CAF50;
        border-color: #4CAF50;
      }
    }
    
    .select-text {
      font-size: 28rpx;
      color: #333;
      margin-left: 12rpx;
    }
  }
  
  .total-info {
    flex: 1;
    text-align: right;
    padding-right: 20rpx;
    
    .total-label {
      font-size: 28rpx;
      color: #333;
    }
    
    .total-price {
      font-size: 36rpx;
      color: #f44336;
      font-weight: bold;
    }
  }
  
  .btn-settle {
    width: 200rpx;
    height: 72rpx;
    line-height: 72rpx;
    background-color: #4CAF50;
    color: #fff;
    border-radius: 36rpx;
    font-size: 28rpx;
    border: none;
    
    &::after {
      border: none;
    }
    
    &.disabled {
      background-color: #ccc;
    }
  }
}
</style>
