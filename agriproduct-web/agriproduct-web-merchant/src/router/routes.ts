import { RouteRecordRaw } from 'vue-router'

export const constantRoutes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: {
      title: '登录',
      hidden: true
    }
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: {
      title: '页面不存在',
      hidden: true
    }
  }
]

export const asyncRoutes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/components/Layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: {
          title: '首页',
          icon: 'House'
        }
      }
    ]
  },
  {
    path: '/product',
    name: 'Product',
    component: () => import('@/components/Layout/index.vue'),
    redirect: '/product/list',
    meta: {
      title: '商品管理',
      icon: 'Goods'
    },
    children: [
      {
        path: 'list',
        name: 'ProductList',
        component: () => import('@/views/product/list.vue'),
        meta: {
          title: '商品列表',
          icon: 'List'
        }
      },
      {
        path: 'add',
        name: 'ProductAdd',
        component: () => import('@/views/product/edit.vue'),
        meta: {
          title: '添加商品',
          icon: 'Plus',
          hidden: true
        }
      },
      {
        path: 'edit/:id',
        name: 'ProductEdit',
        component: () => import('@/views/product/edit.vue'),
        meta: {
          title: '编辑商品',
          icon: 'Edit',
          hidden: true
        }
      }
    ]
  },
  {
    path: '/order',
    name: 'Order',
    component: () => import('@/components/Layout/index.vue'),
    redirect: '/order/list',
    meta: {
      title: '订单管理',
      icon: 'Document'
    },
    children: [
      {
        path: 'list',
        name: 'OrderList',
        component: () => import('@/views/order/list.vue'),
        meta: {
          title: '订单列表',
          icon: 'List'
        }
      },
      {
        path: 'detail/:id',
        name: 'OrderDetail',
        component: () => import('@/views/order/detail.vue'),
        meta: {
          title: '订单详情',
          icon: 'Document',
          hidden: true
        }
      }
    ]
  },
  {
    path: '/statistics',
    name: 'Statistics',
    component: () => import('@/components/Layout/index.vue'),
    redirect: '/statistics/index',
    meta: {
      title: '统计分析',
      icon: 'DataAnalysis'
    },
    children: [
      {
        path: 'index',
        name: 'StatisticsIndex',
        component: () => import('@/views/statistics/index.vue'),
        meta: {
          title: '销售统计',
          icon: 'TrendCharts'
        }
      }
    ]
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/components/Layout/index.vue'),
    redirect: '/profile/index',
    meta: {
      title: '个人中心',
      icon: 'User'
    },
    children: [
      {
        path: 'index',
        name: 'ProfileIndex',
        component: () => import('@/views/profile/index.vue'),
        meta: {
          title: '个人中心',
          icon: 'User'
        }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404',
    meta: {
      hidden: true
    }
  }
]
