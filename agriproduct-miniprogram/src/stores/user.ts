import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, getProfile } from '@/api/auth'
import type { User, LoginParams } from '@/types'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref<string>(uni.getStorageSync('token') || '')
  const userId = ref<number>(uni.getStorageSync('userId') || 0)
  const userInfo = ref<User | null>(uni.getStorageSync('userInfo') || null)

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)

  // 登录
  async function login(params: LoginParams) {
    const res = await loginApi(params)
    token.value = res.token
    userId.value = res.userId
    uni.setStorageSync('token', res.token)
    uni.setStorageSync('userId', res.userId)
    
    // 获取用户信息
    await fetchUserInfo()
    
    return res
  }

  // 获取用户信息
  async function fetchUserInfo() {
    if (!userId.value) return
    try {
      const info = await getProfile()
      userInfo.value = info
      uni.setStorageSync('userInfo', info)
    } catch (e) {
      console.error('获取用户信息失败', e)
    }
  }

  // 登出
  function logout() {
    token.value = ''
    userId.value = 0
    userInfo.value = null
    uni.removeStorageSync('token')
    uni.removeStorageSync('userId')
    uni.removeStorageSync('userInfo')
  }

  // 检查登录状态
  function checkLogin(redirectToLogin = true): boolean {
    if (!isLoggedIn.value && redirectToLogin) {
      uni.showToast({ title: '请先登录', icon: 'none' })
      setTimeout(() => {
        uni.navigateTo({ url: '/pages/login/login' })
      }, 1500)
      return false
    }
    return isLoggedIn.value
  }

  return {
    token,
    userId,
    userInfo,
    isLoggedIn,
    login,
    logout,
    fetchUserInfo,
    checkLogin
  }
})
