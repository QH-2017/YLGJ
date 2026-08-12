// Pinia - 应用全局状态
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  const sidebarCollapsed = ref(false)
  const globalLoading = ref(false)

  function toggleSidebar() {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }

  function setLoading(val: boolean) {
    globalLoading.value = val
  }

  return { sidebarCollapsed, globalLoading, toggleSidebar, setLoading }
})
