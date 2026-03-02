import { get, post } from '@/utils/request'
import type { LoginParams, RegisterParams, LoginResult, User } from '@/types'

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
