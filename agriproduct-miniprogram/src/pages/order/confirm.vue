<template>
  <view class="page-confirm">
    <!-- 收货地址 -->
    <view class="address-section" @click="selectAddress">
      <view class="address-content" v-if="address">
        <view class="address-header">
          <text class="receiver-name">{{ address.receiverName }}</text>
          <text class="receiver-phone">{{ address.receiverPhone }}</text>
        </view>
        <text class="address-detail">{{ fullAddress }}</text>
      </view>
      <view class="address-empty" v-else>
        <text class="empty-text">请选择收货地址</text>
      </view>
      <text class="arrow">›</text>
    </view>
    
    <!-- 商品列表 -->
    <view class="product-section">
      <view class="product-item" v-for="item in products" :key="item.cartId || item.productId">
        <image class="product-image" :src="item.productImage" mode="aspectFill" />
        <view class="product-info">
          <text class="product-name">{{ item.productName }}</text>
          <view class="product-bottom">
            <text class="product-price">¥{{ item.price.toFixed(2) }}</text>
            <text class="product-quantity">x{{ item.quantity }}</text>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 订单备注 -->
    <view class="remark-section">
      <text class="remark-label">备注</text>
      <input class="remark-input" v-model="remark" placeholder="选填，建议与商家协商一致" />
    </view>
    
    <!-- 金额信息 -->
    <view class="amount-section">
      <view class="amount-row">
        <text class="amount-label">商品金额</text>
        <text class="amount-value">¥{{ totalAmount.toFixed(2) }}</text>
      </view>
      <view class="amount-row">
        <text class="amount-label">运费</text>
        <text class="amount-value">¥0.00</text>
      </view>
      <view class="amount-row total">
        <text class="amount-label">合计</text>
        <text class="amount-value">¥{{ totalAmount.toFixed(2) }}</text>
      </view>
    </view>
    
    <!-- 底部提交栏 -->
    <view class="bottom-bar">
      <view class="total-info">
        <text class="total-label">实付：</text>
        <text class="total-price">¥{{ totalAmount.toFixed(2) }}</text>
      </view>
      <button class="btn-submit" :class="{ disabled: !address || submitting }" @click="handleSubmit">
        {{ submitting ? '提交中...' : '提交订单' }}
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getDefaultAddress } from '@/api/address'
import { getProductDetail } from '@/api/product'
import { createOrder } from '@/api/order'
import { useCartStore } from '@/stores/cart'
import type { Address, Product, CartItem } from '@/types'

// 商品项（用于展示）
interface OrderProduct {
  cartId?: number
  productId: number
  productName: string
  productImage: string
  price: number
  quantity: number
}

const cartIds = ref<number[]>([])
const productId = ref<number>(0)
const quantity = ref<number>(1)
const products = ref<OrderProduct[]>([])
const address = ref<Address | null>(null)
const remark = ref('')
const submitting = ref(false)

const cartStore = useCartStore()

// 完整地址
const fullAddress = computed(() => {
  if (!address.value) return ''
  const { province, city, district, detailAddress } = address.value
  return `${province}${city}${district}${detailAddress}`
})

// 商品总金额
const totalAmount = computed(() => {
  return products.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
})

// 选择地址
function selectAddress() {
  uni.navigateTo({
    url: '/pages/address/list?select=1'
  })
}

// 加载商品信息
async function loadProducts() {
  // 从购物车下单
  if (cartIds.value.length > 0) {
    const selectedProducts = cartStore.cartList.filter(item => 
      cartIds.value.includes(item.cartId)
    )
    products.value = selectedProducts.map(item => ({
      cartId: item.cartId,
      productId: item.productId,
      productName: item.productName,
      productImage: item.productImage,
      price: item.price,
      quantity: item.quantity
    }))
  }
  // 直接购买
  else if (productId.value) {
    try {
      const detail = await getProductDetail(productId.value)
      products.value = [{
        productId: detail.id,
        productName: detail.name,
        productImage: detail.imageUrl,
        price: detail.price,
        quantity: quantity.value
      }]
    } catch (e) {
      console.error('获取商品失败', e)
      uni.showToast({ title: '获取商品失败', icon: 'none' })
    }
  }
}

// 加载默认地址
async function loadDefaultAddress() {
  try {
    const addr = await getDefaultAddress()
    address.value = addr
  } catch (e) {
    console.error('获取地址失败', e)
  }
}

