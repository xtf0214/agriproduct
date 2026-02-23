import dayjs from 'dayjs'

// 日期格式化
export function formatDate(date: string | Date, format: string = 'YYYY-MM-DD'): string {
  if (!date) return ''
  return dayjs(date).format(format)
}

// 日期时间格式化
export function formatDateTime(date: string | Date, format: string = 'YYYY-MM-DD HH:mm:ss'): string {
  if (!date) return ''
  return dayjs(date).format(format)
}

// 金额格式化（分转元）
export function formatMoney(amount: number): string {
  if (amount === null || amount === undefined) return '0.00'
  return (amount / 100).toFixed(2)
}

// 金额格式化（元）
export function formatPrice(price: number): string {
  if (price === null || price === undefined) return '0.00'
  return price.toFixed(2)
}

// 订单状态文字
export function orderStatusText(status: number): string {
  const statusMap: Record<number, string> = {
    1: '待付款',
    2: '待发货',
    3: '待收货',
    4: '已完成',
    5: '已取消'
  }
  return statusMap[status] || '未知'
}

// 订单状态颜色
export function orderStatusType(status: number): string {
  const typeMap: Record<number, string> = {
    1: 'warning',
    2: 'primary',
    3: 'info',
    4: 'success',
    5: 'danger'
  }
  return typeMap[status] || 'info'
}

// 商品状态文字
export function productStatusText(status: number): string {
  const statusMap: Record<number, string> = {
    0: '已下架',
    1: '已上架',
    2: '审核中'
  }
  return statusMap[status] || '未知'
}

// 商品审核状态文字
export function auditStatusText(status: number): string {
  const statusMap: Record<number, string> = {
    0: '待审核',
    1: '已通过',
    2: '已拒绝'
  }
  return statusMap[status] || '未知'
}

// 商家状态文字
export function merchantStatusText(status: number): string {
  const statusMap: Record<number, string> = {
    0: '待审核',
    1: '已通过',
    2: '已拒绝'
  }
  return statusMap[status] || '未知'
}
