import { request } from '@/utils/request'
import type { ApiResponse, PageResult, Order } from '@/types/api'

// 获取订单列表（商家端）
export const getOrderList = (params: { pageNum?: number; pageSize?: number }) => {
  return request.get<ApiResponse<PageResult<Order>>>('/api/merchant/orders', params)
}

// 获取订单详情
export const getOrderDetail = (id: number) => {
  return request.get<ApiResponse<Order>>(`/api/order/${id}`)
}

// 订单发货
export const shipOrder = (orderId: number) => {
  return request.put<ApiResponse<void>>(`/api/merchant/order/${orderId}/ship`)
}
