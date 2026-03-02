<template>
  <view class="page-address">
    <view class="address-list" v-if="addresses.length > 0">
      <view 
        class="address-item" 
        v-for="addr in addresses" 
        :key="addr.id"
        @click="handleSelect(addr)"
      >
        <view class="address-content">
          <view class="address-header">
            <text class="receiver-name">{{ addr.receiverName }}</text>
            <text class="receiver-phone">{{ addr.receiverPhone }}</text>
            <text class="default-tag" v-if="addr.isDefault === 1">默认</text>
          </view>
          <text class="address-detail">
            {{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detailAddress }}
          </text>
        </view>
        <view class="address-actions">
          <view class="action-item" @click.stop="handleEdit(addr)">
            <text class="action-icon">✏️</text>
            <text class="action-text">编辑</text>
          </view>
          <view class="action-item" @click.stop="handleDelete(addr.id)">
            <text class="action-icon">🗑️</text>
            <text class="action-text">删除</text>
          </view>
        </view>
      </view>
    </view>
    
    <EmptyState 
      v-else 
      :show="true" 
      text="暂无收货地址"
      buttonText="添加地址"
      @action="handleAdd"
    />
    
    <!-- 添加按钮 -->
    <view class="add-btn-wrap" v-if="addresses.length > 0">
      <button class="add-btn" @click="handleAdd">
        <text class="add-icon">+</text>
        添加收货地址
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getAddressList, deleteAddress } from '@/api/address'
import { useUserStore } from '@/stores/user'
import EmptyState from '@/components/EmptyState.vue'
import type { Address } from '@/types'

const userStore = useUserStore()
const addresses = ref<Address[]>([])
const isSelectMode = ref(false)

// 获取地址列表
async function fetchAddresses() {
  if (!userStore.isLoggedIn) return
  try {
    const list = await getAddressList()
    addresses.value = list
  } catch (e) {
    console.error('获取地址失败', e)
  }
}

// 选择地址（从订单页返回）
function handleSelect(addr: Address) {
  if (isSelectMode.value) {
    uni.$emit('addressSelected', addr)
    uni.navigateBack()
  }
}

// 添加地址
function handleAdd() {
  uni.navigateTo({ url: '/pages/address/edit' })
}

// 编辑地址
function handleEdit(addr: Address) {
  uni.navigateTo({ url: `/pages/address/edit?id=${addr.id}` })
}

// 删除地址
function handleDelete(id: number) {
  uni.showModal({
    title: '提示',
    content: '确定删除该地址吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          await deleteAddress(id)
          uni.showToast({ title: '已删除', icon: 'success' })
          fetchAddresses()
        } catch (e) {
          uni.showToast({ title: '删除失败', icon: 'none' })
        }
      }
    }
  })
}

onLoad((options) => {
  isSelectMode.value = options?.select === '1'
})

onMounted(() => {
  fetchAddresses()
  
  // 监听地址编辑完成事件
  uni.$on('addressUpdated', () => {
    fetchAddresses()
  })
})
</script>

<style lang="scss" scoped>
.page-address {
  min-height: 100vh;
  padding-bottom: 120rpx;
  background-color: #f5f5f5;
}

.address-list {
  padding: 20rpx;
  
  .address-item {
    background-color: #fff;
    border-radius: 16rpx;
    padding: 24rpx;
    margin-bottom: 20rpx;
    
    .address-content {
      .address-header {
        display: flex;
        align-items: center;
        margin-bottom: 16rpx;
        
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
        
        .default-tag {
          margin-left: 16rpx;
          padding: 4rpx 16rpx;
          background-color: #4CAF50;
          color: #fff;
          font-size: 22rpx;
          border-radius: 20rpx;
        }
      }
      
      .address-detail {
        font-size: 26rpx;
        color: #666;
        line-height: 1.5;
      }
    }
    
    .address-actions {
      display: flex;
      justify-content: flex-end;
      padding-top: 20rpx;
      margin-top: 20rpx;
      border-top: 1rpx solid #eee;
      
      .action-item {
        display: flex;
        align-items: center;
        margin-left: 40rpx;
        
        .action-icon {
          font-size: 32rpx;
          margin-right: 8rpx;
        }
        
        .action-text {
          font-size: 26rpx;
          color: #666;
        }
      }
    }
  }
}

.add-btn-wrap {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20rpx 30rpx 40rpx;
  background-color: #fff;
  
  .add-btn {
    width: 100%;
    height: 88rpx;
    line-height: 88rpx;
    background-color: #4CAF50;
    color: #fff;
    border-radius: 44rpx;
    font-size: 32rpx;
    border: none;
    display: flex;
    align-items: center;
    justify-content: center;
    
    &::after {
      border: none;
    }
    
    .add-icon {
      font-size: 36rpx;
      margin-right: 12rpx;
    }
  }
}
</style>
