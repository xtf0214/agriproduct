import { get, put } from './index'

export interface MerchantItem {
  id: number
  shopName: string
  shopDesc?: string
  contactPhone: string
  contactName?: string
  businessLicense?: string
  status: number
  createTime?: string
}

export interface MerchantListParams {
  pageNum?: number
  pageSize?: number
}

// Get merchant list
export function getMerchantListApi(params?: MerchantListParams) {
  return get<{ records: MerchantItem[]; total: number }>('/admin/merchant/list', params)
}

// Audit merchant
export interface AuditMerchantParams {
  auditStatus: number
  auditRemark?: string
}

export function auditMerchantApi(id: number, params: AuditMerchantParams) {
  return put<boolean>(`/admin/merchant/${id}/audit`, params)
}
