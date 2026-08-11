export interface CommonResult<T = unknown> {
  code: number
  message: string
  data: T
}

export interface CommonPage<T> {
  pageNum: number
  pageSize: number
  totalPage: number
  total: number
  list: T[]
}

export interface LoginResult {
  token: string
  tokenHead: string
}

export interface AdminInfo {
  username: string
  icon?: string
  roles?: string[]
}

export interface TagRef {
  id: number
  name: string
  slug: string
}

export interface Article {
  id: number
  title: string
  slug: string
  summary?: string
  content?: string
  contentHtml?: string
  coverImage?: string
  categoryId?: number
  categoryName?: string
  tags?: TagRef[]
  status?: string
  views?: number
  isTop?: number
  allowComment?: number | boolean
  publishTime?: string
  createTime?: string
  updateTime?: string
}

export interface ArticleParam {
  title: string
  slug?: string
  summary?: string
  content?: string
  coverImage?: string
  categoryId?: number
  tagIds?: number[]
  status?: string
  isTop?: number
  allowComment?: number
}

export interface ArticleQuery {
  pageNum?: number
  pageSize?: number
  categoryId?: number
  tagId?: number
  keyword?: string
  status?: string
}

export interface Category {
  id: number
  name: string
  slug: string
  description?: string
  parentId?: number
  sortOrder?: number
  icon?: string
}

export interface CategoryParam {
  name: string
  slug?: string
  description?: string
  parentId?: number
  sortOrder?: number
  icon?: string
}

export interface Tag {
  id: number
  name: string
  slug: string
  articleCount?: number
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

export interface BookmarkParam {
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
}

export interface Innovation {
  id: number
  title: string
  slug?: string
  summary?: string
  description?: string
  coverImage?: string
  demoUrl?: string
  githubUrl?: string
  techStack?: string
  status?: string
  isFeatured?: number
  sortOrder?: number
  createTime?: string
  updateTime?: string
}

export interface InnovationParam {
  title: string
  slug?: string
  summary?: string
  description?: string
  coverImage?: string
  demoUrl?: string
  githubUrl?: string
  techStack?: string
  status?: string
  isFeatured?: number
  sortOrder?: number
}

export interface BlogPage {
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

export interface PageParam {
  title: string
  slug?: string
  content?: string
  template?: string
  status?: string
  sortOrder?: number
}

export interface SiteSetting {
  id: number
  settingKey: string
  settingValue: string
  description?: string
}

export interface NavItem {
  id: number
  name: string
  path?: string
  icon?: string
  parentId?: number
  sortOrder?: number
  isExternal?: number
}

export interface NavItemParam {
  name: string
  path?: string
  icon?: string
  parentId?: number
  sortOrder?: number
  isExternal?: number
}

export interface SocialLink {
  id: number
  platform: string
  icon?: string
  url: string
  sortOrder?: number
}

export interface Comment {
  id: number
  articleId: number
  parentId?: number
  authorName: string
  authorEmail?: string
  authorAvatar?: string
  content: string
  status: string
  ipAddress?: string
  userAgent?: string
  createTime?: string
}

export interface GuestbookMessage {
  id: number
  parentId?: number
  authorName: string
  authorEmail?: string
  authorAvatar?: string
  content: string
  likes?: number
  status: string
  ipAddress?: string
  createTime?: string
}

export interface CommentConfig {
  id?: number
  system: string
  configJson: string
  enabled: number
}

export interface UmsAdmin {
  id: number
  username: string
  password?: string
  icon?: string
  email?: string
  nickName?: string
  note?: string
  createTime?: string
  loginTime?: string
  status?: number
}

export interface UmsAdminParam {
  username: string
  password: string
  icon?: string
  email?: string
  nickName?: string
  note?: string
}

export interface UpdatePasswordParam {
  username: string
  oldPassword: string
  newPassword: string
}

export interface UmsRole {
  id: number
  name: string
  description?: string
  adminCount?: number
  createTime?: string
  status?: number
  sort?: number
}

export interface UmsMenu {
  id: number
  parentId?: number
  createTime?: string
  title: string
  level?: number
  sort?: number
  name?: string
  icon?: string
  hidden?: number
}

export interface UmsMenuNode extends UmsMenu {
  children?: UmsMenuNode[]
}

export interface UmsResource {
  id: number
  createTime?: string
  name: string
  url: string
  description?: string
  categoryId?: number
}

export interface UmsResourceCategory {
  id: number
  createTime?: string
  name: string
  sort?: number
}
