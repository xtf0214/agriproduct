<template>
  <view class="page-order-detail" v-if="order">
    <!-- 订单状态 -->
    <view class="status-section">
      <text class="status-text">{{ getStatusText(order.status) }}</text>
      <text class="status-desc">{{ getStatusDesc(order.status) }}</text>
    </view>
    
    <!-- 收货地址 -->
    <view class="address-section">
      <view class="address-icon">📍</view>
      <view class="address-info">
        <view class="address-header">
          <text class="receiver-name">{{ order.receiverName }}</text>
          <text class="receiver-phone">{{ order.receiverPhone }}</text>
        </view>
        <text class="address-detail">{{ order.receiverAddress }}</text>
      </view>
    </view>
    
    <!-- 商品列表 -->
    <view class="product-section">
      <view class="product-item" v-for="item in order.items" :key="item.id">
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
    
    <!-- 订单信息 -->
    <view class="info-section">
      <view class="info-row">
        <text class="info-label">订单编号</text>
        <text class="info-value">{{ order.orderNo }}</text>
        <text class="copy-btn" @click="copyOrderNo">复制</text>
      </view>
      <view class="info-row">
        <text class="info-label">下单时间</text>
        <text class="info-value">{{ order.createTime }}</text>
      </view>
      <view class="info-row" v-if="order.payTime">
        <text class="info-label">付款时间</text>
        <text class="info-value">{{ order.payTime }}</text>
      </view>
      <view class="info-row" v-if="order.shipTime">
        <text class="info-label">发货时间</text>
        <text class="info-value">{{ order.shipTime }}</text>
      </view>
      <view class="info-row" v-if="order.receiveTime">
        <text class="info-label">收货时间</text>
        <text class="info-value">{{ order.receiveTime }}</text>
      </view>
      <view class="info-row" v-if="order.remark">
        <text class="info-label">订单备注</text>
        <text class="info-value">{{ order.remark }}</text>
      </view>
    </view>
    
    <!-- 金额信息 -->
    <view class="amount-section">
      <view class="amount-row">
        <text class="amount-label">商品金额</text>
        <text class="amount-value">¥{{ order.totalAmount.toFixed(2) }}</text>
      </view>
      <view class="amount-row">
        <text class="amount-label">运费</text>
        <text class="amount-value">¥0.00</text>
      </view>
      <view class="amount-row total">
        <text class="amount-label">实付款</text>
        <text class="amount-value">¥{{ order.totalAmount.toFixed(2) }}</text>
      </view>
    </view>
    
    <!-- 底部操作栏 -->
    <view class="bottom-bar">
      <button 
        v-if="order.status === 1" 
        class="btn-action"
        @click="handleCancel"
      >
        取消订单
      </button>
      <button 
        v-if="order.status === 3" 
        class="btn-action primary"
        @click="handleReceive"
      >
        确认收货
      </button>
      <button 
        v-if="order.status === 1" 
        class="btn-action primary"
        @click="handlePay"
      >
        立即付款
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getOrderDetail, cancelOrder, confirmReceive } from '@/api/order'
import type { Order, OrderStatus } from '@/types'

const orderId = ref('')
const order = ref<Order | null>(null)

// 获取状态文本
function getStatusText(status: OrderStatus): string {
  const texts: Record<number, string> = {
    1: '待付款',
    2: '待发货',
    3: '待收货',
    4: '已完成',
    5: '已取消'
  }
  return texts[status] || '未知'
}

// 获取状态描述
function getStatusDesc(status: OrderStatus): string {
  const descs: Record<number, string> = {
    1: '请在30分钟内完成付款',
    2: '商家正在为您准备商品',
    3: '商品正在配送中',
    4: '交易已完成',
    5: '订单已取消'
  }
  return descs[status] || ''
}

