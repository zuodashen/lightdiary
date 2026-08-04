import { get, post, postQuery } from './request'
import type { CommonPage, UmsMenu, UmsResource, UmsRole } from '@/types'

export function fetchRoles(params?: {
  keyword?: string
  pageNum?: number
  pageSize?: number
}) {
  return get<CommonPage<UmsRole>>('/role/list', params as Record<string, unknown>)
}

export function fetchAllRoles() {
  return get<UmsRole[]>('/role/listAll')
}

export function createRole(data: Partial<UmsRole>) {
  return post<null>('/role/create', data)
}

export function updateRole(id: number, data: Partial<UmsRole>) {
  return post<null>(`/role/update/${id}`, data)
}

export function deleteRoles(ids: number[]) {
  return postQuery<null>('/role/delete', { ids })
}

export function updateRoleStatus(id: number, status: number) {
  return postQuery<null>(`/role/updateStatus/${id}`, { status })
}

export function fetchRoleMenus(roleId: number) {
  return get<UmsMenu[]>(`/role/listMenu/${roleId}`)
}

export function fetchRoleResources(roleId: number) {
  return get<UmsResource[]>(`/role/listResource/${roleId}`)
}

export function allocRoleMenus(roleId: number, menuIds: number[]) {
  return postQuery<number>('/role/allocMenu', { roleId, menuIds })
}

export function allocRoleResources(roleId: number, resourceIds: number[]) {
  return postQuery<number>('/role/allocResource', { roleId, resourceIds })
}
