import { request } from '@/utils/request'
import type { ApiResponse, UserInfo, LoginResponse, MerchantInfo } from '@/types/api'

// 登录
export const login = (username: string, password: string) => {
  return request.post<ApiResponse<LoginResponse>>('/api/auth/login', {
    username,
    password,
    loginType: 'merchant' // 商家登录
  })
}

// 获取当前用户信息
export const getUserInfo = (userId: number) => {
  return request.get<ApiResponse<UserInfo>>('/api/auth/profile', {}, {
    headers: { 'X-User-Id': String(userId) }
  })
}

// 获取商家信息
export const getMerchantInfo = (userId: number) => {
  return request.get<ApiResponse<MerchantInfo>>('/api/merchant/info', {}, {
    headers: { 'X-User-Id': String(userId) }
  })
}

// 更新商家信息
export const updateMerchantInfo = (id: number, userId: number, data: Partial<MerchantInfo>) => {
  return request.put<ApiResponse<boolean>>(`/api/merchant/${id}`, data, {
    headers: { 'X-User-Id': String(userId) }
  })
}

// 修改用户信息（需要后端支持）
export const updateUserInfo = (userId: number, data: Partial<UserInfo>) => {
  return request.put<ApiResponse<void>>('/api/auth/profile', data, {
    headers: { 'X-User-Id': String(userId) }
  })
}

// 修改密码（需要后端支持）
export const updatePassword = (userId: number, oldPassword: string, newPassword: string) => {
  return request.put<ApiResponse<void>>('/api/auth/password', {
    oldPassword,
    newPassword
  }, {
    headers: { 'X-User-Id': String(userId) }
  })
}
