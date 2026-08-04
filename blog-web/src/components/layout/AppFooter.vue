<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useSiteStore } from '@/stores/site'
import { runtimeSince } from '@/utils/format'

const siteStore = useSiteStore()
const runtime = ref('')

let timer: ReturnType<typeof setInterval> | undefined

function updateRuntime() {
  if (siteStore.siteStartTime) {
    runtime.value = runtimeSince(siteStore.siteStartTime)
  }
}

onMounted(() => {
  updateRuntime()
  timer = setInterval(updateRuntime, 1000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})

const currentYear = computed(() => new Date().getFullYear())
</script>

<template>
  <footer
    class="mt-auto border-t py-8 dark:border-white/8 light:border-black/6"
  >
    <div class="container-blog">
      <div class="flex flex-col items-center gap-4 text-center">
        <div class="flex flex-wrap items-center justify-center gap-4">
          <a
            v-for="link in siteStore.socialLinks"
            :key="link.id"
            :href="link.url"
            target="_blank"
            rel="noopener noreferrer"
            class="text-sm text-muted transition-colors hover:text-primary"
          >
            {{ link.platform }}
          </a>
        </div>

        <p v-if="runtime" class="text-xs text-muted">
          本站已运行
          <span class="font-medium text-primary">{{ runtime }}</span>
        </p>

        <p class="text-xs text-muted">
          © {{ currentYear }}
          <span v-if="siteStore.siteAuthor">{{ siteStore.siteAuthor }}</span>
          <span v-if="siteStore.siteTitle"> · {{ siteStore.siteTitle }}</span>
        </p>

        <p v-if="siteStore.siteDescription" class="max-w-md text-xs text-muted">
          {{ siteStore.siteDescription }}
        </p>
      </div>
    </div>
  </footer>
</template>

<style scoped>
.text-muted {
  color: var(--color-muted, #8b8b9a);
}
</style>
