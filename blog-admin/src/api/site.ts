import { get, post } from './request'
import type { NavItem, NavItemParam, SiteSetting, SocialLink } from '@/types'

export function fetchSiteSettings() {
  return get<SiteSetting[]>('/siteSetting/list')
}

export function updateSiteSettings(settings: Record<string, string>) {
  return post<null>('/siteSetting/update', { settings })
}

export function fetchNavItems() {
  return get<NavItem[]>('/navItem/list')
}

export function createNavItem(data: NavItemParam) {
  return post<null>('/navItem/create', data)
}

export function updateNavItem(id: number, data: NavItemParam) {
  return post<null>(`/navItem/update/${id}`, data)
}

export function deleteNavItem(id: number) {
  return post<null>(`/navItem/delete/${id}`)
}

export function fetchSocialLinks() {
  return get<SocialLink[]>('/socialLink/list')
}

export function createSocialLink(data: SocialLink) {
  return post<null>('/socialLink/create', data)
}

export function deleteSocialLink(id: number) {
  return post<null>(`/socialLink/delete/${id}`)
}
