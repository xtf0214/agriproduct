import { request } from '@/utils/request'
import type { ApiResponse, PageResult, Product, ProductForm, Category } from '@/types/api'

// 获取商品列表
export const getProductList = (params: { pageNum?: number; pageSize?: number; keyword?: string; status?: number }) => {
  return request.get<ApiResponse<PageResult<Product>>>('/api/product/list', params)
}

// 获取商品详情
export const getProductDetail = (id: number) => {
  return request.get<ApiResponse<Product>>(`/api/product/${id}`)
}

// 添加商品（商家端，需要后端支持）
export const addProduct = (data: ProductForm) => {
  return request.post<ApiResponse<Product>>('/api/merchant/product', data)
}

// 修改商品（商家端，需要后端支持）
export const updateProduct = (id: number, data: ProductForm) => {
  return request.put<ApiResponse<Product>>(`/api/merchant/product/${id}`, data)
}

// 删除商品（商家端，需要后端支持）
export const deleteProduct = (id: number) => {
  return request.delete<ApiResponse<void>>(`/api/merchant/product/${id}`)
}

// 修改商品库存（商家端，需要后端支持）
export const updateProductStock = (id: number, stock: number) => {
  return request.put<ApiResponse<void>>(`/api/merchant/product/${id}/stock`, { stock })
}

// 商品上架（商家端，需要后端支持）
export const publishProduct = (id: number) => {
  return request.put<ApiResponse<void>>(`/api/merchant/product/${id}/publish`)
}

// 商品下架（商家端，需要后端支持）
export const unpublishProduct = (id: number) => {
  return request.put<ApiResponse<void>>(`/api/merchant/product/${id}/unpublish`)
}

// 获取分类列表
export const getCategoryList = () => {
  return request.get<ApiResponse<Category[]>>('/api/home/category')
}

// 获取分类树
export const getCategoryTree = () => {
  return request.get<ApiResponse<Category[]>>('/api/home/category/tree')
}
