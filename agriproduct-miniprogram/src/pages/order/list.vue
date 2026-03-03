<template>
  <view class="page-order-list">
    <!-- 状态筛选 -->
    <scroll-view class="status-tabs" scroll-x>
      <view 
        class="tab-item" 
        :class="{ active: currentStatus === null }"
        @click="selectStatus(null)"
      >
        全部
      </view>
      <view 
        class="tab-item" 
        :class="{ active: currentStatus === 1 }"
        @click="selectStatus(1)"
      >
        待付款
      </view>
      <view 
        class="tab-item" 
        :class="{ active: currentStatus === 2 }"
        @click="selectStatus(2)"
      >
        待发货
      </view>
      <view 
        class="tab-item" 
        :class="{ active: currentStatus === 3 }"
        @click="selectStatus(3)"
      >
        待收货
      </view>
      <view 
        class="tab-item" 
        :class="{ active: currentStatus === 4 }"
        @click="selectStatus(4)"
      >
       已完成
      </view>
    </scroll-view>
    
    <!-- 订单列表 -->
    <scroll-view 
      class="order-list" 
      scroll-y 
      @scrolltolower="loadMore"
      refresher-enabled
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
    >
      <view class="order-item" v-for="order in orders" :key="order.id" @click="goDetail(order.id)">
        <view class="order-header">
          <text class="order-no">订单号: {{ order.orderNo }}</text>
          <text class="order-status" :class="getStatusClass(order.status)">
            {{ getStatusText(order.status) }}
          </text>
        </view>
        
        <view class="order-products">
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
        
        <view class="order-footer">
          <view class="order-total">
            <text class="total-label">共{{ getTotalQuantity(order) }}件商品 实付</text>
            <text class="total-price">¥{{ order.totalAmount.toFixed(2) }}</text>
          </view>
          <view class="order-actions">
            <button 
              v-if="order.status === 1" 
              class="btn-action" 
              @click.stop="handleCancel(order.id)"
            >
              取消订单
            </button>
            <button 
              v-if="order.status === 3" 
              class="btn-action primary"
              @click.stop="handleReceive(order.id)"
            >
              确认收货
            </button>
            <button 
              v-if="order.status === 1" 
              class="btn-action primary"
              @click.stop="handlePay(order.id)"
            >
              立即付款
            </button>
          </view>
        </view>
      </view>
      
      <LoadMore :status="loadStatus" />
      
      <EmptyState 
        v-if="!loading && orders.length === 0" 
        :show="true" 
        text="暂无订单"
        buttonText="去逛逛"
        @action="goShopping"
      />
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { onPullDownRefresh } from '@dcloudio/uni-app'
import { getOrderList, cancelOrder, confirmReceive, payOrder } from '@/api/order'
import { useUserStore } from '@/stores/user'
import EmptyState from '@/components/EmptyState.vue'
import LoadMore from '@/components/LoadMore.vue'
import type { Order, OrderStatus } from '@/types'

const userStore = useUserStore()

const currentStatus = ref<number | null>(null)
const orders = ref<Order[]>([])
const loading = ref(false)
const refreshing = ref(false)
const loadStatus = ref<'loading' | 'nomore' | 'more'>('more')
const pageNum = ref(1)
const pageSize = 10

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

// 获取状态样式类
function getStatusClass(status: OrderStatus): string {
  const classes: Record<number, string> = {
    1: 'pending',
    2: 'paid',
    3: 'shipped',
    4: 'completed',
    5: 'cancelled'
  }
  return classes[status] || ''
}

// 获取订单总数量
function getTotalQuantity(order: Order): number {
  return order.items.reduce((sum, item) => sum + item.quantity, 0)
}

// 获取订单列表
async function fetchOrders(reset = false) {
  if (loading.value) return
  if (reset) {
    pageNum.value = 1
    orders.value = []
    loadStatus.value = 'more'
  }
  
  loading.value = true
  loadStatus.value = 'loading'
  
  try {
    const params: any = {
      pageNum: pageNum.value,
      pageSize
    }
    if (currentStatus.value !== null) {
      params.status = currentStatus.value
    }
    
    const res = await getOrderList(params)
    
    if (reset) {
      orders.value = res.records
    } else {
      orders.value = [...orders.value, ...res.records]
    }
    
    if (orders.value.length >= res.total) {
      loadStatus.value = 'nomore'
    } else {
      loadStatus.value = 'more'
    }
  } catch (e) {
    console.error('获取订单失败', e)
    loadStatus.value = 'more'
  } finally {
    loading.value = false
    refreshing.value = false
  }
}

