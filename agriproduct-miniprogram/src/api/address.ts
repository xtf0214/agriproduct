import { get, post, put, del } from '@/utils/request'
import type { Address, AddressParams } from '@/types'

// 获取地址列表
export function getAddressList() {
  return get<Address[]>('/api/address/list')
}

// 获取地址详情
export function getAddressDetail(id: number) {
  return get<Address>(`/api/address/${id}`)
}

// 添加地址
export function addAddress(data: AddressParams) {
  return post('/api/address', data)
}

// 更新地址
export function updateAddress(id: number, data: AddressParams) {
  return put(`/api/address/${id}`, data)
}

// 删除地址
export function deleteAddress(id: number) {
  return del(`/api/address/${id}`)
}

// 获取默认地址
export function getDefaultAddress() {
  return get<Address>('/api/address/default')
}
