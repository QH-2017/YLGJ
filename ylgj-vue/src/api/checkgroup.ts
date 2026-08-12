// 检查组管理 API
import request from './index'
import type { Checkgroup } from '@/types/models'
import type { QueryPageBean, PageResult, Result } from '@/types/api'

export function addCheckgroup(data: Checkgroup): Promise<Result> {
  return request.post('/checkgroup/add', data).then(r => r.data)
}

export function findCheckgroupPage(params: QueryPageBean): Promise<PageResult<Checkgroup>> {
  return request.post('/checkgroup/findPage', params).then(r => r.data)
}

export function findAllCheckgroup(): Promise<Result<Checkgroup[]>> {
  return request.get('/checkgroup/findAll').then(r => r.data)
}

export function findCheckgroupById(id: number): Promise<Result<Checkgroup>> {
  return request.get(`/checkgroup/findById/${id}`).then(r => r.data)
}

export function editCheckgroup(data: Checkgroup): Promise<Result> {
  return request.post('/checkgroup/edit', data).then(r => r.data)
}

export function deleteCheckgroup(id: number): Promise<Result> {
  return request.delete(`/checkgroup/deleteInfoById/${id}`).then(r => r.data)
}
