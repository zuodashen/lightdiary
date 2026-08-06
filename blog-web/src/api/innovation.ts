import { get } from '@/api'
import type { Innovation } from '@/types'

export function fetchInnovations() {
  return get<Innovation[]>('/innovations')
}
