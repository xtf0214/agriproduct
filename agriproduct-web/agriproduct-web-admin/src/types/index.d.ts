export interface SelectOption {
  label: string
  value: number | string
}

export interface TableResponse<T> {
  records: T[]
  total: number
  pages: number
  current: number
  size: number
}

// API响应通用类型
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}
