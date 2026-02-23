import { get, put } from './index'

export interface AuditProductItem {
  id: number
  name: string
  price: number
  stock: number
  sales: number
  imageUrl?: string
  description?: string
  merchantId: number
  merchantName: string
  categoryName?: string
  status: number
  createTime?: string
}

export interface AuditProductListParams {
  pageNum?: number
  pageSize?: number
}

// Get pending audit product list
export function getAuditProductListApi(params?: AuditProductListParams) {
  return get<{ records: AuditProductItem[]; total: number }>('/admin/product/audit/list', params)
}

// Audit product
export interface AuditProductParams {
  auditStatus: number
  auditReason?: string
}

export function auditProductApi(id: number, params: AuditProductParams) {
  return put<boolean>(`/admin/product/${id}/audit`, params)
}
