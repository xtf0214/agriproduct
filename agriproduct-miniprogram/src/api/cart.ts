import { get, post, put, del } from '@/utils/request'
import type { CartItem, CartAddParams } from '@/types'

interface CartApiItem {
  id?: number
  cartId?: number
  productId: number
  productName: string
  productImage: string
  price?: number
  productPrice?: number
  quantity: number
  subtotal?: number
  totalPrice?: number
  selected?: boolean
}

function normalizeCartItem(item: CartApiItem): CartItem {
  const cartId = Number(item.cartId ?? item.id)
  const price = Number(item.price ?? item.productPrice ?? 0)
  const quantity = Number(item.quantity ?? 0)
  const subtotal = Number(item.subtotal ?? item.totalPrice ?? price * quantity)

  return {
    cartId,
    productId: Number(item.productId),
    productName: item.productName,
    productImage: item.productImage,
    price,
    quantity,
    subtotal,
    selected: item.selected
  }
}

// 获取购物车列表
export function getCartList() {
  return get<CartApiItem[]>('/api/cart/list').then((list) => {
    if (!Array.isArray(list)) return []
    return list
      .map(normalizeCartItem)
      .filter(item => Number.isFinite(item.cartId) && item.cartId > 0)
  })
}

// 添加商品到购物车
export function addToCart(data: CartAddParams) {
  return post('/api/cart', data)
}

// 更新购物车商品数量
export function updateCartQuantity(cartId: number, quantity: number) {
  return put(`/api/cart/${cartId}?quantity=${quantity}`)
}

// 删除购物车项
export function removeCartItem(cartId: number) {
  return del(`/api/cart/${cartId}`)
}

// 清空购物车
export function clearCart() {
  return del('/api/cart/clear')
}

// 获取购物车商品数量
export function getCartCount() {
  return get<number>('/api/cart/count')
}
