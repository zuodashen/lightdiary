<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import { useSiteStore } from '@/stores/site'
import { runtimeSince, resolveNavPath, isExternalUrl } from '@/utils/format'

const siteStore = useSiteStore()
const runtime = ref('')
const showBackTop = ref(false)

let timer: ReturnType<typeof setInterval> | undefined

function updateRuntime() {
  if (siteStore.siteStartTime) {
    runtime.value = runtimeSince(siteStore.siteStartTime)
  }
}

function onScroll() {
  showBackTop.value = window.scrollY > 400
}

function scrollToTop() {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const currentYear = computed(() => new Date().getFullYear())

const navLinks = computed(() =>
  siteStore.navItems.filter((item) => item.path && item.path !== '/'),
)

onMounted(() => {
  updateRuntime()
  timer = setInterval(updateRuntime, 1000)
  window.addEventListener('scroll', onScroll, { passive: true })
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
  window.removeEventListener('scroll', onScroll)
})
</script>

<template>
  <footer class="site-footer mt-auto border-t dark:border-white/8 light:border-black/6">
    <div class="container-blog py-12">
      <div class="grid gap-10 sm:grid-cols-2 lg:grid-cols-4">
        <!-- Brand -->
        <div class="lg:col-span-1">
          <div class="mb-3 flex items-center gap-2">
            <div class="footer-logo">
              {{ siteStore.siteTitle.charAt(0).toUpperCase() }}
            </div>
            <span class="font-semibold dark:text-white light:text-gray-900">
              {{ siteStore.siteTitle }}
            </span>
          </div>
          <p class="text-sm leading-relaxed text-muted">
            {{ siteStore.siteDescription }}
          </p>
          <p v-if="runtime" class="mt-3 text-xs text-muted">
            已运行
            <span class="font-medium text-primary">{{ runtime }}</span>
          </p>
        </div>

        <!-- Navigation -->
        <div>
          <h4 class="mb-4 text-sm font-semibold dark:text-gray-200 light:text-gray-800">
            导航
          </h4>
          <ul class="space-y-2">
            <li v-for="item in navLinks" :key="item.id">
              <a
                v-if="item.isExternal || isExternalUrl(item.path)"
                :href="item.path"
                target="_blank"
                rel="noopener noreferrer"
                class="footer-link"
              >
                {{ item.name }}
              </a>
              <RouterLink
                v-else
                :to="resolveNavPath(item.path)"
                class="footer-link"
              >
                {{ item.name }}
              </RouterLink>
            </li>
          </ul>
        </div>

        <!-- Contact -->
        <div>
          <h4 class="mb-4 text-sm font-semibold dark:text-gray-200 light:text-gray-800">
            联系我
          </h4>
          <ul class="space-y-2">
            <li v-for="link in siteStore.socialLinks" :key="link.id">
              <a
                :href="link.url"
                target="_blank"
                rel="noopener noreferrer"
                class="footer-link"
              >
                {{ link.platform }}
              </a>
            </li>
          </ul>
        </div>

        <!-- Guestbook -->
        <div>
          <h4 class="mb-4 text-sm font-semibold dark:text-gray-200 light:text-gray-800">
            互动交流
          </h4>
          <p class="mb-4 text-sm text-muted">
            欢迎来留言板分享想法，或通过社交链接找到我。
          </p>
          <RouterLink to="/guestbook" class="btn-primary text-sm">
            前往留言板
          </RouterLink>
        </div>
      </div>

      <div class="mt-10 flex flex-col items-center justify-between gap-4 border-t pt-6 dark:border-white/8 light:border-black/6 sm:flex-row">
        <p class="text-xs text-muted">
          © {{ currentYear }}
          <span v-if="siteStore.siteAuthor">{{ siteStore.siteAuthor }}</span>
          <span v-if="siteStore.siteTitle"> · {{ siteStore.siteTitle }}</span>
        </p>
        <p class="text-xs text-muted">
          Built with Vue 3 & Spring Boot
        </p>
      </div>
    </div>

    <Transition
      enter-active-class="transition duration-200"
      enter-from-class="opacity-0 translate-y-2"
      enter-to-class="opacity-100 translate-y-0"
      leave-active-class="transition duration-150"
      leave-from-class="opacity-100"
      leave-to-class="opacity-0 translate-y-2"
    >
      <button
        v-if="showBackTop"
        type="button"
        class="back-top-btn"
        aria-label="回到顶部"
        @click="scrollToTop"
      >
        ↑
      </button>
    </Transition>
  </footer>
</template>

<style scoped>
.text-muted {
  color: var(--color-muted, #8b8b9a);
}

.footer-logo {
  display: flex;
  height: 2rem;
  width: 2rem;
  align-items: center;
  justify-content: center;
  border-radius: 0.625rem;
  font-size: 0.875rem;
  font-weight: 700;
  color: white;
  background: linear-gradient(135deg, var(--color-secondary), var(--color-primary));
}

.footer-link {
  font-size: 0.875rem;
  color: var(--color-muted);
  transition: color 0.2s;
}

.footer-link:hover {
  color: var(--color-primary);
}

.back-top-btn {
  position: fixed;
  bottom: 2rem;
  right: 2rem;
  z-index: 30;
  display: flex;
  height: 2.75rem;
  width: 2.75rem;
  align-items: center;
  justify-content: center;
  border-radius: 9999px;
  font-size: 1.125rem;
  font-weight: 600;
  color: white;
  background: linear-gradient(135deg, var(--color-secondary), var(--color-primary));
  box-shadow: 0 8px 24px color-mix(in srgb, var(--color-primary) 35%, transparent);
  transition: transform 0.2s;
}

.back-top-btn:hover {
  transform: translateY(-2px);
}
</style>
