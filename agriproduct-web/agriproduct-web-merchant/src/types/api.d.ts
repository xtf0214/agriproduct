// API响应通用类型
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

// 分页请求参数
export interface PageParams {
  pageNum: number
  pageSize: number
}

// 分页响应数据
export interface PageResult<T> {
  records: T[]
  total: number
  pages: number
  current: number
  size: number
}

// 登录响应
export interface LoginResponse {
  token: string
  userId: number
  username: string
  nickname: string
  userType: string
}

// 用户信息
export interface UserInfo {
  id?: number
  userId?: number
  username: string
  nickname: string
  avatar?: string
  phone?: string
  token?: string
  userType?: string
}

// 商家信息
export interface MerchantInfo {
  id: number
  userId?: number
  shopName: string
  shopDesc?: string
  businessLicense?: string
  contactPhone?: string
  contactName?: string
  status: number
}

// 商家入驻申请请求
export interface MerchantApplyRequest {
  shopName: string
  shopDesc?: string
  businessLicense: string
  contactPhone: string
  contactName: string
}

// 商品信息
export interface Product {
  id: number
  merchantId: number
  categoryId: number
  name: string
  subtitle?: string
  mainImage?: string
  images?: string
  price: number
  originalPrice?: number
  stock: number
  sales?: number
  detail?: string
  status: number
  auditStatus?: number
  auditReason?: string
  createTime?: string
  updateTime?: string
}

// 商品表单
export interface ProductForm {
  id?: number
  categoryId: number
  name: string
  subtitle?: string
  mainImage?: string
  images?: string[]
  price: number
  originalPrice?: number
  stock: number
  detail?: string
}

// 订单信息
export interface Order {
  id?: number
  orderId?: string
  orderNo?: string
  userId?: number
  merchantId?: number
  totalAmount: number
  payAmount?: number
  status: number
  statusText?: string
  payStatus?: number
  payTime?: string
  shipTime?: string
  finishTime?: string
  receiverName?: string
  receiverPhone?: string
  receiverAddress?: string
  remark?: string
  createTime?: string
  items?: OrderItem[]
}

// 订单商品
export interface OrderItem {
  id?: number
  orderId?: number
  productId: number
  productName: string
  productImage?: string
  price: number
  quantity: number
  totalAmount?: number
}

// 统计概览
export interface StatisticsOverview {
  todaySales: number
  todayOrders: number
  totalProducts: number
  pendingOrders: number
}

// 商品销量统计
export interface ProductSales {
  productId: number
  productName: string
  productImage?: string
  salesCount: number
  salesAmount: number
}

// 分类信息
export interface Category {
  id: number
  name: string
  parentId?: number
  icon?: string
  sortOrder?: number
  children?: Category[]
}

// 商品查询参数
export interface ProductQueryRequest {
  categoryId?: number
  keyword?: string
  priceSort?: string
  salesSort?: string
}

// 地址信息
export interface AddressVO {
  addressId: number
  receiverName: string
  receiverPhone: string
  province?: string
  city?: string
  district?: string
  detailAddress: string
  isDefault: number
}
