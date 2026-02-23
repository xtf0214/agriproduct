import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, removeToken, getUserId } from '@/utils/auth'
import router from '@/router'

// 创建axios实例
const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 30000
})

// 请求拦截器
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = getToken()
    const userId = getUserId()
    if (token && config.headers) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    // 添加 X-User-Id 请求头
    if (userId && config.headers) {
      config.headers['X-User-Id'] = String(userId)
    }
    return config
  },
  (error) => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data
    
    // 业务状态码判断
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      
      // 401 未授权
      if (res.code === 401) {
        removeToken()
        router.push('/login')
      }
      
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    
    return res
  },
  (error) => {
    console.error('Response error:', error)
    
    if (error.response) {
      switch (error.response.status) {
        case 401:
          ElMessage.error('登录已过期，请重新登录')
          removeToken()
          router.push('/login')
          break
        case 403:
          ElMessage.error('没有权限访问')
          break
        case 404:
          ElMessage.error('请求资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error(error.message || '网络错误')
      }
    } else {
      ElMessage.error('网络连接失败')
    }
    
    return Promise.reject(error)
  }
)

// 扩展的请求配置
interface ExtendedAxiosRequestConfig extends AxiosRequestConfig {
  headers?: Record<string, string>
}

// 封装请求方法
export const request = {
  get<T = any>(url: string, params?: any, config?: ExtendedAxiosRequestConfig): Promise<T> {
    return service.get(url, { params, ...config })
  },
  
  post<T = any>(url: string, data?: any, config?: ExtendedAxiosRequestConfig): Promise<T> {
    return service.post(url, data, config)
  },
  
  put<T = any>(url: string, data?: any, config?: ExtendedAxiosRequestConfig): Promise<T> {
    return service.put(url, data, config)
  },
  
  delete<T = any>(url: string, params?: any, config?: ExtendedAxiosRequestConfig): Promise<T> {
    return service.delete(url, { params, ...config })
  },
  
  upload<T = any>(url: string, file: File, onProgress?: (percent: number) => void): Promise<T> {
    const formData = new FormData()
    formData.append('file', file)
    
    return service.post(url, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      onUploadProgress: (progressEvent) => {
        if (onProgress && progressEvent.total) {
          const percent = Math.round((progressEvent.loaded * 100) / progressEvent.total)
          onProgress(percent)
        }
      }
    })
  }
}

export default service
