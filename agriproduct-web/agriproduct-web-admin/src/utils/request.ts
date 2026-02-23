import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse } from 'axios'
import { ElMessage, type ElMessageBox } from 'element-plus'
import { getToken } from './auth'
import router from '@/router'

// Response interface
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

// Create axios instance
const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 30000
})

// Request interceptor
service.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token && config.headers) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

// Response interceptor
service.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    const res = response.data

    // Success
    if (res.code === 200) {
      return res
    }

    // Error handling
    ElMessage.error(res.message || '请求失败')

    // Token expired or unauthorized
    if (res.code === 401) {
      ElMessageBox.confirm('登录状态已过期，请重新登录', '系统提示', {
        confirmButtonText: '重新登录',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        localStorage.removeItem('admin_token')
        localStorage.removeItem('admin_user')
        router.push('/login')
      })
    }

    return Promise.reject(new Error(res.message || '请求失败'))
  },
  (error) => {
    console.error('Response error:', error)
    ElMessage.error(error.message || '网络请求失败')
    return Promise.reject(error)
  }
)

export default service

// Generic request method
export function request<T = any>(config: AxiosRequestConfig): Promise<ApiResponse<T>> {
  return service.request<ApiResponse<T>>(config)
}

export function get<T = any>(url: string, params?: any): Promise<ApiResponse<T>> {
  return request({ method: 'get', url, params })
}

export function post<T = any>(url: string, data?: any): Promise<ApiResponse<T>> {
  return request({ method: 'post', url, data })
}

export function put<T = any>(url: string, data?: any): Promise<ApiResponse<T>> {
  return request({ method: 'put', url, data })
}

export function del<T = any>(url: string): Promise<ApiResponse<T>> {
  return request({ method: 'delete', url })
}
