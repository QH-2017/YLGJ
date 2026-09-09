import request from './index'
import type { Result } from '@/types/api'

export interface MyMenu {
  id: number
  name: string
  path: string
  icon?: string
  priority?: number
}

/** 获取当前登录用户可见菜单（RBAC 动态菜单） */
export function getMyMenus(): Promise<Result<MyMenu[]>> {
  return request.get('/menu/my').then(r => r.data)
}
