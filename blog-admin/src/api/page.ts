import { get, post } from './request'
import type { BlogPage, PageParam } from '@/types'

export function fetchPages() {
  return get<BlogPage[]>('/page/list')
}

export function createPage(data: PageParam) {
  return post<null>('/page/create', data)
}

export function updatePage(id: number, data: PageParam) {
  return post<null>(`/page/update/${id}`, data)
}

export function deletePage(id: number) {
  return post<null>(`/page/delete/${id}`)
}
