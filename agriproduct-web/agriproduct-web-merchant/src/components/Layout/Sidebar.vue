<template>
  <div class="sidebar-container">
    <div class="logo">
      <img src="@/assets/logo.png" alt="logo" class="logo-img" />
      <span v-show="!collapsed" class="logo-text">商家后台</span>
    </div>
    <el-menu
      :default-active="activeMenu"
      :collapse="collapsed"
      :unique-opened="true"
      background-color="#304156"
      text-color="#bfcbd9"
      active-text-color="#409EFF"
      router
    >
      <template v-for="route in menuRoutes" :key="route.path">
        <!-- 单级菜单 -->
        <el-menu-item
          v-if="!route.children || route.children.length === 1"
          :index="getMenuIndex(route)"
        >
          <el-icon v-if="getIcon(route)">
            <component :is="getIcon(route)" />
          </el-icon>
          <template #title>{{ getTitle(route) }}</template>
        </el-menu-item>
        
        <!-- 多级菜单 -->
        <el-sub-menu v-else :index="route.path">
          <template #title>
            <el-icon v-if="route.meta?.icon">
              <component :is="route.meta.icon" />
            </el-icon>
            <span>{{ route.meta?.title }}</span>
          </template>
          <el-menu-item
            v-for="child in route.children?.filter(c => !c.meta?.hidden)"
            :key="child.path"
            :index="`${route.path}/${child.path}`"
          >
            <el-icon v-if="child.meta?.icon">
              <component :is="child.meta.icon" />
            </el-icon>
            <span>{{ child.meta?.title }}</span>
          </el-menu-item>
        </el-sub-menu>
      </template>
    </el-menu>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { asyncRoutes } from '@/router/routes'
import type { RouteRecordRaw } from 'vue-router'

const route = useRoute()
const appStore = useAppStore()

const collapsed = computed(() => appStore.sidebarCollapsed)
const activeMenu = computed(() => route.path)

// 过滤需要显示的菜单
const menuRoutes = computed(() => {
  return asyncRoutes.filter(r => !r.meta?.hidden && r.path !== '/:pathMatch(.*)*' && r.path !== '/404')
})

const getMenuIndex = (route: RouteRecordRaw) => {
  if (route.children && route.children.length === 1) {
    return `${route.path}/${route.children[0].path}`.replace('//', '/')
  }
  return route.path
}

const getIcon = (route: RouteRecordRaw) => {
  if (route.children && route.children.length === 1) {
    return route.children[0].meta?.icon
  }
  return route.meta?.icon
}

const getTitle = (route: RouteRecordRaw) => {
  if (route.children && route.children.length === 1) {
    return route.children[0].meta?.title
  }
  return route.meta?.title
}
</script>

<style scoped lang="scss">
.sidebar-container {
  height: 100%;
  
  .logo {
    height: 50px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #263445;
    overflow: hidden;
    
    .logo-img {
      width: 32px;
      height: 32px;
    }
    
    .logo-text {
      margin-left: 10px;
      font-size: 16px;
      font-weight: 600;
      color: #fff;
      white-space: nowrap;
    }
  }
  
  :deep(.el-menu) {
    border-right: none;
  }
  
  :deep(.el-menu--collapse) {
    width: 64px;
  }
}
</style>
