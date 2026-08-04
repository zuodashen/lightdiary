import { get, post } from './request'
import type { Bookmark, BookmarkCategory, BookmarkParam } from '@/types'

export function fetchBookmarks() {
  return get<Bookmark[]>('/bookmark/list')
}

export function createBookmark(data: BookmarkParam) {
  return post<null>('/bookmark/create', data)
}

export function updateBookmark(id: number, data: BookmarkParam) {
  return post<null>(`/bookmark/update/${id}`, data)
}

export function deleteBookmark(id: number) {
  return post<null>(`/bookmark/delete/${id}`)
}

export function fetchBookmarkCategories() {
  return get<BookmarkCategory[]>('/bookmarkCategory/list')
}

export function createBookmarkCategory(data: {
  name: string
  icon?: string
  sortOrder?: number
}) {
  return post<null>('/bookmarkCategory/create', data)
}

export function deleteBookmarkCategory(id: number) {
  return post<null>(`/bookmarkCategory/delete/${id}`)
}
