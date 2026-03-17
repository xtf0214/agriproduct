<template>
  <view class="page-mine">
    <!-- 用户信息 -->
    <view class="user-section">
      <view class="user-info" v-if="isLoggedIn" @click="goProfile">
        <image class="user-avatar" :src="userInfo?.avatar || '/static/default-avatar.png'" mode="aspectFill" />
        <view class="user-detail">
          <text class="user-name">{{ userInfo?.nickname || userInfo?.username }}</text>
          <text class="user-phone">{{ userInfo?.phone }}</text>
        </view>
      </view>
      <view class="user-info" v-else @click="goLogin">
        <image class="user-avatar" src="/static/default-avatar.png" mode="aspectFill" />
        <view class="user-detail">
          <text class="user-name">点击登录</text>
          <text class="user-phone">登录后享受更多服务</text>
        </view>
      </view>
    </view>
    
    <!-- 订单入口 -->
    <view class="order-section">
      <view class="section-header">
        <text class="section-title">我的订单</text>
        <view class="section-more" @click="goOrders()">
          <text class="more-text">全部订单</text>
          <text class="more-arrow">›</text>
        </view>
      </view>
      <view class="order-nav">
        <view class="nav-item" @click="goOrders(0)">
          <text class="nav-icon">💰</text>
          <text class="nav-text">待付款</text>
        </view>
        <view class="nav-item" @click="goOrders(1)">
          <text class="nav-icon">📦</text>
          <text class="nav-text">待发货</text>
        </view>
        <view class="nav-item" @click="goOrders(2)">
          <text class="nav-icon">🚚</text>
          <text class="nav-text">待收货</text>
        </view>
        <view class="nav-item" @click="goOrders(3)">
          <text class="nav-icon">✅</text>
          <text class="nav-text">已完成</text>
        </view>
      </view>
    </view>
    
    <!-- 功能列表 -->
    <view class="menu-section">
      <view class="menu-item" @click="goAddress">
        <text class="menu-icon">📍</text>
        <text class="menu-text">收货地址</text>
        <text class="menu-arrow">›</text>
      </view>
    </view>
    
    <!-- 退出登录 -->
    <button v-if="isLoggedIn" class="logout-btn" @click="handleLogout">
      退出登录
    </button>
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const isLoggedIn = computed(() => userStore.isLoggedIn)
const userInfo = computed(() => userStore.userInfo)

// 跳转登录
function goLogin() {
  uni.navigateTo({ url: '/pages/login/login' })
}

// 跳转个人信息
function goProfile() {
  uni.showToast({ title: '个人信息编辑待开发', icon: 'none' })
}

// 跳转订单列表
function goOrders(status?: number) {
  if (!userStore.checkLogin()) return
  
  let url = '/pages/order/list'
  if (status !== undefined) {
    url += `?status=${status}`
  }
  uni.navigateTo({ url })
}

// 跳转地址管理
function goAddress() {
  if (!userStore.checkLogin()) return
  uni.navigateTo({ url: '/pages/address/list' })
}

// 意见反馈
function handleFeedback() {
  uni.showToast({ title: '意见反馈待开发', icon: 'none' })
}

// 关于我们
function handleAbout() {
  uni.showToast({ title: '关于我们待开发', icon: 'none' })
}

// 退出登录
function handleLogout() {
  uni.showModal({
    title: '提示',
    content: '确定退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        userStore.logout()
        uni.showToast({ title: '已退出登录', icon: 'success' })
      }
    }
  })
}

onShow(() => {
  if (userStore.isLoggedIn) {
    userStore.fetchUserInfo()
  }
})
</script>

<style lang="scss" scoped>
.page-mine {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.user-section {
  background-color: #4CAF50;
  padding: 60rpx 30rpx 40rpx;
  
  .user-info {
    display: flex;
    align-items: center;
    
    .user-avatar {
      width: 120rpx;
      height: 120rpx;
      border-radius: 50%;
      border: 4rpx solid rgba(255, 255, 255, 0.5);
    }
    
    .user-detail {
      margin-left: 30rpx;
      
      .user-name {
        font-size: 36rpx;
        color: #fff;
        font-weight: bold;
        display: block;
        margin-bottom: 10rpx;
      }
      
      .user-phone {
        font-size: 26rpx;
        color: rgba(255, 255, 255, 0.8);
      }
    }
  }
}

.order-section {
  background-color: #fff;
  margin-bottom: 20rpx;
  
  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 30rpx;
    border-bottom: 1rpx solid #eee;
    
    .section-title {
      font-size: 30rpx;
      color: #333;
      font-weight: bold;
    }
    
    .section-more {
      display: flex;
      align-items: center;
      
      .more-text {
        font-size: 26rpx;
        color: #999;
      }
      
      .more-arrow {
        font-size: 36rpx;
        color: #999;
        margin-left: 8rpx;
      }
    }
  }
  
  .order-nav {
    display: flex;
    padding: 30rpx 0;
    
    .nav-item {
      flex: 1;
      display: flex;
      flex-direction: column;
      align-items: center;
      
      .nav-icon {
        font-size: 48rpx;
        margin-bottom: 12rpx;
      }
      
      .nav-text {
        font-size: 26rpx;
        color: #333;
      }
    }
  }
}

.menu-section {
  background-color: #fff;
  
  .menu-item {
    display: flex;
    align-items: center;
    padding: 30rpx;
    border-bottom: 1rpx solid #eee;
    
    &:last-child {
      border-bottom: none;
    }
    
    .menu-icon {
      font-size: 40rpx;
      margin-right: 20rpx;
    }
    
    .menu-text {
      flex: 1;
      font-size: 30rpx;
      color: #333;
    }
    
    .menu-arrow {
      font-size: 36rpx;
      color: #999;
    }
  }
}

.logout-btn {
  margin: 60rpx 30rpx;
  height: 88rpx;
  line-height: 88rpx;
  background-color: #fff;
  color: #f44336;
  border-radius: 44rpx;
  font-size: 32rpx;
  border: 1rpx solid #f44336;
  
  &::after {
    border: none;
  }
}
</style>
