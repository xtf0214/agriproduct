export { default as request, get, post, put, del } from '@/utils/request'
export { loginApi, registerApi } from './auth'
export { getUserListApi, updateUserStatusApi } from './user'
export { getMerchantListApi, auditMerchantApi } from './merchant'
export { getAuditProductListApi, auditProductApi } from './product'
export { getBannerListApi, addBannerApi, updateBannerApi, deleteBannerApi } from './banner'
export { getStatisticsOverviewApi, getDailyOrderStatisticsApi, getCategorySalesApi } from './statistics'
export {
  getCategoryTreeApi,
  getTopCategoriesApi,
  getChildrenCategoriesApi,
  addCategoryApi,
  updateCategoryApi,
  deleteCategoryApi,
  uploadCategoryIconApi
} from './category'
export { getOrderListApi, getOrderDetailApi, shipOrderApi, cancelOrderApi } from './order'
export type { CategoryItem, CategoryForm } from './category'
export type { OrderItem, OrderItemDetail, OrderListParams } from './order'
