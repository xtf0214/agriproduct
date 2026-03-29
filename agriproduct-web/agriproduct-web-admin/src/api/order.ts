import { get, put } from './index'

export interface OrderItem {
  id: number
  orderNo: string
  userId: number
  merchantId: number
  totalAmount: number
  payAmount: number
  status: number
  statusDesc: string
  payStatus: number
  payTime?: string
  shipTime?: string
  finishTime?: string
  receiverName: string
  receiverPhone: string
  receiverAddress: string
  remark?: string
  createTime: string
  items?: OrderItemDetail[]
}

export interface OrderItemDetail {
  id: number
  orderId: number
  productId: number
  productName: string
  productImage: string
  price: number
  quantity: number
}

export interface OrderListParams {
  pageNum?: number
  pageSize?: number
  orderNo?: string
  status?: number
  userId?: number
  merchantId?: number
}

// Get order list
export function getOrderListApi(params?: OrderListParams) {
  return get<{ records: OrderItem[]; total: number }>('/admin/order/list', params)
}

// Get order detail
export function getOrderDetailApi(id: number) {
  return get<OrderItem>(`/admin/order/${id}`)
}

// Ship order
export function shipOrderApi(id: number) {
  return put<boolean>(`/admin/order/${id}/ship`)
}

// Cancel order
export function cancelOrderApi(id: number) {
  return put<boolean>(`/admin/order/${id}/cancel`)
}
