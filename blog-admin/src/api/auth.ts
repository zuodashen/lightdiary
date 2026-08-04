import { get, post } from './request'
import type { AdminInfo, LoginResult } from '@/types'

export function login(username: string, password: string) {
  return post<LoginResult>('/admin/login', { username, password })
}

export function getAdminInfo() {
  return get<AdminInfo>('/admin/info')
}

export function logout() {
  return post<null>('/admin/logout')
}

export function refreshToken() {
  return get<LoginResult>('/admin/refreshToken')
}
