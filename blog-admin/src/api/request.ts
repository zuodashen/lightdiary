import axios, { type AxiosRequestConfig } from 'axios'
import type { CommonResult } from '@/types'

export const SUCCESS_CODE = 200
export const UNAUTHORIZED_CODE = 401

const TOKEN_KEY = 'lightdiary_admin_token'
const TOKEN_HEAD_KEY = 'lightdiary_admin_token_head'

function getAuthHeader(): string | undefined {
  const token = localStorage.getItem(TOKEN_KEY)
  if (!token) return undefined
  const tokenHead = localStorage.getItem(TOKEN_HEAD_KEY) || 'Bearer '
  return `${tokenHead}${token}`
}

async function handleUnauthorized() {
  const { useAuthStore } = await import('@/stores/auth')
  useAuthStore().clearSession()
  const { default: router } = await import('@/router')
  if (router.currentRoute.value.path !== '/login') {
    router.push({ path: '/login', query: { redirect: router.currentRoute.value.fullPath } })
  }
}

const instance = axios.create({
  baseURL: '/lightdiary-api',
  timeout: 20000,
  headers: {
    'Content-Type': 'application/json',
  },
})

instance.interceptors.request.use((config) => {
  const authHeader = getAuthHeader()
  if (authHeader) {
    config.headers.Authorization = authHeader
  }
  return config
})

instance.interceptors.response.use(
  (response) => {
    const result = response.data as CommonResult
    if (result.code === UNAUTHORIZED_CODE) {
      handleUnauthorized()
      return Promise.reject(new Error(result.message || '登录已过期'))
    }
    if (result.code !== SUCCESS_CODE) {
      return Promise.reject(new Error(result.message || '请求失败'))
    }
    return response
  },
  (error) => {
    if (error.response?.status === UNAUTHORIZED_CODE) {
      handleUnauthorized()
    }
    const message =
      error.response?.data?.message || error.message || '网络错误'
    return Promise.reject(new Error(message))
  },
)

export async function get<T>(
  url: string,
  params?: Record<string, unknown>,
  config?: AxiosRequestConfig,
): Promise<T> {
  const response = await instance.get<CommonResult<T>>(url, { params, ...config })
  return response.data.data
}

export async function post<T>(
  url: string,
  data?: unknown,
  config?: AxiosRequestConfig,
): Promise<T> {
  const response = await instance.post<CommonResult<T>>(url, data, config)
  return response.data.data
}

/** Spring 风格 query 参数 POST（支持 ids=1&ids=2 数组） */
export async function postQuery<T>(
  url: string,
  params: Record<string, string | number | boolean | (string | number)[] | undefined | null>,
): Promise<T> {
  const qs = new URLSearchParams()
  for (const [key, value] of Object.entries(params)) {
    if (value === undefined || value === null) continue
    if (Array.isArray(value)) {
      value.forEach((v) => qs.append(key, String(v)))
    } else {
      qs.append(key, String(value))
    }
  }
  const query = qs.toString()
  return post<T>(query ? `${url}?${query}` : url)
}

export default instance
