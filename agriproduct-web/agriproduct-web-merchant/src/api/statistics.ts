import { request } from '@/utils/request'
import type { ApiResponse, StatisticsOverview, ProductSales } from '@/types/api'

// 获取统计概览（商家端，需要后端支持）
export const getStatisticsOverview = () => {
  return request.get<ApiResponse<StatisticsOverview>>('/api/merchant/statistics/overview')
}

// 获取销售统计（按日期）
export const getSalesStatistics = (startDate: string, endDate: string) => {
  return request.get<ApiResponse<{ date: string; amount: number; count: number }[]>>('/api/merchant/statistics/sales', {
    startDate,
    endDate
  })
}

// 获取商品销量排行
export const getProductSalesRank = (limit: number = 10) => {
  return request.get<ApiResponse<ProductSales[]>>('/api/merchant/statistics/products', { limit })
}
