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
