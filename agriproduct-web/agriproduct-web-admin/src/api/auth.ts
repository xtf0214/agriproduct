import { post, get } from './index'

export interface LoginParams {
  username: string
  password: string
  loginType?: string
}

export interface RegisterParams {
  username: string
  password: string
  nickname?: string
  phone: string
  registerType?: string
}

export interface LoginResponse {
  token: string
  userId: number
  username: string
  nickname: string
  phone?: string
  avatar?: string
  userType: string
}

// Login
export function loginApi(params: LoginParams) {
  return post<LoginResponse>('/auth/login', params)
}

// Register
export function registerApi(params: RegisterParams) {
  return post<number>('/auth/register', params)
}

// Get current user profile
export function getProfileApi() {
  return get<LoginResponse>('/auth/profile')
}
