import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { isLogin } from '@/utils/auth'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/views/layout/index.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', affix: true }
      },
      {
        path: '/user',
        name: 'User',
        component: () => import('@/views/user/list.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: '/merchant',
        name: 'Merchant',
        component: () => import('@/views/merchant/list.vue'),
        meta: { title: '商家管理' }
      },
      {
        path: '/product-audit',
        name: 'ProductAudit',
        component: () => import('@/views/product/audit.vue'),
        meta: { title: '商品审核' }
      },
      {
        path: '/banner',
        name: 'Banner',
        component: () => import('@/views/banner/list.vue'),
        meta: { title: '轮播图管理' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '404' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation guard
router.beforeEach((to, from, next) => {
  // Set page title
  document.title = (to.meta.title as string) || '农产品电商管理后台'

  // Check auth
  if (to.meta.requiresAuth !== false && !isLogin()) {
    next('/login')
  } else if (to.path === '/login' && isLogin()) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
