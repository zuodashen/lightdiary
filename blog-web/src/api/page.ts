import { get } from '@/api'
import type { PageContent } from '@/types'

export function fetchPageBySlug(slug: string) {
  return get<PageContent>(`/pages/${slug}`)
}
