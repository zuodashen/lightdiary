import { get, post } from './request'
import type { Article, ArticleParam, ArticleQuery, CommonPage } from '@/types'

export function fetchArticles(params?: ArticleQuery) {
  return get<CommonPage<Article>>('/article/list', params as Record<string, unknown>)
}

export function fetchArticle(id: number) {
  return get<Article>(`/article/${id}`)
}

export function createArticle(data: ArticleParam) {
  return post<null>('/article/create', data)
}

export function updateArticle(id: number, data: ArticleParam) {
  return post<null>(`/article/update/${id}`, data)
}

export function deleteArticle(id: number) {
  return post<null>(`/article/delete/${id}`)
}

export function publishArticle(id: number) {
  return post<null>(`/article/publish/${id}`)
}

export function draftArticle(id: number) {
  return post<null>(`/article/draft/${id}`)
}

export function toggleArticleTop(id: number) {
  return post<null>(`/article/top/${id}`)
}
