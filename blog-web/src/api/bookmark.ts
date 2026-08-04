import { get } from '@/api'
import type { BookmarkCategory } from '@/types'

export function fetchBookmarks() {
  return get<BookmarkCategory[]>('/bookmarks')
}
