import { defineStore } from 'pinia'
import { ref } from 'vue'
import { loginApi } from '@/api/auth'

export interface UserInfo {
  token: string
  userId: number
  username: string
  nickname: string
  phone?: string
  avatar?: string
  userType: string
}

export const useUserStore = defineStore(
  'admin-user',
  () => {
    const token = ref<string>(localStorage.getItem('admin_token') || '')
    const userInfo = ref<UserInfo | null>(
      JSON.parse(localStorage.getItem('admin_user') || 'null')
    )

    const isLogin = ref<boolean>(!!token.value)

    function setToken(tokenValue: string) {
      token.value = tokenValue
      localStorage.setItem('admin_token', tokenValue)
      isLogin.value = true
    }

    function setUserInfo(info: UserInfo) {
      userInfo.value = info
      localStorage.setItem('admin_user', JSON.stringify(info))
    }

    async function login(username: string, password: string) {
      try {
        const res = await loginApi({
          username,
          password,
          loginType: 'admin'
        })

        if (res.code === 200 && res.data) {
          setToken(res.data.token)
          setUserInfo(res.data)
          return true
        }
        return false
      } catch (error) {
        console.error('Login error:', error)
        return false
      }
    }

    function logout() {
      token.value = ''
      userInfo.value = null
      isLogin.value = false
      localStorage.removeItem('admin_token')
      localStorage.removeItem('admin_user')
    }

    return {
      token,
      userInfo,
      isLogin,
      setToken,
      setUserInfo,
      login,
      logout
    }
  },
  {
    persist: false
  }
)
