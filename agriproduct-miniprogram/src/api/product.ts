import { get } from '@/utils/request'
import type { Product, PageResponse } from '@/types'

// 商品列表参数
interface ProductListParams {
  pageNum?: number
  pageSize?: number
  categoryId?: number
  keyword?: string
  priceSort?: 'asc' | 'desc'
  salesSort?: 'asc' | 'desc'
}

// 获取商品列表
export function getProductList(params: ProductListParams) {
  return get<PageResponse<Product>>('/api/product/list', params)
}

// 获取商品详情
export function getProductDetail(id: number) {
  return get<Product>(`/api/product/${id}`)
}

// 搜索商品
export function searchProducts(keyword: string) {
  return get<Product[]>('/api/product/search', { keyword })
}
