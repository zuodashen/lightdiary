<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import { useSiteStore } from '@/stores/site'
import { useThemeStore } from '@/stores/theme'
import { isExternalUrl, resolveNavPath } from '@/utils/format'

const siteStore = useSiteStore()
const themeStore = useThemeStore()
const route = useRoute()
const mobileOpen = ref(false)
const scrolled = ref(false)

function onScroll() {
  scrolled.value = window.scrollY > 20
}

function isActive(path?: string) {
  if (!path) return false
  const resolved = resolveNavPath(path)
  if (isExternalUrl(resolved)) return false
  return route.path === resolved
}

function closeMobile() {
  mobileOpen.value = false
}

onMounted(() => window.addEventListener('scroll', onScroll, { passive: true }))
onUnmounted(() => window.removeEventListener('scroll', onScroll))
</script>

<template>
  <header
    class="sticky top-0 z-40 border-b transition-all duration-300"
    :class="
      scrolled
        ? 'dark:border-white/8 dark:bg-[#0a0a0c]/90 light:border-black/6 light:bg-white/90 backdrop-blur-xl shadow-sm'
        : 'border-transparent bg-transparent'
    "
  >
    <div class="container-blog flex h-16 items-center justify-between">
      <RouterLink
        to="/"
        class="group flex items-center gap-2 transition-opacity hover:opacity-80"
        @click="closeMobile"
      >
        <div
          class="flex h-9 w-9 items-center justify-center rounded-xl text-sm font-bold text-white"
          style="background: linear-gradient(135deg, var(--color-primary), var(--color-secondary))"
        >
          {{ siteStore.siteTitle.charAt(0).toUpperCase() }}
        </div>
        <div class="hidden sm:block">
          <div class="text-sm font-semibold dark:text-white light:text-gray-900">
            {{ siteStore.siteTitle }}
          </div>
          <div v-if="siteStore.siteSubtitle" class="text-xs text-muted">
            {{ siteStore.siteSubtitle }}
          </div>
        </div>
      </RouterLink>

      <nav class="hidden items-center gap-1 md:flex">
        <template v-for="item in siteStore.navItems" :key="item.id">
          <a
            v-if="item.isExternal || isExternalUrl(item.path)"
            :href="item.path"
            target="_blank"
            rel="noopener noreferrer"
            class="btn-ghost px-3 py-2 text-sm"
          >
            {{ item.name }}
          </a>
          <RouterLink
            v-else
            :to="resolveNavPath(item.path)"
            class="btn-ghost px-3 py-2 text-sm"
            :class="{ '!text-primary font-medium': isActive(item.path) }"
          >
            {{ item.name }}
          </RouterLink>
        </template>
      </nav>

      <div class="flex items-center gap-2">
        <button
          class="btn-ghost rounded-xl p-2"
          :aria-label="themeStore.isDark ? '切换到浅色模式' : '切换到深色模式'"
          @click="themeStore.toggle()"
        >
          <svg
            v-if="themeStore.isDark"
            xmlns="http://www.w3.org/2000/svg"
            class="h-5 w-5"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="1.5"
              d="M12 3v1m0 16v1m9-9h-1M4 12H3m15.364 6.364l-.707-.707M6.343 6.343l-.707-.707m12.728 0l-.707.707M6.343 17.657l-.707.707M16 12a4 4 0 11-8 0 4 4 0 018 0z"
            />
          </svg>
          <svg
            v-else
            xmlns="http://www.w3.org/2000/svg"
            class="h-5 w-5"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="1.5"
              d="M20.354 15.354A9 9 0 018.646 3.646 9.003 9.003 0 0012 21a9.003 9.003 0 008.354-5.646z"
            />
          </svg>
        </button>

        <button
          class="btn-ghost rounded-xl p-2 md:hidden"
          aria-label="菜单"
          @click="mobileOpen = !mobileOpen"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-5 w-5"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              v-if="!mobileOpen"
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="1.5"
              d="M4 6h16M4 12h16M4 18h16"
            />
            <path
              v-else
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="1.5"
              d="M6 18L18 6M6 6l12 12"
            />
          </svg>
        </button>
      </div>
    </div>

    <Transition
      enter-active-class="transition duration-200 ease-out"
      enter-from-class="opacity-0 -translate-y-2"
      enter-to-class="opacity-100 translate-y-0"
      leave-active-class="transition duration-150 ease-in"
      leave-from-class="opacity-100 translate-y-0"
      leave-to-class="opacity-0 -translate-y-2"
    >
      <nav
        v-if="mobileOpen"
        class="border-t px-4 py-3 md:hidden dark:border-white/8 light:border-black/6"
      >
        <template v-for="item in siteStore.navItems" :key="item.id">
          <a
            v-if="item.isExternal || isExternalUrl(item.path)"
            :href="item.path"
            target="_blank"
            rel="noopener noreferrer"
            class="block rounded-lg px-3 py-2.5 text-sm dark:text-gray-300 light:text-gray-700"
            @click="closeMobile"
          >
            {{ item.name }}
          </a>
          <RouterLink
            v-else
            :to="resolveNavPath(item.path)"
            class="block rounded-lg px-3 py-2.5 text-sm"
            :class="
              isActive(item.path)
                ? 'bg-primary/10 text-primary font-medium'
                : 'dark:text-gray-300 light:text-gray-700'
            "
            @click="closeMobile"
          >
            {{ item.name }}
          </RouterLink>
        </template>
      </nav>
    </Transition>
  </header>
</template>

<style scoped>
.text-muted {
  color: var(--color-muted, #8b8b9a);
}
</style>
