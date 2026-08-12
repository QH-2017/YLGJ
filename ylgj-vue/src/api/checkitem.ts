// 检查项管理 API
import request from './index'
import type { Checkitem } from '@/types/models'
import type { QueryPageBean, PageResult, Result } from '@/types/api'

export function addCheckitem(data: Checkitem): Promise<Result> {
  return request.post('/checkitem/add', data).then(r => r.data)
}

export function findCheckitemPage(params: QueryPageBean): Promise<PageResult<Checkitem>> {
  return request.post('/checkitem/findPage', params).then(r => r.data)
}

export function findAllCheckitem(): Promise<Result<Checkitem[]>> {
  return request.get('/checkitem/findAll').then(r => r.data)
}

export function findCheckitemById(id: number): Promise<Result<Checkitem>> {
  return request.get(`/checkitem/findById/${id}`).then(r => r.data)
}

export function editCheckitem(data: Checkitem): Promise<Result> {
  return request.post('/checkitem/edit', data).then(r => r.data)
}

export function deleteCheckitem(id: number): Promise<Result> {
  return request.delete(`/checkitem/deleteInfoById/${id}`).then(r => r.data)
}

// 获取检查组关联的检查项 ID 列表
export function findCheckitemIdsByCheckgroupId(groupId: number): Promise<Result<number[]>> {
  return request.get(`/checkgroup-checkitem/findCheckItemIdsByCheckGroupId/${groupId}`).then(r => r.data)
}
