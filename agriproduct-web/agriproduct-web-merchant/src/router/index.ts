import { createRouter, createWebHistory } from 'vue-router'
// @ts-ignore
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { constantRoutes, asyncRoutes } from './routes'
import { isLoggedIn } from '@/utils/auth'
import { useUserStore } from '@/stores/user'

NProgress.configure({ showSpinner: false })

const router = createRouter({
  history: createWebHistory(),
  routes: [...constantRoutes, ...asyncRoutes]
})

// 白名单路由
const whiteList = ['/login', '/404']

router.beforeEach(async (to, _from, next) => {
  NProgress.start()
  
  const userStore = useUserStore()
  const hasToken = isLoggedIn()
  
  if (hasToken) {
    if (to.path === '/login') {
      // 已登录跳转到首页
      next({ path: '/' })
      NProgress.done()
    } else {
      // 检查是否有用户信息
      if (!userStore.userInfo) {
        try {
          await userStore.fetchUserInfo()
          next({ ...to, replace: true })
        } catch (error) {
          // 获取用户信息失败，清除token并跳转登录页
          userStore.logout()
          next(`/login?redirect=${to.path}`)
          NProgress.done()
        }
      } else {
        next()
      }
    }
  } else {
    // 未登录
    if (whiteList.includes(to.path)) {
      next()
    } else {
      next(`/login?redirect=${to.path}`)
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})

export default router
