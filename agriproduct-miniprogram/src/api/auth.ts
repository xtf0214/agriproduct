import { get, post, put, upload } from '@/utils/request'
import type { LoginParams, RegisterParams, LoginResult, User, UserUpdateParams } from '@/types'

// 登录
export function login(data: LoginParams) {
  return post<LoginResult>('/api/auth/login', data, { showLoading: true })
}

// 注册
export function register(data: RegisterParams) {
  return post<number>('/api/auth/register', data, { showLoading: true })
}

// 获取当前用户信息
export function getProfile() {
  return get<User>('/api/auth/profile')
}

// 更新用户信息
export function updateProfile(data: UserUpdateParams) {
  return put<boolean>('/api/auth/profile', data, { showLoading: true })
}

// 上传用户头像
export function uploadAvatar(filePath: string) {
  return upload<{ url: string }>('/api/auth/avatar/upload', filePath, { showLoading: true })
}
