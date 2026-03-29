import { get, post, put, del } from './index'
import request from '@/utils/request'

export interface CategoryItem {
  id: number
  name: string
  parentId: number
  icon?: string
  sortOrder: number
  status: number
  hasChildren?: boolean
  children?: CategoryItem[]
  createTime?: string
}

export interface CategoryForm {
  id?: number
  name: string
  parentId?: number
  icon?: string
  sortOrder?: number
  status?: number
}

// 获取分类树形结构
export function getCategoryTreeApi() {
  return get<CategoryItem[]>('/admin/category/tree')
}

// 获取一级分类列表
export function getTopCategoriesApi() {
  return get<CategoryItem[]>('/admin/category/top')
}

// 获取子分类列表
export function getChildrenCategoriesApi(parentId: number) {
  return get<CategoryItem[]>(`/admin/category/children/${parentId}`)
}

// 新增分类
export function addCategoryApi(data: CategoryForm) {
  return post<number>('/admin/category', data)
}

// 更新分类
export function updateCategoryApi(id: number, data: CategoryForm) {
  return put<boolean>(`/admin/category/${id}`, data)
}

// 删除分类
export function deleteCategoryApi(id: number) {
  return del<boolean>(`/admin/category/${id}`)
}

// 上传分类图标
export function uploadCategoryIconApi(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request<{ url: string }>({
    url: '/admin/category/upload',
    method: 'POST',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
