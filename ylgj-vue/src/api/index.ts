import axios from 'axios'
import { ElMessage } from 'element-plus'
import type { Result } from '@/types/api'

const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || '',
  timeout: 15000,
})

// 请求拦截器：自动携带 JWT Token
service.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('admin_token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    const res = response.data as Result
    if (res.flag === false) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message))
    }
    return response
  },
  (error) => {
    const status = error.response?.status
    if (status === 401) {
      // Token 失效：清除登录态并跳转登录页
      localStorage.removeItem('admin_token')
      localStorage.removeItem('admin_username')
      if (!window.location.pathname.startsWith('/admin/login')) {
        ElMessage.error('登录已过期，请重新登录')
        window.location.href = '/admin/login'
      }
    } else {
      ElMessage.error(error.response?.data?.message || error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default service
