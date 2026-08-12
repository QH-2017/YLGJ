// AI 助手 API
import request from './index'
import type { Result } from '@/types/api'

export function aiChat(message: string): Promise<Result<string>> {
  return request.post('/ai/chat', { message }).then(r => r.data)
}
