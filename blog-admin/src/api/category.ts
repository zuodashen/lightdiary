import { get, post } from './request'
import type { Category, CategoryParam } from '@/types'

export function fetchCategories() {
  return get<Category[]>('/category/list')
}

export function createCategory(data: CategoryParam) {
  return post<null>('/category/create', data)
}

export function updateCategory(id: number, data: CategoryParam) {
  return post<null>(`/category/update/${id}`, data)
}

export function deleteCategory(id: number) {
  return post<null>(`/category/delete/${id}`)
}
