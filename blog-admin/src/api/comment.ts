import { get, post } from './request'
import type { Comment, CommentConfig, CommonPage } from '@/types'

export function fetchComments(params?: {
  pageNum?: number
  pageSize?: number
  status?: string
}) {
  return get<CommonPage<Comment>>('/comment/list', params as Record<string, unknown>)
}

export function approveComment(id: number) {
  return post<null>(`/comment/approve/${id}`)
}

export function rejectComment(id: number) {
  return post<null>(`/comment/reject/${id}`)
}

export function deleteComment(id: number) {
  return post<null>(`/comment/delete/${id}`)
}

export function fetchCommentConfig() {
  return get<CommentConfig>('/comment/config')
}

export function updateCommentConfig(config: CommentConfig) {
  return post<null>('/comment/config/update', config)
}
