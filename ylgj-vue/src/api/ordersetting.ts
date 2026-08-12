// 预约设置 API
import request from './index'
import type { Ordersetting } from '@/types/models'
import type { Result } from '@/types/api'

export function findAllOrdersetting(): Promise<Result<Ordersetting[]>> {
  return request.get('/ordersetting/findAll').then(r => r.data)
}

export function addOrdersetting(data: Ordersetting): Promise<Result> {
  return request.post('/ordersetting/add', data).then(r => r.data)
}

export function editOrdersetting(data: Ordersetting): Promise<Result> {
  return request.post('/ordersetting/editNumber', data).then(r => r.data)
}
