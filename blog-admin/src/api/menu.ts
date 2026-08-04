import { get, post, postQuery } from './request'
import type { CommonPage, UmsMenu, UmsMenuNode } from '@/types'

export function fetchMenuTree() {
  return get<UmsMenuNode[]>('/menu/treeList')
}

export function fetchMenus(parentId: number, params?: { pageNum?: number; pageSize?: number }) {
  return get<CommonPage<UmsMenu>>(`/menu/list/${parentId}`, params as Record<string, unknown>)
}

export function fetchMenu(id: number) {
  return get<UmsMenu>(`/menu/${id}`)
}

export function createMenu(data: Partial<UmsMenu>) {
  return post<null>('/menu/create', data)
}

export function updateMenu(id: number, data: Partial<UmsMenu>) {
  return post<null>(`/menu/update/${id}`, data)
}

export function deleteMenu(id: number) {
  return post<null>(`/menu/delete/${id}`)
}

export function updateMenuHidden(id: number, hidden: number) {
  return postQuery<null>(`/menu/updateHidden/${id}`, { hidden })
}
