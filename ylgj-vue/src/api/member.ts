// 会员管理 API
import request from './index'
import type { Member } from '@/types/models'
import type { QueryPageBean, PageResult, Result } from '@/types/api'

export function addMember(data: Member): Promise<Result> {
  return request.post('/member/add', data).then(r => r.data)
}

export function findMemberPage(params: QueryPageBean): Promise<PageResult<Member>> {
  return request.post('/member/findPage', params).then(r => r.data)
}

export function findAllMember(): Promise<Result<Member[]>> {
  return request.get('/member/findAll').then(r => r.data)
}

export function findMemberById(id: number): Promise<Result<Member>> {
  return request.get(`/member/findById/${id}`).then(r => r.data)
}

export function editMember(data: Member): Promise<Result> {
  return request.post('/member/edit', data).then(r => r.data)
}

export function deleteMember(id: number): Promise<Result> {
  return request.delete(`/member/deleteInfoById/${id}`).then(r => r.data)
}
