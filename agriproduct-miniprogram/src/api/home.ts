import { get } from '@/utils/request'
import type { Banner, Category, Product, HomeData } from '@/types'

// 获取首页数据
export function getHomeData() {
  return get<HomeData>('/api/home')
}

// 获取轮播图
export function getBanners() {
  return get<Banner[]>('/api/home/banner')
}

// 获取分类列表
export function getCategories() {
  return get<Category[]>('/api/home/category')
}

// 获取分类树
export function getCategoryTree() {
  return get<Category[]>('/api/home/category/tree')
}

// 获取推荐商品
export function getRecommends(limit?: number) {
  return get<Product[]>('/api/home/recommend', { limit })
}
