// 请求配置
export const BASE_URL = 'http://localhost:8080'

// 请求拦截
interface RequestConfig {
  url: string
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE'
  data?: any
  header?: Record<string, string>
  showLoading?: boolean
  showError?: boolean
}

// 响应数据
interface ResponseData<T = any> {
  code: number | string
  message: string
  data: T
}

// 获取存储的 token
function getToken(): string {
  return uni.getStorageSync('token') || ''
}

// 获取用户ID
function getUserId(): string {
  return uni.getStorageSync('userId') || ''
}

// 请求计数器（用于控制 loading）
let requestCount = 0

// 请求封装
export function request<T = any>(config: RequestConfig): Promise<T> {
  const { url, method = 'GET', data, header = {}, showLoading = false, showError = true } = config

  // 显示加载提示（默认改为 false）
  if (showLoading) {
    requestCount++
    if (requestCount === 1) {
      uni.showLoading({ title: '加载中...', mask: true })
    }
  }

  // 设置请求头
  const token = getToken()
  const userId = getUserId()
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...header
  }
  
  if (token) {
    headers['Authorization'] = `Bearer ${token}`
  }
  if (userId) {
    headers['X-User-Id'] = userId
  }

  return new Promise((resolve, reject) => {
    console.log('发起请求:', method, BASE_URL + url, data)
    
    uni.request({
      url: BASE_URL + url,
      method,
      data,
      header: headers,
      success: (res) => {
        if (showLoading) {
          requestCount--
          if (requestCount === 0) {
            uni.hideLoading()
          }
        }
        
        console.log('请求成功:', url, res.statusCode, res.data)

        const responseData = res.data as any
        const isBusinessResponse = responseData && typeof responseData === 'object' && 'code' in responseData

        // 兼容后端直接返回数据（未包装 code/message/data）
        if (!isBusinessResponse) {
          if (res.statusCode >= 200 && res.statusCode < 300) {
            console.log('数据解析成功:', responseData)
            resolve(responseData as T)
          } else {
            const errorMessage =
              (responseData && typeof responseData === 'object' && responseData.message) ||
              `请求失败(${res.statusCode})`
            if (showError) {
              uni.showToast({ title: errorMessage, icon: 'none' })
            }
            reject(new Error(errorMessage))
          }
          return
        }

        const result = responseData as ResponseData<T>
        const code = Number(result.code)

        if (code === 200) {
          console.log('数据解析成功:', result.data)
          resolve(result.data)
        } else if (code === 401) {
          // 未登录或 token 过期
          uni.removeStorageSync('token')
          uni.removeStorageSync('userId')
          uni.removeStorageSync('userInfo')
          uni.showToast({ title: '请先登录', icon: 'none' })
          setTimeout(() => {
            uni.navigateTo({ url: '/pages/login/login' })
          }, 1500)
          reject(new Error(result.message || '请先登录'))
        } else {
          const errorMessage = result.message || '请求失败'
          if (showError) {
            uni.showToast({ title: errorMessage, icon: 'none' })
          }
          reject(new Error(errorMessage))
        }
      },
      fail: (err) => {
        if (showLoading) {
          requestCount--
          if (requestCount === 0) {
            uni.hideLoading()
          }
        }
        if (showError) {
          uni.showToast({ title: '网络请求失败', icon: 'none' })
        }
        reject(err)
      }
    })
  })
}

// GET 请求
export function get<T = any>(url: string, params?: any, config?: Partial<RequestConfig>): Promise<T> {
  let fullUrl = url
  if (params) {
    const query = Object.keys(params)
      .filter(key => params[key] !== undefined && params[key] !== null)
      .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`)
      .join('&')
    if (query) {
      fullUrl += (url.includes('?') ? '&' : '?') + query
    }
  }
  return request<T>({ url: fullUrl, method: 'GET', ...config })
}

// POST 请求
export function post<T = any>(url: string, data?: any, config?: Partial<RequestConfig>): Promise<T> {
  return request<T>({ url, method: 'POST', data, ...config })
}

// PUT 请求
export function put<T = any>(url: string, data?: any, config?: Partial<RequestConfig>): Promise<T> {
  return request<T>({ url, method: 'PUT', data, ...config })
}

// DELETE 请求
export function del<T = any>(url: string, data?: any, config?: Partial<RequestConfig>): Promise<T> {
  return request<T>({ url, method: 'DELETE', data, ...config })
}

// 上传请求配置
interface UploadConfig {
  url: string
  filePath: string
  name?: string
  showLoading?: boolean
  showError?: boolean
}

// 文件上传
export function upload<T = any>(config: UploadConfig): Promise<T> {
  const { url, filePath, name = 'file', showLoading = false, showError = true } = config

  if (showLoading) {
    uni.showLoading({ title: '上传中...', mask: true })
  }

  // 获取 token 和 userId
  const token = getToken()
  const userId = getUserId()
  const header: Record<string, string> = {}

  if (token) {
    header['Authorization'] = `Bearer ${token}`
  }
  if (userId) {
    header['X-User-Id'] = userId
  }

  return new Promise((resolve, reject) => {
    uni.uploadFile({
      url: BASE_URL + url,
      filePath,
      name,
      header,
      success: (res) => {
        if (showLoading) {
          uni.hideLoading()
        }

        if (res.statusCode === 200) {
          const data = JSON.parse(res.data)
          if (data.code === 200) {
            resolve(data.data)
          } else {
            const errorMessage = data.message || '上传失败'
            if (showError) {
              uni.showToast({ title: errorMessage, icon: 'none' })
            }
            reject(new Error(errorMessage))
          }
        } else {
          if (showError) {
            uni.showToast({ title: '上传失败', icon: 'none' })
          }
          reject(new Error('上传失败'))
        }
      },
      fail: (err) => {
        if (showLoading) {
          uni.hideLoading()
        }
        if (showError) {
          uni.showToast({ title: '上传失败', icon: 'none' })
        }
        reject(err)
      }
    })
  })
}

export default { request, get, post, put, del, upload }
