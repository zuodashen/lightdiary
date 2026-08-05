import axios, { type AxiosRequestConfig } from 'axios'
import type { CommonResult } from '@/types'

const SUCCESS_CODE = 200

const instance = axios.create({
  baseURL: '/lightdiary-api/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
  },
})

instance.interceptors.response.use(
  (response) => {
    const result = response.data as CommonResult
    if (result.code !== SUCCESS_CODE) {
      return Promise.reject(new Error(result.message || 'Request failed'))
    }
    return response
  },
  (error) => {
    const message =
      error.response?.data?.message || error.message || 'Network error'
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

export default instance
