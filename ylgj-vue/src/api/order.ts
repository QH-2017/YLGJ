// 预约订单 API
import request from './index'
import type { OrderEnriched, OrderSubmitParams } from '@/types/models'
import type { Result } from '@/types/api'

export function findAllOrders(): Promise<Result<OrderEnriched[]>> {
  return request.get('/order/findAll').then(r => r.data)
}

export function findOrderById(id: number): Promise<Result> {
  return request.get(`/order/findById/${id}`).then(r => r.data)
}

export function findOrdersByPhone(phone: string): Promise<Result<OrderEnriched[]>> {
  return request.get('/order/findByPhone', { params: { phone } }).then(r => r.data)
}

export function submitOrder(params: OrderSubmitParams): Promise<Result<number>> {
  return request.post('/order/submit', params).then(r => r.data)
}

export function updateOrder(params: { orderId: string; orderDate?: string; orderStatus?: string }): Promise<Result> {
  return request.post('/order/update', params).then(r => r.data)
}

export function cancelOrder(orderId: number): Promise<Result> {
  return request.post('/order/cancel', { orderId: String(orderId) }).then(r => r.data)
}