// 提交订单
async function handleSubmit() {
  if (!address.value) {
    uni.showToast({ title: '请选择收货地址', icon: 'none' })
    return
  }
  
  if (submitting.value) return
  submitting.value = true
  
  try {
    const params: any = {
      addressId: address.value.id,
      remark: remark.value
    }
    
    if (cartIds.value.length > 0) {
      params.cartIds = cartIds.value
    } else {
      params.productId = productId.value
      params.quantity = quantity.value
    }
    
    await createOrder(params)

    uni.showToast({ title: '支付成功', icon: 'success' })

    if (cartIds.value.length > 0) {
      const settledCartIds = new Set(cartIds.value)
      cartStore.cartList = cartStore.cartList.filter(item => !settledCartIds.has(item.cartId))
      cartStore.selectedIds = cartStore.selectedIds.filter(id => !settledCartIds.has(id))
    }

    setTimeout(() => {
      uni.redirectTo({
        url: '/pages/order/list'
      })
    }, 1200)
  } catch (e) {
    console.error('下单失败', e)
    uni.showToast({ title: '下单失败', icon: 'none' })
  } finally {
    submitting.value = false
  }
}

onLoad((options) => {
  if (options?.cartIds) {
    cartIds.value = options.cartIds.split(',').map(Number)
    cartStore.fetchCartList().then(() => {
      loadProducts()
    })
  } else if (options?.productId) {
    productId.value = Number(options.productId)
    quantity.value = Number(options.quantity) || 1
    loadProducts()
  }
})

onMounted(() => {
  loadDefaultAddress()
  
  // 监听地址选择返回
  uni.$on('addressSelected', (addr: Address) => {
    address.value = addr
  })
})
</script>

<style lang="scss" scoped>
.page-confirm {
  min-height: 100vh;
  padding-bottom: 120rpx;
  background-color: #f5f5f5;
}

.address-section {
  background-color: #fff;
  padding: 30rpx;
  margin-bottom: 20rpx;
  display: flex;
  align-items: center;
  position: relative;
  
  .address-content {
    flex: 1;
    
    .address-header {
      margin-bottom: 12rpx;
      
      .receiver-name {
        font-size: 32rpx;
        color: #333;
        font-weight: bold;
        margin-right: 20rpx;
      }
      
      .receiver-phone {
        font-size: 28rpx;
        color: #666;
      }
    }
    
    .address-detail {
      font-size: 26rpx;
      color: #666;
      line-height: 1.5;
    }
  }
  
  .address-empty {
    flex: 1;
    
    .empty-text {
      font-size: 28rpx;
      color: #999;
    }
  }
  
  .arrow {
    font-size: 40rpx;
    color: #999;
    margin-left: 20rpx;
  }
}

.product-section {
  background-color: #fff;
  padding: 20rpx 30rpx;
  margin-bottom: 20rpx;
  
  .product-item {
    display: flex;
    padding: 20rpx 0;
    border-bottom: 1rpx solid #eee;
    
    &:last-child {
      border-bottom: none;
    }
    
    .product-image {
      width: 160rpx;
      height: 160rpx;
      border-radius: 8rpx;
      margin-right: 20rpx;
    }
    
    .product-info {
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      
      .product-name {
        font-size: 28rpx;
        color: #333;
        display: -webkit-box;
        -webkit-box-orient: vertical;
        -webkit-line-clamp: 2;
        overflow: hidden;
      }
      
      .product-bottom {
        display: flex;
        justify-content: space-between;
        align-items: center;
        
        .product-price {
          color: #f44336;
          font-size: 28rpx;
        }
        
        .product-quantity {
          font-size: 26rpx;
          color: #999;
        }
      }
    }
  }
}

.remark-section {
  background-color: #fff;
  padding: 30rpx;
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
  
  .remark-label {
    font-size: 28rpx;
    color: #333;
    width: 100rpx;
  }
  
  .remark-input {
    flex: 1;
    font-size: 28rpx;
    color: #333;
  }
}

.amount-section {
  background-color: #fff;
  padding: 20rpx 30rpx;
  
  .amount-row {
    display: flex;
    justify-content: space-between;
    padding: 16rpx 0;
    
    .amount-label {
      font-size: 28rpx;
      color: #666;
    }
    
    .amount-value {
      font-size: 28rpx;
      color: #333;
    }
    
    &.total {
      border-top: 1rpx solid #eee;
      padding-top: 20rpx;
      margin-top: 10rpx;
      
      .amount-label {
        font-weight: bold;
        color: #333;
      }
      
      .amount-value {
        color: #f44336;
        font-weight: bold;
        font-size: 32rpx;
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
  
  .total-info {
    flex: 1;
    
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
  
  .btn-submit {
    width: 260rpx;
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
