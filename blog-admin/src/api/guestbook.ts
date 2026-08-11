import { get, post } from './request'
import type { CommonPage, GuestbookMessage } from '@/types'

export function fetchGuestbookList(params?: {
  pageNum?: number
  pageSize?: number
  status?: string
}) {
  return get<CommonPage<GuestbookMessage>>('/guestbook/list', params as Record<string, unknown>)
}

export function approveGuestbook(id: number) {
  return post<null>(`/guestbook/approve/${id}`)
}

export function rejectGuestbook(id: number) {
  return post<null>(`/guestbook/reject/${id}`)
}

export function deleteGuestbook(id: number) {
  return post<null>(`/guestbook/delete/${id}`)
}
