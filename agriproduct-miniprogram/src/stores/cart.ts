import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { 
  getCartList, 
  addToCart, 
  updateCartQuantity, 
  removeCartItem, 
  clearCart,
  getCartCount 
} from '@/api/cart'
import type { CartItem } from '@/types'
import { useUserStore } from './user'

export const useCartStore = defineStore('cart', () => {
  // 状态
  const cartList = ref<CartItem[]>([])
  const selectedIds = ref<number[]>([])

  // 计算属性
  const totalCount = computed(() => {
    return cartList.value.reduce((sum, item) => sum + item.quantity, 0)
  })

  const selectedItems = computed(() => {
    return cartList.value.filter(item => selectedIds.value.includes(item.cartId))
  })

  const selectedCount = computed(() => {
    return selectedItems.value.reduce((sum, item) => sum + item.quantity, 0)
  })

  const selectedAmount = computed(() => {
    return selectedItems.value.reduce((sum, item) => sum + item.subtotal, 0)
  })

  const isAllSelected = computed(() => {
    return cartList.value.length > 0 && selectedIds.value.length === cartList.value.length
  })

  // 获取购物车列表
  async function fetchCartList() {
    const userStore = useUserStore()
    if (!userStore.isLoggedIn) {
      cartList.value = []
      return
    }
    try {
      console.log('开始获取购物车列表...')
      const list = await getCartList()
      console.log('购物车数据:', list)
      
      // 数据验证
      cartList.value = Array.isArray(list) ? list : []
      
      // 默认全选
      selectedIds.value = cartList.value.map(item => item.cartId)
      
      console.log('购物车商品数量:', cartList.value.length)
    } catch (e) {
      console.error('获取购物车失败', e)
      cartList.value = []
    }
  }

  // 添加到购物车
  async function addProduct(productId: number, quantity: number = 1) {
    const userStore = useUserStore()
    if (!userStore.checkLogin()) return false
    
    try {
      await addToCart({ productId, quantity })
      uni.showToast({ title: '添加成功', icon: 'success' })
      await fetchCartList()
      return true
    } catch (e) {
      console.error('添加购物车失败', e)
      return false
    }
  }

  // 更新数量
  async function updateQuantity(cartId: number, quantity: number) {
    if (quantity < 1) return
    try {
      await updateCartQuantity(cartId, quantity)
      const item = cartList.value.find(i => i.cartId === cartId)
      if (item) {
        item.quantity = quantity
        item.subtotal = item.price * quantity
      }
    } catch (e) {
      console.error('更新数量失败', e)
    }
  }

  // 删除商品
  async function removeProduct(cartId: number) {
    try {
      await removeCartItem(cartId)
      cartList.value = cartList.value.filter(item => item.cartId !== cartId)
      selectedIds.value = selectedIds.value.filter(id => id !== cartId)
      uni.showToast({ title: '删除成功', icon: 'success' })
    } catch (e) {
      console.error('删除失败', e)
    }
  }

  // 清空购物车
  async function clearAll() {
    try {
      await clearCart()
      cartList.value = []
      selectedIds.value = []
      uni.showToast({ title: '已清空', icon: 'success' })
    } catch (e) {
      console.error('清空失败', e)
    }
  }

  // 选择/取消选择
  function toggleSelect(cartId: number) {
    const index = selectedIds.value.indexOf(cartId)
    if (index > -1) {
      selectedIds.value.splice(index, 1)
    } else {
      selectedIds.value.push(cartId)
    }
  }

  // 全选/取消全选
  function toggleSelectAll() {
    if (isAllSelected.value) {
      selectedIds.value = []
    } else {
      selectedIds.value = cartList.value.map(item => item.cartId)
    }
  }

  // 获取购物车数量（用于tabbar徽标）
  async function fetchCartCount() {
    const userStore = useUserStore()
    if (!userStore.isLoggedIn) return 0
    try {
      const count = await getCartCount()
      return count
    } catch (e) {
      return 0
    }
  }

  return {
    cartList,
    selectedIds,
    totalCount,
    selectedItems,
    selectedCount,
    selectedAmount,
    isAllSelected,
    fetchCartList,
    addProduct,
    updateQuantity,
    removeProduct,
    clearAll,
    toggleSelect,
    toggleSelectAll,
    fetchCartCount
  }
})
