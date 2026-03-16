import { get } from '@/utils/request'
import type { Banner, Category, Product, HomeData } from '@/types'
import { normalizeBanner, normalizeCategory, normalizeProduct, type BannerApiRaw, type ProductApiRaw } from '@/utils/normalize'

// 获取首页数据
export function getHomeData() {
  return get<any>('/api/home').then((res) => {
    const data = res as HomeData
    return {
      ...data,
      banners: Array.isArray(data?.banners) ? data.banners.map((item) => normalizeBanner(item as BannerApiRaw)) : [],
      categories: Array.isArray(data?.categories) ? data.categories.map((item) => normalizeCategory(item as Category)) : [],
      recommends: Array.isArray(data?.recommends) ? data.recommends.map((item) => normalizeProduct(item as ProductApiRaw)) : []
    } as HomeData
  })
}

// 获取轮播图
export function getBanners() {
  return get<any[]>('/api/home/banner').then((list) => {
    return Array.isArray(list) ? list.map((item) => normalizeBanner(item as BannerApiRaw)) : []
  })
}

// 获取分类列表
export function getCategories() {
  return get<Category[]>('/api/home/category').then((list) => {
    return Array.isArray(list) ? list.map((item) => normalizeCategory(item)) : []
  })
}

// 获取分类树
export function getCategoryTree() {
  return get<Category[]>('/api/home/category/tree').then((list) => {
    return Array.isArray(list) ? list.map((item) => normalizeCategory(item)) : []
  })
}

// 获取推荐商品
export function getRecommends(limit?: number) {
  return get<any[]>('/api/home/recommend', { limit }).then((list) => {
    return Array.isArray(list) ? list.map((item) => normalizeProduct(item as ProductApiRaw)) : []
  })
}
