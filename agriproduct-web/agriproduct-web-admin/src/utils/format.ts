import dayjs from 'dayjs'

export function formatDate(date: string | Date, format = 'YYYY-MM-DD HH:mm:ss'): string {
  if (!date) return '-'
  return dayjs(date).format(format)
}

export function formatPrice(price: number | string): string {
  if (price === undefined || price === null) return '-'
  return `¥${Number(price).toFixed(2)}`
}

export function formatNumber(num: number | string): string {
  if (num === undefined || num === null) return '-'
  return Number(num).toLocaleString()
}
