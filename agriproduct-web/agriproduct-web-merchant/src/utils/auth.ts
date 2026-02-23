const TOKEN_KEY = 'merchant_token'
const USER_KEY = 'merchant_user'
const USER_ID_KEY = 'merchant_user_id'

// 获取Token
export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY)
}

// 设置Token
export function setToken(token: string): void {
  localStorage.setItem(TOKEN_KEY, token)
}

// 移除Token
export function removeToken(): void {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(USER_KEY)
  localStorage.removeItem(USER_ID_KEY)
}

// 获取用户ID
export function getUserId(): number | null {
  const id = localStorage.getItem(USER_ID_KEY)
  return id ? Number(id) : null
}

// 设置用户ID
export function setUserId(id: number): void {
  localStorage.setItem(USER_ID_KEY, String(id))
}

// 获取用户信息
export function getUserInfo(): any {
  const userStr = localStorage.getItem(USER_KEY)
  return userStr ? JSON.parse(userStr) : null
}

// 设置用户信息
export function setUserInfo(user: any): void {
  localStorage.setItem(USER_KEY, JSON.stringify(user))
}

// 检查是否已登录
export function isLoggedIn(): boolean {
  return !!getToken()
}
