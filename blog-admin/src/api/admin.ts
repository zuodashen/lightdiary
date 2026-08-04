import { get, post, postQuery } from './request'
import type {
  CommonPage,
  UmsAdmin,
  UmsAdminParam,
  UmsRole,
  UpdatePasswordParam,
} from '@/types'

export function registerAdmin(data: UmsAdminParam) {
  return post<UmsAdmin>('/admin/register', data)
}

export function fetchAdmins(params?: {
  keyword?: string
  pageNum?: number
  pageSize?: number
}) {
  return get<CommonPage<UmsAdmin>>('/admin/list', params as Record<string, unknown>)
}

export function fetchAdmin(id: number) {
  return get<UmsAdmin>(`/admin/${id}`)
}

export function updateAdmin(id: number, data: Partial<UmsAdmin>) {
  return post<null>(`/admin/update/${id}`, data)
}

export function deleteAdmin(id: number) {
  return post<null>(`/admin/delete/${id}`)
}

export function updateAdminStatus(id: number, status: number) {
  return postQuery<null>(`/admin/updateStatus/${id}`, { status })
}

export function updatePassword(data: UpdatePasswordParam) {
  return post<number>('/admin/updatePassword', data)
}

export function fetchAdminRoles(adminId: number) {
  return get<UmsRole[]>(`/admin/role/${adminId}`)
}

export function updateAdminRoles(adminId: number, roleIds: number[]) {
  return postQuery<null>('/admin/role/update', { adminId, roleIds })
}
