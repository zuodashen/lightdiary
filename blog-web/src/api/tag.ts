import { get } from '@/api'
import type { Tag } from '@/types'

export function fetchTags() {
  return get<Tag[]>('/tags')
}

export function fetchTagBySlug(slug: string, tags: Tag[]) {
  return tags.find((t) => t.slug === slug)
}
