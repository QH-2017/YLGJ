// 文件管理 API
import request from './index'
import type { Result } from '@/types/api'

export function uploadHealthData(file: File, memberId?: string, description?: string): Promise<Result> {
  const fd = new FormData()
  fd.append('file', file)
  if (memberId) fd.append('memberId', memberId)
  if (description) fd.append('description', description)
  return request.post('/file/uploadHealthData', fd, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }).then(r => r.data)
}

export function listHealthData(): Promise<Result<any[]>> {
  return request.get('/file/listHealthData').then(r => r.data)
}

export function deleteHealthData(savedName: string): Promise<Result> {
  return request.delete(`/file/deleteHealthData/${savedName}`).then(r => r.data)
}

export function getDownloadUrl(savedName: string): string {
  return `/file/downloadHealthData/${savedName}`
}
