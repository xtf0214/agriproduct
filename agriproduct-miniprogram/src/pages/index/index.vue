<template>
  <view class="page-index">
    <!-- 搜索栏 -->
    <view class="search-bar" @click="goSearch">
      <view class="search-input">
        <text class="icon-search">🔍</text>
        <text class="placeholder">搜索商品</text>
      </view>
    </view>
    
    <!-- 轮播图 -->
    <swiper class="banner-swiper" indicator-dots autoplay circular>
      <swiper-item v-for="banner in banners" :key="banner.id" @click="handleBannerClick(banner)">
        <image class="banner-image" :src="banner.imageUrl" mode="aspectFill" />
      </swiper-item>
    </swiper>
    
    <!-- 分类导航 -->
    <view class="category-nav">
      <view class="category-item" v-for="cat in categories" :key="cat.id" @click="goCategory(cat.id)">
        <image class="category-icon" :src="cat.icon || '/static/default-icon.png'" mode="aspectFit" />
        <text class="category-name">{{ cat.name }}</text>
      </view>
    </view>
    
    <!-- 推荐商品 -->
    <view class="recommend-section">
      <view class="section-header">
        <text class="section-title">热门推荐</text>
      </view>
      <view class="product-list">
        <view class="product-item" v-for="product in recommends" :key="product.id">
          <ProductCard :product="product" />
        </view>
      </view>
    </view>
    
    <!-- 空状态 -->
    <EmptyState v-if="!loading && recommends.length === 0" :show="true" text="暂无推荐商品" />
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { onPullDownRefresh } from '@dcloudio/uni-app'
import { getBanners, getCategories, getRecommends } from '@/api/home'
import ProductCard from '@/components/ProductCard.vue'
import EmptyState from '@/components/EmptyState.vue'
import type { Banner, Category, Product } from '@/types'

const loading = ref(false)
const banners = ref<Banner[]>([])
const categories = ref<Category[]>([])
const recommends = ref<Product[]>([])

// 获取首页数据
async function fetchHomeData() {
  loading.value = true
  try {
    console.log('开始获取首页数据...')
    
    const [bannerList, categoryList, recommendList] = await Promise.all([
      getBanners(),
      getCategories(),
      getRecommends(10)
    ])
    
    console.log('轮播图数据:', bannerList)
    console.log('分类数据:', categoryList)
    console.log('推荐商品数据:', recommendList)
    
    // 数据验证和转换
    banners.value = Array.isArray(bannerList) ? bannerList : []
    categories.value = Array.isArray(categoryList) ? categoryList.slice(0, 8) : []
    recommends.value = Array.isArray(recommendList) ? recommendList : []
    
    console.log('处理后的数据:')
    console.log('- 轮播图数量:', banners.value.length)
    console.log('- 分类数量:', categories.value.length)
    console.log('- 推荐商品数量:', recommends.value.length)
    console.log('首页数据加载成功')
  } catch (e) {
    console.error('获取首页数据失败', e)
  } finally {
    loading.value = false
  }
}

// 轮播图点击
function handleBannerClick(banner: Banner) {
  if (banner.linkUrl) {
    uni.navigateTo({ url: banner.linkUrl })
  }
}

// 跳转搜索页
function goSearch() {
  uni.navigateTo({ url: '/pages/search/search' })
}

// 跳转分类页
function goCategory(categoryId: number) {
  uni.switchTab({ url: '/pages/category/category' })
}

// 下拉刷新
onPullDownRefresh(async () => {
  await fetchHomeData()
  uni.stopPullDownRefresh()
})

onMounted(() => {
  fetchHomeData()
})
</script>

<style lang="scss" scoped>
.page-index {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.search-bar {
  background-color: #4CAF50;
  padding: 20rpx 30rpx;
  
  .search-input {
    background-color: #fff;
    border-radius: 40rpx;
    padding: 16rpx 30rpx;
    display: flex;
    align-items: center;
    
    .icon-search {
      margin-right: 16rpx;
    }
    
    .placeholder {
      color: #999;
      font-size: 28rpx;
    }
  }
}

.banner-swiper {
  width: 100%;
  height: 300rpx;
  
  .banner-image {
    width: 100%;
    height: 100%;
  }
}

.category-nav {
  background-color: #fff;
  display: flex;
  flex-wrap: wrap;
  padding: 30rpx 20rpx;
  
  .category-item {
    width: 25%;
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: 20rpx;
    
    .category-icon {
      width: 88rpx;
      height: 88rpx;
      margin-bottom: 12rpx;
    }
    
    .category-name {
      font-size: 24rpx;
      color: #333;
    }
  }
}

.recommend-section {
  margin-top: 20rpx;
  
  .section-header {
    background-color: #fff;
    padding: 24rpx 30rpx;
    
    .section-title {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
    }
  }
  
  .product-list {
    display: flex;
    flex-wrap: wrap;
    padding: 10rpx;
    
    .product-item {
      width: 50%;
      padding: 10rpx;
      box-sizing: border-box;
    }
  }
}
</style>
