import { get, post, put, del } from './index'

export interface BannerItem {
  id: number
  title: string
  imageUrl: string
  linkType?: number
  linkId?: number
  linkUrl?: string
  sortOrder?: number
  status: number
  createTime?: string
}

export interface BannerListParams {
  pageNum?: number
  pageSize?: number
}

export interface BannerParams {
  title?: string
  image: string
  linkType?: number
  linkId?: number
  sortOrder?: number
  status?: number
}

// Get banner list
export function getBannerListApi(params?: BannerListParams) {
  return get<{ records: BannerItem[]; total: number }>('/admin/banner/list', params)
}

// Add banner
export function addBannerApi(params: BannerParams) {
  return post<number>('/admin/banner', params)
}

// Update banner
export function updateBannerApi(id: number, params: Partial<BannerParams>) {
  return put<boolean>(`/admin/banner/${id}`, params)
}

// Delete banner
export function deleteBannerApi(id: number) {
  return del<boolean>(`/admin/banner/${id}`)
}
