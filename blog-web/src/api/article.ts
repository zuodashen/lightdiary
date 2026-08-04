import { get } from '@/api'
import type {
  ArticleDetail,
  ArticleQuery,
  ArticleSummary,
  ArchiveItem,
  CommonPage,
} from '@/types'

export function fetchArticles(params: ArticleQuery) {
  return get<CommonPage<ArticleSummary>>('/articles', params as Record<string, unknown>)
}

export function fetchArticleBySlug(slug: string) {
  return get<ArticleDetail>(`/articles/${slug}`)
}

export function fetchHotArticles(limit = 5) {
  return get<ArticleSummary[]>('/articles/hot', { limit })
}

export function fetchArchives() {
  return get<ArchiveItem[]>('/articles/archives')
}
