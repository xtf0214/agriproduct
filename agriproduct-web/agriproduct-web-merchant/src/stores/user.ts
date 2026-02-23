import { defineStore } from 'pinia'
import { ref } from 'vue'
import { request } from '@/utils/request'
import { setToken, removeToken, setUserInfo, getToken, getUserInfo, setUserId, getUserId } from '@/utils/auth'
import type { UserInfo, LoginResponse, ApiResponse, MerchantInfo } from '@/types/api'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(getToken() || '')
  const userInfo = ref<UserInfo | null>(getUserInfo())
  const merchantInfo = ref<MerchantInfo | null>(null)
  
  // 登录
  const login = async (username: string, password: string) => {
    const res = await request.post<ApiResponse<LoginResponse>>('/api/auth/login', {
      username,
      password,
      loginType: 'merchant'
    })
    
    if (res.data) {
      token.value = res.data.token || ''
      
      const user: UserInfo = {
        id: res.data.userId,
        username: res.data.username,
        nickname: res.data.nickname,
        userType: res.data.userType,
        token: res.data.token
      }
      userInfo.value = user
      
      if (res.data.token) {
        setToken(res.data.token)
      }
      if (res.data.userId) {
        setUserId(res.data.userId)
      }
      setUserInfo(user)
    }
    
    return res
  }
  
  // 退出登录
  const logout = () => {
    token.value = ''
    userInfo.value = null
    merchantInfo.value = null
    removeToken()
  }
  
  // 获取用户信息
  const fetchUserInfo = async () => {
    const userId = getUserId()
    if (!userId) return null
    
    const res = await request.get<ApiResponse<UserInfo>>('/api/auth/profile', {}, {
      headers: { 'X-User-Id': String(userId) }
    })
    if (res.data) {
      userInfo.value = res.data
      setUserInfo(res.data)
    }
    return res
  }
  
  // 获取商家信息
  const fetchMerchantInfo = async () => {
    const userId = getUserId()
    if (!userId) return null
    
    const res = await request.get<ApiResponse<MerchantInfo>>('/api/merchant/info', {}, {
      headers: { 'X-User-Id': String(userId) }
    })
    if (res.data) {
      merchantInfo.value = res.data
    }
    return res
  }
  
  return {
    token,
    userInfo,
    merchantInfo,
    login,
    logout,
    fetchUserInfo,
    fetchMerchantInfo
  }
})