// 获取订单详情
async function fetchOrderDetail() {
  if (!orderId.value) return
  try {
    const detail = await getOrderDetail(orderId.value)
    order.value = detail
  } catch (e) {
    console.error('获取订单详情失败', e)
    uni.showToast({ title: '获取订单失败', icon: 'none' })
  }
}

// 复制订单号
function copyOrderNo() {
  if (!order.value) return
  uni.setClipboardData({
    data: order.value.orderNo,
    success: () => {
      uni.showToast({ title: '已复制', icon: 'success' })
    }
  })
}

// 取消订单
function handleCancel() {
  uni.showModal({
    title: '提示',
    content: '确定取消该订单吗？',
    success: async (res) => {
      if (res.confirm && order.value) {
        try {
          await cancelOrder(order.value.id)
          uni.showToast({ title: '已取消', icon: 'success' })
          fetchOrderDetail()
        } catch (e) {
          uni.showToast({ title: '取消失败', icon: 'none' })
        }
      }
    }
  })
}

// 确认收货
function handleReceive() {
  uni.showModal({
    title: '提示',
    content: '确认已收到货物吗？',
    success: async (res) => {
      if (res.confirm && order.value) {
        try {
          await confirmReceive(order.value.id)
          uni.showToast({ title: '已确认收货', icon: 'success' })
          fetchOrderDetail()
        } catch (e) {
          uni.showToast({ title: '操作失败', icon: 'none' })
        }
      }
    }
  })
}

// 去付款
function handlePay() {
  uni.showToast({ title: '支付功能待实现', icon: 'none' })
}

onLoad((options) => {
  if (options?.orderId) {
    orderId.value = options.orderId
    fetchOrderDetail()
  }
})
</script>

<style lang="scss" scoped>
.page-order-detail {
  min-height: 100vh;
  padding-bottom: 120rpx;
  background-color: #f5f5f5;
}

.status-section {
  background-color: #4CAF50;
  padding: 40rpx 30rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  
  .status-text {
    font-size: 40rpx;
    color: #fff;
    font-weight: bold;
  }
  
  .status-desc {
    font-size: 26rpx;
    color: rgba(255, 255, 255, 0.8);
    margin-top: 12rpx;
  }
}

.address-section {
  background-color: #fff;
  padding: 30rpx;
  margin-bottom: 20rpx;
  display: flex;
  align-items: flex-start;
  
  .address-icon {
    font-size: 40rpx;
    margin-right: 20rpx;
  }
  
  .address-info {
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
    }
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
        
        .product-price {
          font-size: 28rpx;
          color: #f44336;
        }
        
        .product-quantity {
          font-size: 26rpx;
          color: #999;
        }
      }
    }
  }
}

.info-section {
  background-color: #fff;
  padding: 10rpx 30rpx;
  margin-bottom: 20rpx;
  
  .info-row {
    display: flex;
    align-items: center;
    padding: 20rpx 0;
    border-bottom: 1rpx solid #f5f5f5;
    
    &:last-child {
      border-bottom: none;
    }
    
    .info-label {
      font-size: 28rpx;
      color: #999;
      width: 160rpx;
    }
    
    .info-value {
      flex: 1;
      font-size: 28rpx;
      color: #333;
    }
    
    .copy-btn {
      font-size: 24rpx;
      color: #4CAF50;
      padding: 8rpx 20rpx;
      border: 1rpx solid #4CAF50;
      border-radius: 20rpx;
    }
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
  justify-content: flex-end;
  padding: 0 20rpx;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
  
  .btn-action {
    padding: 0 40rpx;
    height: 72rpx;
    line-height: 72rpx;
    font-size: 28rpx;
    border-radius: 36rpx;
    margin-left: 20rpx;
    background-color: #fff;
    border: 1rpx solid #ddd;
    color: #666;
    
    &::after {
      border: none;
    }
    
    &.primary {
      background-color: #4CAF50;
      border-color: #4CAF50;
      color: #fff;
    }
  }
}
</style>
