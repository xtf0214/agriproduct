import { get, post, put } from '@/utils/request'
import type { Order, OrderCreateParams, OrderCreateResult, PageResponse } from '@/types'

// 创建订单
export function createOrder(data: OrderCreateParams) {
  return post<OrderCreateResult>('/api/order', data)
}

// 获取订单详情
export function getOrderDetail(orderId: number | string) {
  return get<Order>(`/api/order/${orderId}`)
}

// 获取订单列表
export function getOrderList(params?: { status?: number; pageNum?: number; pageSize?: number }) {
  return get<PageResponse<Order>>('/api/order/list', params)
}

// 取消订单
export function cancelOrder(orderId: number | string) {
  return put(`/api/order/${orderId}/cancel`)
}

// 确认收货
export function confirmReceive(orderId: number | string) {
  return put(`/api/order/${orderId}/confirm`)
}
