export function formatDate(dateStr?: string): string {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  if (Number.isNaN(date.getTime())) return dateStr
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  })
}

/** 更新时间与发布时间相差超过 1 分钟则视为有实质更新 */
export function wasUpdatedAfterPublish(publishTime?: string, updateTime?: string): boolean {
  if (!publishTime || !updateTime) return false
  const publish = new Date(publishTime).getTime()
  const updated = new Date(updateTime).getTime()
  if (Number.isNaN(publish) || Number.isNaN(updated)) return false
  return updated - publish > 60_000
}

export function formatDateShort(dateStr?: string): string {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  if (Number.isNaN(date.getTime())) return dateStr
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  })
}

export function formatDateTime(dateStr?: string): string {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  if (Number.isNaN(date.getTime())) return dateStr
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}

export function formatSiteStartDate(dateStr?: string): string {
  if (!dateStr) return '-'
  const date = new Date(dateStr.replace(/-/g, '/'))
  if (Number.isNaN(date.getTime())) return dateStr.slice(0, 10)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  })
}

export function avatarLetter(avatar?: string, name?: string): string {
  if (avatar?.startsWith('letter:')) return avatar.slice(7).toUpperCase()
  if (name) return name.trim().charAt(0).toUpperCase()
  return '?'
}

export function isLetterAvatar(avatar?: string): boolean {
  return !avatar || avatar.startsWith('letter:')
}

export function runtimeSince(startTimeStr: string): string {
  const start = new Date(startTimeStr.replace(/-/g, '/'))
  if (Number.isNaN(start.getTime())) return ''
  const now = new Date()
  const diff = now.getTime() - start.getTime()
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
  const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
  const seconds = Math.floor((diff % (1000 * 60)) / 1000)
  return `${days} 天 ${hours} 时 ${minutes} 分 ${seconds} 秒`
}

export function resolveNavPath(path?: string): string {
  if (!path) return '/'
  if (path.startsWith('http://') || path.startsWith('https://')) return path
  return path.startsWith('/') ? path : `/${path}`
}

export function isExternalUrl(path?: string): boolean {
  if (!path) return false
  return path.startsWith('http://') || path.startsWith('https://')
}

export function formatCompactNumber(value?: number): string {
  if (value === undefined || value === null) return '0'
  if (value >= 10000) {
    const wan = value / 10000
    return `${wan >= 10 ? Math.round(wan) : wan.toFixed(1).replace(/\.0$/, '')} 万`
  }
  return value.toLocaleString('zh-CN')
}

export function formatReadingTime(minutes?: number, fallbackText?: string): string {
  if (minutes && minutes > 0) return `${minutes} 分钟`
  if (!fallbackText) return ''
  const plain = fallbackText
    .replace(/```[\s\S]*?```/g, '')
    .replace(/`[^`]+`/g, '')
    .replace(/[#>*\-\[\]()!_\s]/g, '')
  if (!plain.length) return ''
  return `${Math.max(1, Math.ceil(plain.length / 400))} 分钟`
}
