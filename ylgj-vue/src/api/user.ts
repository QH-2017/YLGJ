// 用户管理 API
import request from './index'
import type { User } from '@/types/models'
import type { Result } from '@/types/api'

export function login(username: string, password: string): Promise<Result> {
  return request.post('/user/login', { username, password }).then(r => r.data)
}

export function logout(): Promise<Result> {
  return request.post('/user/logout').then(r => r.data)
}

export function register(data: User): Promise<Result> {
  return request.post('/user/register', data).then(r => r.data)
}

export function changePassword(username: string, oldPassword: string, newPassword: string): Promise<Result> {
  return request.post('/user/changePassword', { username, oldPassword, newPassword }).then(r => r.data)
}

export function findAllUsers(): Promise<Result<User[]>> {
  return request.get('/user/findAll').then(r => r.data)
}

export function addUser(data: User): Promise<Result> {
  return request.post('/user/add', data).then(r => r.data)
}

export function updateUser(data: User): Promise<Result> {
  return request.post('/user/update', data).then(r => r.data)
}

export function deleteUser(id: number): Promise<Result> {
  return request.delete(`/user/delete/${id}`).then(r => r.data)
}
