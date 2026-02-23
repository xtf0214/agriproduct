import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore(
  'admin-app',
  () => {
    const sidebar = ref({
      opened: true,
      withoutAnimation: false
    })

    const device = ref<'desktop' | 'mobile'>('desktop')

    function toggleSideBar() {
      sidebar.value.opened = !sidebar.value.opened
      sidebar.value.withoutAnimation = false
    }

    function closeSideBar(withoutAnimation: boolean = false) {
      sidebar.value.opened = false
      sidebar.value.withoutAnimation = withoutAnimation
    }

    function toggleDevice(type: 'desktop' | 'mobile') {
      device.value = type
    }

    return {
      sidebar,
      device,
      toggleSideBar,
      closeSideBar,
      toggleDevice
    }
  },
  {
    persist: false
  }
)
