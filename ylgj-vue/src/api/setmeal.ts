// 套餐管理 API
import request from './index'
import type { Setmeal } from '@/types/models'
import type { QueryPageBean, PageResult, Result } from '@/types/api'

export function addSetmeal(data: Setmeal): Promise<Result> {
  return request.post('/setmeal/add', data).then(r => r.data)
}

export function findSetmealPage(params: QueryPageBean): Promise<PageResult<Setmeal>> {
  return request.post('/setmeal/findPage', params).then(r => r.data)
}

export function findAllSetmeal(): Promise<Result<Setmeal[]>> {
  return request.get('/setmeal/findAll').then(r => r.data)
}

export function findSetmealById(id: number): Promise<Result<Setmeal>> {
  return request.get(`/setmeal/findById/${id}`).then(r => r.data)
}

export function editSetmeal(data: Setmeal): Promise<Result> {
  return request.post('/setmeal/edit', data).then(r => r.data)
}

export function deleteSetmeal(id: number): Promise<Result> {
  return request.delete(`/setmeal/deleteInfoById/${id}`).then(r => r.data)
}

export function uploadSetmealImg(file: File): Promise<Result<{ url: string }>> {
  const fd = new FormData()
  fd.append('imgFile', file)
  return request.post('/setmeal/upload.do', fd, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }).then(r => r.data)
}
