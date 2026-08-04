import { get, post } from './request'
import type { Tag } from '@/types'

export function fetchTags() {
  return get<Tag[]>('/tag/list')
}

export function createTag(name: string) {
  return post<null>('/tag/create', { name })
}

export function deleteTag(id: number) {
  return post<null>(`/tag/delete/${id}`)
}
