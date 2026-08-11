import { get, post } from '@/api'
import type { CommonPage, GuestbookMessage, GuestbookParam } from '@/types'

export function fetchGuestbook(pageNum = 1, pageSize = 10) {
  return get<CommonPage<GuestbookMessage>>('/guestbook', { pageNum, pageSize })
}

export function submitGuestbook(data: GuestbookParam) {
  return post<null>('/guestbook', data)
}

export function likeGuestbook(id: number) {
  return post<null>(`/guestbook/${id}/like`)
}

export function fetchGuestbookRemaining() {
  return get<{ remaining: number }>('/guestbook/remaining')
}
