/** API response wrapper */
export interface CommonResult<T = unknown> {
  code: number
  message: string
  data: T
}

/** Paginated list */
export interface CommonPage<T> {
  pageNum: number
  pageSize: number
  totalPage: number
  total: number
  list: T[]
}

export interface TagRef {
  id: number
  name: string
  slug: string
}

export interface ArticleSummary {
  id: number
  title: string
  slug: string
  summary?: string
  coverImage?: string
  categoryId?: number
  categoryName?: string
  tags?: TagRef[]
  views?: number
  isTop?: boolean
  publishTime?: string
  createTime?: string
}

export interface ArticleDetail extends ArticleSummary {
  content?: string
  contentHtml?: string
  allowComment?: boolean
  updateTime?: string
}

export interface Category {
  id: number
  name: string
  slug: string
  description?: string
  parentId?: number
  sortOrder?: number
  icon?: string
  articleCount?: number
}

export interface Tag {
  id: number
  name: string
  slug: string
  articleCount?: number
  createTime?: string
}

export interface ArchiveItem {
  year: number
  month: number
  count: number
  articles: ArticleSummary[]
}

export interface PageContent {
  id: number
  title: string
  slug: string
  content?: string
  contentHtml?: string
  template?: string
  status?: string
  sortOrder?: number
  createTime?: string
  updateTime?: string
}

export interface Bookmark {
  id: number
  categoryId: number
  name: string
  link: string
  description?: string
  image?: string
  sortOrder?: number
}

export interface BookmarkCategory {
  id: number
  name: string
  icon?: string
  sortOrder?: number
  bookmarks: Bookmark[]
}

export interface NavItem {
  id: number
  name: string
  path?: string
  icon?: string
  parentId?: number
  sortOrder?: number
  isExternal?: boolean
}

export interface SocialLink {
  id: number
  platform: string
  icon?: string
  url: string
  sortOrder?: number
}

export type SiteSettings = Record<string, string>

export interface GiscusConfig {
  repo: string
  repoId: string
  category: string
  categoryId: string
  mapping?: string
  lang?: string
  reactionsEnabled?: boolean
  theme?: string
}

export interface CommentConfig {
  system: string
  configJson: string
  enabled: boolean
}

export interface ArticleQuery {
  pageNum?: number
  pageSize?: number
  categoryId?: number | string
  tagId?: number | string
  keyword?: string
}
