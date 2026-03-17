<template>
  <view class="page-category">
    <!-- 搜索栏 -->
    <view class="search-bar" @click="goSearch">
      <view class="search-input">
        <text class="icon-search">🔍</text>
        <text class="placeholder">搜索商品</text>
      </view>
    </view>
    
    <view class="category-content">
      <!-- 左侧分类 -->
      <scroll-view class="category-left" scroll-y>
        <view 
          class="category-item" 
          :class="{ active: currentCategoryId === 0 }"
          @click="selectCategory(0)"
        >
          全部分类
        </view>
        <view 
          class="category-item" 
          :class="{ active: currentCategoryId === item.id }"
          v-for="item in categoryTree" 
          :key="item.id"
          @click="selectCategory(item.id)"
        >
          {{ item.name }}
        </view>
      </scroll-view>
      
      <!-- 右侧商品 -->
      <scroll-view 
        class="category-right" 
        scroll-y 
        @scrolltolower="loadMore"
      >
        <!-- 子分类 -->
        <view class="sub-categories" v-if="currentCategory?.children?.length">
          <view 
            class="sub-item" 
            :class="{ active: currentSubId === sub.id }"
            v-for="sub in currentCategory.children" 
            :key="sub.id"
            @click="selectSubCategory(sub.id)"
          >
            {{ sub.name }}
          </view>
        </view>
        
        <!-- 商品列表 -->
        <view class="product-list">
          <view class="product-item" v-for="product in products" :key="product.id">
            <ProductCard :product="product" />
          </view>
        </view>
        
        <!-- 加载更多 -->
        <LoadMore :status="loadStatus" />
        
        <!-- 空状态 -->
        <EmptyState v-if="!loading && products.length === 0" :show="true" text="暂无商品" />
      </scroll-view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { getCategoryTree } from '@/api/home'
import { getProductList } from '@/api/product'
import ProductCard from '@/components/ProductCard.vue'
import EmptyState from '@/components/EmptyState.vue'
import LoadMore from '@/components/LoadMore.vue'
import type { Category, Product, PageResponse } from '@/types'

const categoryTree = ref<Category[]>([])
const currentCategoryId = ref(0)
const currentSubId = ref(0)
const products = ref<Product[]>([])
const loading = ref(false)
const loadStatus = ref<'loading' | 'nomore' | 'more'>('more')
const pageNum = ref(1)
const pageSize = 10
const total = ref(0)

// 当前选中的分类
const currentCategory = computed(() => {
  return categoryTree.value.find(c => c.id === currentCategoryId.value)
})

// 获取分类树
async function fetchCategoryTree() {
  try {
    const tree = await getCategoryTree()
    categoryTree.value = tree
  } catch (e) {
    console.error('获取分类失败', e)
  }
}

// 获取商品列表
async function fetchProducts(reset = false) {
  if (loading.value) return
  if (reset) {
    pageNum.value = 1
    products.value = []
    loadStatus.value = 'more'
  }
  
  loading.value = true
  loadStatus.value = 'loading'
  
  try {
    // 如果选择了二级分类，则只查询该二级分类的商品
    // 如果只选择了一级分类（没有选择二级分类），则查询该一级分类及其所有子分类的商品
    const categoryId = currentSubId.value || currentCategoryId.value || undefined
    const res: PageResponse<Product> = await getProductList({
      pageNum: pageNum.value,
      pageSize,
      categoryId: categoryId || undefined
    })
    
    if (reset) {
      products.value = res.records
    } else {
      products.value = [...products.value, ...res.records]
    }
    total.value = res.total
    
    // 判断是否还有更多
    if (products.value.length >= res.total) {
      loadStatus.value = 'nomore'
    } else {
      loadStatus.value = 'more'
    }
  } catch (e) {
    console.error('获取商品失败', e)
    loadStatus.value = 'more'
  } finally {
    loading.value = false
  }
}

// 选择分类
function selectCategory(id: number) {
  currentCategoryId.value = id
  currentSubId.value = 0
  fetchProducts(true)
}

// 选择子分类（点击后只显示该子分类的商品）
function selectSubCategory(id: number) {
  currentSubId.value = id
  fetchProducts(true)
}

// 加载更多
function loadMore() {
  if (loadStatus.value === 'nomore' || loading.value) return
  pageNum.value++
  fetchProducts()
}

// 跳转搜索页
function goSearch() {
  uni.navigateTo({ url: '/pages/search/search' })
}

onMounted(() => {
  fetchCategoryTree()
  fetchProducts(true)
})
</script>

<style lang="scss" scoped>
.page-category {
  height: 100vh;
  display: flex;
  flex-direction: column;
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

.category-content {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.category-left {
  width: 180rpx;
  background-color: #fff;
  height: 100%;
  
  .category-item {
    padding: 30rpx 20rpx;
    text-align: center;
    font-size: 26rpx;
    color: #333;
    border-left: 6rpx solid transparent;
    
    &.active {
      background-color: #f5f5f5;
      border-left-color: #4CAF50;
      color: #4CAF50;
      font-weight: bold;
    }
  }
}

.category-right {
  flex: 1;
  height: 100%;
  background-color: #f5f5f5;
}

.sub-categories {
  display: flex;
  flex-wrap: wrap;
  padding: 20rpx;
  background-color: #fff;
  margin-bottom: 20rpx;
  
  .sub-item {
    padding: 12rpx 24rpx;
    margin: 10rpx;
    background-color: #f5f5f5;
    border-radius: 30rpx;
    font-size: 24rpx;
    color: #666;
    
    &.active {
      background-color: #4CAF50;
      color: #fff;
    }
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
</style>
