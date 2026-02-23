import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  const sidebarCollapsed = ref(false)
  const loading = ref(false)
  
  // 切换侧边栏
  const toggleSidebar = () => {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }
  
  // 设置加载状态
  const setLoading = (status: boolean) => {
    loading.value = status
  }
  
  return {
    sidebarCollapsed,
    loading,
    toggleSidebar,
    setLoading
  }
})
