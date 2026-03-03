import { get } from './index'

// 统计概览
export interface StatisticsOverview {
  totalUsers: number
  totalMerchants: number
  totalProducts: number
  totalOrders: number
}

// 每日订单统计
export interface DailyOrderStatistics {
  date: string
  orderCount: number
  totalAmount: number
}

// 分类销售统计
export interface CategorySales {
  categoryId: number
  categoryName: string
  productCount: number
  salesAmount: number
}

// 获取统计概览
export const getStatisticsOverviewApi = () => {
  return get<StatisticsOverview>('/admin/statistics/overview')
}

// 获取每日订单统计
export const getDailyOrderStatisticsApi = (startDate: string, endDate: string) => {
  return get<DailyOrderStatistics[]>('/admin/statistics/orders/daily', {
    startDate,
    endDate
  })
}

// 获取分类销售统计
export const getCategorySalesApi = () => {
  return get<CategorySales[]>('/admin/statistics/category')
}
