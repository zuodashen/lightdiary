<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useToast } from '@/composables/useToast'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const toast = useToast()

const username = ref('')
const password = ref('')
const loading = ref(false)
const showPassword = ref(false)

watch(
  () => auth.isLoggedIn,
  (loggedIn) => {
    if (loggedIn) {
      const redirect = (route.query.redirect as string) || '/dashboard'
      router.replace(redirect)
    }
  },
  { immediate: true },
)

async function handleSubmit() {
  if (!username.value.trim() || !password.value) {
    toast.error('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    await auth.login(username.value.trim(), password.value)
    toast.success('登录成功')
    const redirect = (route.query.redirect as string) || '/dashboard'
    router.replace(redirect)
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="flex min-h-screen items-center justify-center bg-[#0a0a0c] p-4">
    <div class="absolute inset-0 overflow-hidden">
      <div class="absolute -left-32 -top-32 h-96 w-96 rounded-full bg-primary/20 blur-3xl" />
      <div class="absolute -bottom-32 -right-32 h-96 w-96 rounded-full bg-purple-900/20 blur-3xl" />
    </div>

    <div class="relative w-full max-w-md">
      <div class="mb-8 text-center">
        <div
          class="mx-auto mb-4 flex h-14 w-14 items-center justify-center rounded-2xl bg-primary text-xl font-bold text-white shadow-lg shadow-primary/30"
        >
          微
        </div>
        <h1 class="text-2xl font-bold text-white">微光博客</h1>
        <p class="mt-1 text-sm text-gray-500">管理后台登录</p>
      </div>

      <form class="admin-card space-y-5" @submit.prevent="handleSubmit">
        <div>
          <label class="admin-label" for="username">用户名</label>
          <input
            id="username"
            v-model="username"
            type="text"
            class="admin-input"
            placeholder="请输入用户名"
            autocomplete="username"
            :disabled="loading"
          />
        </div>

        <div>
          <label class="admin-label" for="password">密码</label>
          <div class="relative">
            <input
              id="password"
              v-model="password"
              :type="showPassword ? 'text' : 'password'"
              class="admin-input pr-10"
              placeholder="请输入密码"
              autocomplete="current-password"
              :disabled="loading"
            />
            <button
              type="button"
              class="absolute right-3 top-1/2 -translate-y-1/2 text-xs text-gray-500 hover:text-gray-300"
              @click="showPassword = !showPassword"
            >
              {{ showPassword ? '隐藏' : '显示' }}
            </button>
          </div>
        </div>

        <button type="submit" class="btn-primary w-full py-2.5" :disabled="loading">
          {{ loading ? '登录中...' : '登录' }}
        </button>

        <p class="text-center text-xs text-gray-500">
          默认账号见数据库 ums_admin 表（如 test/test）
        </p>
      </form>
    </div>
  </div>
</template>
