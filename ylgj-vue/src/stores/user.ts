// Pinia - 用户状态管理（JWT 认证）
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, changePassword as changePwdApi, logout as logoutApi } from '@/api/user'
import { ElMessage } from 'element-plus'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('admin_token') || '')
  const username = ref<string>(localStorage.getItem('admin_username') || '')
  const roles = ref<string[]>(JSON.parse(localStorage.getItem('admin_roles') || '[]'))
  const isLoggedIn = computed(() => !!token.value)
  // 头部/资料页展示用：取首个角色，无角色时兜底为系统管理员
  const roleLabel = computed(() => roles.value[0] || '系统管理员')

  async function login(user: string, pwd: string) {
    const res = await loginApi(user, pwd)
    if (res.flag) {
      const data = res.data as { token: string; username: string; roles?: string[] }
      token.value = data.token
      username.value = data.username || user
      roles.value = data.roles || []
      localStorage.setItem('admin_token', data.token)
      localStorage.setItem('admin_username', username.value)
      localStorage.setItem('admin_roles', JSON.stringify(roles.value))
      ElMessage.success('登录成功')
      return true
    }
    return false
  }

  async function logout() {
    // 先同步清理本地登录态，避免路由守卫读到残留 Token
    token.value = ''
    username.value = ''
    roles.value = []
    localStorage.removeItem('admin_token')
    localStorage.removeItem('admin_username')
    localStorage.removeItem('admin_roles')
    ElMessage.info('已退出登录')
    try {
      await logoutApi()
    } catch (e) {
      // 后端登出失败不影响本地清理
    }
  }

  async function changePassword(oldPwd: string, newPwd: string) {
    if (!username.value) return false
    const res = await changePwdApi(username.value, oldPwd, newPwd)
    if (res.flag) {
      // 修改密码后强制重新登录
      await logout()
    }
    return res.flag
  }

  return { token, username, roles, roleLabel, isLoggedIn, login, logout, changePassword }
})
