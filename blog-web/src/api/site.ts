import { get } from '@/api'
import type { CommentConfig, NavItem, SiteSettings, SocialLink } from '@/types'

export function fetchNav() {
  return get<NavItem[]>('/nav')
}

export function fetchSocial() {
  return get<SocialLink[]>('/social')
}

export function fetchSettings() {
  return get<SiteSettings>('/settings')
}

export function fetchCommentConfig() {
  return get<CommentConfig>('/comment/config')
}
