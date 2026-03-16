import { get } from '@/utils/request'
import type { Product, PageResponse } from '@/types'
import { normalizeProduct, type ProductApiRaw } from '@/utils/normalize'

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
  return get<PageResponse<ProductApiRaw>>('/api/product/list', params).then((res) => {
    return {
      ...res,
      records: Array.isArray(res?.records) ? res.records.map((item) => normalizeProduct(item)) : []
    } as PageResponse<Product>
  })
}

// 获取商品详情
export function getProductDetail(id: number) {
  return get<ProductApiRaw>(`/api/product/${id}`).then((data) => normalizeProduct(data || {}))
}

// 搜索商品
export function searchProducts(keyword: string) {
  return get<ProductApiRaw[]>('/api/product/search', { keyword }).then((list) => {
    return Array.isArray(list) ? list.map((item) => normalizeProduct(item)) : []
  })
}
