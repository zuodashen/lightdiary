import { get, post } from './request'
import type { Innovation, InnovationParam } from '@/types'

export function fetchInnovations() {
  return get<Innovation[]>('/innovation/list')
}

export function createInnovation(data: InnovationParam) {
  return post<null>('/innovation/create', data)
}

export function updateInnovation(id: number, data: InnovationParam) {
  return post<null>(`/innovation/update/${id}`, data)
}

export function deleteInnovation(id: number) {
  return post<null>(`/innovation/delete/${id}`)
}
