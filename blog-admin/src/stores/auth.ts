import { defineStore } from 'pinia'
import { login as loginApi, getAdminInfo, logout as logoutApi } from '@/api/auth'

const TOKEN_KEY = 'lightdiary_admin_token'
const TOKEN_HEAD_KEY = 'lightdiary_admin_token_head'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem(TOKEN_KEY) || '',
    tokenHead: localStorage.getItem(TOKEN_HEAD_KEY) || 'Bearer ',
    username: '',
    roles: [] as string[],
    initialized: false,
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    authHeader: (state) => `${state.tokenHead}${state.token}`,
  },

  actions: {
    persistToken(token: string, tokenHead: string) {
      this.token = token
      this.tokenHead = tokenHead
      localStorage.setItem(TOKEN_KEY, token)
      localStorage.setItem(TOKEN_HEAD_KEY, tokenHead)
    },

    clearSession() {
      this.token = ''
      this.tokenHead = 'Bearer '
      this.username = ''
      this.roles = []
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(TOKEN_HEAD_KEY)
    },

    async login(username: string, password: string) {
      const result = await loginApi(username, password)
      this.persistToken(result.token, result.tokenHead)
      await this.fetchUserInfo()
    },

    async fetchUserInfo() {
      if (!this.token) {
        this.initialized = true
        return
      }
      try {
        const info = await getAdminInfo()
        this.username = info.username
        this.roles = info.roles || []
      } catch {
        this.clearSession()
      } finally {
        this.initialized = true
      }
    },

    async logout() {
      try {
        await logoutApi()
      } catch {
        // ignore logout errors
      }
      this.clearSession()
    },
  },
})
