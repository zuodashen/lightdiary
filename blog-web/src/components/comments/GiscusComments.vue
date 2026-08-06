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

const discussionsUrl = computed(() => {
  const repo = giscusConfig.value?.repo
  return repo ? `https://github.com/${repo}/discussions` : 'https://github.com/settings/notifications'
})
</script>

<template>
  <section v-if="giscusConfig" class="mt-12 border-t pt-8 dark:border-white/8 light:border-black/6">
    <h2 class="mb-3 text-lg font-semibold dark:text-gray-200 light:text-gray-800">
      💬 评论
    </h2>
    <p class="comment-tip mb-6 rounded-xl border px-4 py-3 text-sm leading-relaxed">
      评论由 GitHub 提供。留言后会收到该讨论的通知；若觉得打扰，可在
      <a
        :href="discussionsUrl"
        target="_blank"
        rel="noopener noreferrer"
        class="comment-tip-link"
      >
        GitHub 讨论页
      </a>
      点击 <strong>Unsubscribe</strong> 取消订阅，或在
      <a
        href="https://github.com/settings/notifications"
        target="_blank"
        rel="noopener noreferrer"
        class="comment-tip-link"
      >
        通知设置
      </a>
      中调整。
    </p>
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

<style scoped>
.comment-tip {
  color: var(--color-muted, #8b8b9a);
}

.dark .comment-tip {
  border-color: rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.03);
}

.light .comment-tip {
  border-color: rgba(0, 0, 0, 0.06);
  background: rgba(0, 0, 0, 0.02);
}

.comment-tip-link {
  color: var(--color-primary);
  text-decoration: underline;
  text-underline-offset: 2px;
  transition: opacity 0.2s;
}

.comment-tip-link:hover {
  opacity: 0.85;
}

.comment-tip strong {
  font-weight: 600;
  color: inherit;
}
</style>
