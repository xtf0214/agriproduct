// 通用响应类型
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

// 分页响应
export interface PageResponse<T> {
  records: T[]
  total: number
  pages: number
  current: number
  size: number
}

// 用户相关
export interface User {
  userId: number
  username: string
  nickname: string
  phone: string
  avatar: string
}

export interface LoginParams {
  username: string
  password: string
  loginType?: string
}

export interface RegisterParams {
  username: string
  password: string
  nickname?: string
  phone: string
  registerType?: string
}

export interface LoginResult {
  token: string
  userId: number
  username: string
  nickname: string
  userType: string
}

export interface UserUpdateParams {
  nickname?: string
  avatar?: string
  phone?: string
}

// 轮播图
export interface Banner {
  id: number
  title: string
  imageUrl: string
  linkUrl: string
}

// 分类
export interface Category {
  id: number
  name: string
  icon?: string
  children?: Category[]
}

// 商品
export interface Product {
  id: number
  name: string
  price: number
  stock: number
  sales: number
  description?: string
  imageUrl: string
  images?: string[]
  categoryId?: number
  merchantId?: number
  merchantName?: string
}

// 购物车
export interface CartItem {
  cartId: number
  productId: number
  productName: string
  productImage: string
  price: number
  quantity: number
  subtotal: number
  selected?: boolean
}

export interface CartAddParams {
  productId: number
  quantity: number
}

// 地址
export interface Address {
  id: number
  receiverName: string
  receiverPhone: string
  province: string
  city: string
  district: string
  detailAddress: string
  isDefault: number
}

export interface AddressParams {
  receiverName: string
  receiverPhone: string
  province: string
  city: string
  district: string
  detailAddress: string
  isDefault?: number
}

// 订单
export interface Order {
  id: number
  orderNo: string
  totalAmount: number
  payAmount: number
  status: number
  createTime: string
  payTime?: string
  shipTime?: string
  receiveTime?: string
  receiverName: string
  receiverPhone: string
  receiverAddress: string
  remark?: string
  items: OrderItem[]
}

export interface OrderItem {
  id: number
  productId: number
  productName: string
  productImage: string
  price: number
  quantity: number
  subtotal: number
}

export interface OrderCreateParams {
  addressId: number
  cartIds?: number[]
  productId?: number
  quantity?: number
  remark?: string
}

export interface OrderCreateResult {
  orderId?: string | number
  id?: number
  orderNo?: string
  totalAmount: number
  status: number
}

// 首页数据
export interface HomeData {
  banners: Banner[]
  categories: Category[]
  recommends: Product[]
}

// 订单状态枚举
export enum OrderStatus {
  PENDING = 1,      // 待付款
  PAID = 2,         // 待发货
  SHIPPED = 3,      // 待收货
  COMPLETED = 4,    // 已完成
  CANCELLED = 5     // 已取消
}

export const OrderStatusText: Record<OrderStatus, string> = {
  [OrderStatus.PENDING]: '待付款',
  [OrderStatus.PAID]: '待发货',
  [OrderStatus.SHIPPED]: '待收货',
  [OrderStatus.COMPLETED]: '已完成',
  [OrderStatus.CANCELLED]: '已取消'
}
