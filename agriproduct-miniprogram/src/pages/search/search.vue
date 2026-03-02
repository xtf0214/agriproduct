<template>
  <view class="page-search">
    <!-- 搜索栏 -->
    <view class="search-bar">
      <view class="search-input-wrap">
        <text class="search-icon">🔍</text>
        <input 
          class="search-input" 
          v-model="keyword" 
          placeholder="搜索商品"
          confirm-type="search"
          @confirm="handleSearch"
        />
        <text class="clear-icon" v-if="keyword" @click="keyword = ''">✕</text>
      </view>
      <text class="cancel-btn" @click="goBack">取消</text>
    </view>
    
    <!-- 搜索历史 -->
    <view class="history-section" v-if="!hasSearched && historyList.length > 0">
      <view class="section-header">
        <text class="section-title">搜索历史</text>
        <text class="clear-btn" @click="clearHistory">清空</text>
      </view>
      <view class="history-list">
        <text 
          class="history-item" 
          v-for="(item, index) in historyList" 
          :key="index"
          @click="searchByHistory(item)"
        >
          {{ item }}
        </text>
      </view>
    </view>
    
    <!-- 搜索结果 -->
    <view class="result-section" v-if="hasSearched">
      <view class="result-header">
        <text class="result-count">共找到 {{ total }} 个商品</text>
      </view>
      
      <view class="product-list" v-if="products.length > 0">
        <view class="product-item" v-for="product in products" :key="product.id">
          <ProductCard :product="product" />
        </view>
      </view>
      
      <EmptyState v-else :show="true" text="未找到相关商品" />
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { searchProducts } from '@/api/product'
import ProductCard from '@/components/ProductCard.vue'
import EmptyState from '@/components/EmptyState.vue'
import type { Product } from '@/types'

const keyword = ref('')
const products = ref<Product[]>([])
const historyList = ref<string[]>([])
const hasSearched = ref(false)
const total = ref(0)

// 搜索商品
async function handleSearch() {
  if (!keyword.value.trim()) return
  
  saveHistory(keyword.value.trim())
  
  try {
    const list = await searchProducts(keyword.value.trim())
    products.value = list
    total.value = list.length
    hasSearched.value = true
  } catch (e) {
    console.error('搜索失败', e)
    uni.showToast({ title: '搜索失败', icon: 'none' })
  }
}

// 通过历史记录搜索
function searchByHistory(text: string) {
  keyword.value = text
  handleSearch()
}

// 保存搜索历史
function saveHistory(keyword: string) {
  let history = uni.getStorageSync('searchHistory') || []
  history = history.filter((item: string) => item !== keyword)
  history.unshift(keyword)
  if (history.length > 10) {
    history = history.slice(0, 10)
  }
  uni.setStorageSync('searchHistory', history)
  historyList.value = history
}

// 获取搜索历史
function getHistory() {
  const history = uni.getStorageSync('searchHistory') || []
  historyList.value = history
}

// 清空历史
function clearHistory() {
  uni.showModal({
    title: '提示',
    content: '确定清空搜索历史吗？',
    success: (res) => {
      if (res.confirm) {
        uni.removeStorageSync('searchHistory')
        historyList.value = []
      }
    }
  })
}

// 返回
function goBack() {
  uni.navigateBack()
}

onMounted(() => {
  getHistory()
})
</script>

<style lang="scss" scoped>
.page-search {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.search-bar {
  display: flex;
  align-items: center;
  padding: 20rpx 30rpx;
  background-color: #fff;
  
  .search-input-wrap {
    flex: 1;
    display: flex;
    align-items: center;
    background-color: #f5f5f5;
    border-radius: 40rpx;
    padding: 16rpx 24rpx;
    
    .search-icon {
      font-size: 32rpx;
      margin-right: 16rpx;
    }
    
    .search-input {
      flex: 1;
      font-size: 28rpx;
      color: #333;
    }
    
    .clear-icon {
      font-size: 32rpx;
      color: #999;
      margin-left: 16rpx;
    }
  }
  
  .cancel-btn {
    font-size: 28rpx;
    color: #666;
    margin-left: 20rpx;
  }
}

.history-section {
  background-color: #fff;
  margin-top: 20rpx;
  padding: 30rpx;
  
  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24rpx;
    
    .section-title {
      font-size: 30rpx;
      color: #333;
      font-weight: bold;
    }
    
    .clear-btn {
      font-size: 26rpx;
      color: #999;
    }
  }
  
  .history-list {
    display: flex;
    flex-wrap: wrap;
    
    .history-item {
      padding: 12rpx 24rpx;
      background-color: #f5f5f5;
      border-radius: 30rpx;
      font-size: 26rpx;
      color: #666;
      margin-right: 20rpx;
      margin-bottom: 20rpx;
    }
  }
}

.result-section {
  .result-header {
    padding: 20rpx 30rpx;
    
    .result-count {
      font-size: 26rpx;
      color: #999;
    }
  }
  
  .product-list {
    display: flex;
    flex-wrap: wrap;
    padding: 0 10rpx;
    
    .product-item {
      width: 50%;
      padding: 10rpx;
      box-sizing: border-box;
    }
  }
}
</style>
