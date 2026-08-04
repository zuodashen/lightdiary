import { get, post } from './request'
import type { CommonPage, UmsResource, UmsResourceCategory } from '@/types'

export function fetchResources(params?: {
  categoryId?: number
  nameKeyword?: string
  urlKeyword?: string
  pageNum?: number
  pageSize?: number
}) {
  return get<CommonPage<UmsResource>>('/resource/list', params as Record<string, unknown>)
}

export function fetchAllResources() {
  return get<UmsResource[]>('/resource/listAll')
}

export function fetchResource(id: number) {
  return get<UmsResource>(`/resource/${id}`)
}

export function createResource(data: Partial<UmsResource>) {
  return post<null>('/resource/create', data)
}

export function updateResource(id: number, data: Partial<UmsResource>) {
  return post<null>(`/resource/update/${id}`, data)
}

export function deleteResource(id: number) {
  return post<null>(`/resource/delete/${id}`)
}

export function fetchResourceCategories() {
  return get<UmsResourceCategory[]>('/resourceCategory/listAll')
}

export function createResourceCategory(data: Partial<UmsResourceCategory>) {
  return post<null>('/resourceCategory/create', data)
}

export function updateResourceCategory(id: number, data: Partial<UmsResourceCategory>) {
  return post<null>(`/resourceCategory/update/${id}`, data)
}

export function deleteResourceCategory(id: number) {
  return post<null>(`/resourceCategory/delete/${id}`)
}
