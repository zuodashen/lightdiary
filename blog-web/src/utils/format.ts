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
