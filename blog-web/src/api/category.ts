import { get } from '@/api'
import type { Category } from '@/types'

export function fetchCategories() {
  return get<Category[]>('/categories')
}

export function fetchCategoryBySlug(slug: string, categories: Category[]) {
  return categories.find((c) => c.slug === slug)
}
