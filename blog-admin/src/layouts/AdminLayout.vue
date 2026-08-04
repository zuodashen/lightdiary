<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import AppSidebar from '@/components/AppSidebar.vue'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const pageTitle = computed(() => (route.meta.title as string) || '管理后台')

async function handleLogout() {
  await auth.logout()
  router.push('/login')
}
</script>

<template>
  <div class="flex min-h-screen">
    <AppSidebar />

    <div class="flex min-h-screen flex-1 flex-col">
      <header
        class="sticky top-0 z-10 flex h-14 items-center justify-between border-b border-[var(--color-border)] bg-[#0a0a0c]/90 px-6 backdrop-blur"
      >
        <h1 class="text-lg font-semibold text-white">{{ pageTitle }}</h1>
        <div class="flex items-center gap-3">
          <router-link to="/account/profile" class="text-sm text-gray-400 hover:text-primary">
            {{ auth.username || '管理员' }}
          </router-link>
          <button type="button" class="btn-ghost px-3 py-1.5 text-sm" @click="handleLogout">
            退出登录
          </button>
        </div>
      </header>

      <main class="flex-1 p-6">
        <router-view />
      </main>
    </div>
  </div>
</template>
