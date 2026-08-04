<script setup lang="ts">
import { computed } from 'vue'
import Giscus from '@giscus/vue'
import { useSiteStore } from '@/stores/site'
import { useThemeStore } from '@/stores/theme'
import type { GiscusConfig } from '@/types'

defineProps<{
  slug: string
}>()

const siteStore = useSiteStore()
const themeStore = useThemeStore()

const giscusConfig = computed<GiscusConfig | null>(() => {
  const config = siteStore.commentConfig
  if (!config?.enabled || config.system !== 'giscus') return null
  try {
    return JSON.parse(config.configJson) as GiscusConfig
  } catch {
    return null
  }
})

const giscusTheme = computed(() =>
  themeStore.isDark ? 'dark_dimmed' : 'light',
)
</script>

<template>
  <section v-if="giscusConfig" class="mt-12 border-t pt-8 dark:border-white/8 light:border-black/6">
    <h2 class="mb-6 text-lg font-semibold dark:text-gray-200 light:text-gray-800">
      💬 评论
    </h2>
    <Giscus
      :repo="giscusConfig.repo as `${string}/${string}`"
      :repo-id="giscusConfig.repoId"
      :category="giscusConfig.category"
      :category-id="giscusConfig.categoryId"
      :mapping="(giscusConfig.mapping as 'pathname' | 'url' | 'title' | 'og:title') || 'pathname'"
      :term="slug"
      :theme="giscusTheme"
      :lang="giscusConfig.lang || 'zh-CN'"
      :reactions-enabled="giscusConfig.reactionsEnabled !== false ? '1' : '0'"
      emit-metadata="0"
      input-position="top"
      loading="lazy"
    />
  </section>
</template>