// 选择状态
function selectStatus(status: number | null) {
  currentStatus.value = status
  fetchOrders(true)
}

// 加载更多
function loadMore() {
  if (loadStatus.value === 'nomore' || loading.value) return
  pageNum.value++
  fetchOrders()
}

// 下拉刷新
async function onRefresh() {
  refreshing.value = true
  await fetchOrders(true)
}

// 取消订单
function handleCancel(orderId: number) {
  uni.showModal({
    title: '提示',
    content: '确定取消该订单吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          await cancelOrder(orderId)
          uni.showToast({ title: '已取消', icon: 'success' })
          fetchOrders(true)
        } catch (e) {
          uni.showToast({ title: '取消失败', icon: 'none' })
        }
      }
    }
  })
}

// 确认收货
function handleReceive(orderId: number) {
  uni.showModal({
    title: '提示',
    content: '确认已收到货物吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          await confirmReceive(orderId)
          uni.showToast({ title: '已确认收货', icon: 'success' })
          fetchOrders(true)
        } catch (e) {
          uni.showToast({ title: '操作失败', icon: 'none' })
        }
      }
    }
  })
}

// 去付款
function handlePay(orderId: number) {
  const order = orders.value.find(o => o.id === orderId)
  uni.showModal({
    title: '确认支付',
    content: `确定支付 ¥${order?.totalAmount.toFixed(2)} 吗？`,
    success: async (res) => {
      if (res.confirm) {
        try {
          uni.showLoading({ title: '支付中...' })
          await payOrder(orderId)
          uni.hideLoading()
          uni.showToast({ title: '支付成功', icon: 'success' })
          fetchOrders(true)
        } catch (e) {
          uni.hideLoading()
          uni.showToast({ title: '支付失败', icon: 'none' })
        }
      }
    }
  })
}

// 跳转详情
function goDetail(orderId: number) {
  uni.navigateTo({
    url: `/pages/order/detail?orderId=${orderId}`
  })
}

// 去购物
function goShopping() {
  uni.switchTab({ url: '/pages/index/index' })
}

onPullDownRefresh(async () => {
  await fetchOrders(true)
  uni.stopPullDownRefresh()
})

onMounted(() => {
  if (userStore.isLoggedIn) {
    fetchOrders(true)
  }
})
</script>

<style lang="scss" scoped>
.page-order-list {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f5f5;
}

.status-tabs {
  background-color: #fff;
  white-space: nowrap;
  border-bottom: 1rpx solid #eee;
  
  .tab-item {
    display: inline-block;
    padding: 24rpx 40rpx;
    font-size: 28rpx;
    color: #666;
    
    &.active {
      color: #4CAF50;
      font-weight: bold;
      border-bottom: 4rpx solid #4CAF50;
    }
  }
}

.order-list {
  flex: 1;
  padding: 20rpx;
}

.order-item {
  background-color: #fff;
  border-radius: 16rpx;
  margin-bottom: 20rpx;
  overflow: hidden;
  
  .order-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20rpx 24rpx;
    border-bottom: 1rpx solid #eee;
    
    .order-no {
      font-size: 24rpx;
      color: #999;
    }
    
    .order-status {
      font-size: 26rpx;
      font-weight: bold;
      
      &.pending { color: #f44336; }
      &.paid { color: #FF9800; }
      &.shipped { color: #2196F3; }
      &.completed { color: #4CAF50; }
      &.cancelled { color: #999; }
    }
  }
  
  .order-products {
    padding: 20rpx 24rpx;
    
    .product-item {
      display: flex;
      padding: 16rpx 0;
      border-bottom: 1rpx solid #f5f5f5;
      
      &:last-child {
        border-bottom: none;
      }
      
      .product-image {
        width: 140rpx;
        height: 140rpx;
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
  
  .order-footer {
    padding: 20rpx 24rpx;
    border-top: 1rpx solid #eee;
    
    .order-total {
      display: flex;
      justify-content: flex-end;
      align-items: center;
      margin-bottom: 16rpx;
      
      .total-label {
        font-size: 24rpx;
        color: #666;
      }
      
      .total-price {
        font-size: 32rpx;
        color: #f44336;
        font-weight: bold;
        margin-left: 8rpx;
      }
    }
    
    .order-actions {
      display: flex;
      justify-content: flex-end;
      
      .btn-action {
        padding: 12rpx 30rpx;
        font-size: 26rpx;
        border-radius: 30rpx;
        margin-left: 16rpx;
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
  }
}
</style>
