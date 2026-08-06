<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import { fetchHotArticles } from '@/api/article'
import { useSiteStore } from '@/stores/site'
import SiteStatsWidget from '@/components/sidebar/SiteStatsWidget.vue'
import type { ArticleSummary } from '@/types'

defineProps<{
  showAnnouncement?: boolean
}>()

const siteStore = useSiteStore()
const hotArticles = ref<ArticleSummary[]>([])
const loadingHot = ref(true)

onMounted(async () => {
  try {
    hotArticles.value = await fetchHotArticles(5)
  } catch {
    hotArticles.value = []
  } finally {
    loadingHot.value = false
  }
})
</script>

<template>
  <aside class="space-y-5">
    <SiteStatsWidget />

    <section
      v-if="showAnnouncement && siteStore.sidebarAnnouncement"
      class="card p-5"
    >
      <h3 class="mb-2 flex items-center gap-2 text-sm font-semibold dark:text-gray-200 light:text-gray-800">
        <span class="text-primary">📢</span>
        公告
      </h3>
      <p class="text-sm leading-relaxed text-muted">
        {{ siteStore.sidebarAnnouncement }}
      </p>
    </section>

    <section class="card p-5">
      <h3 class="mb-4 text-sm font-semibold dark:text-gray-200 light:text-gray-800">
        🔥 热门文章
      </h3>
      <div v-if="loadingHot" class="space-y-3">
        <div
          v-for="i in 3"
          :key="i"
          class="h-4 animate-pulse rounded bg-white/5"
        />
      </div>
      <ul v-else-if="hotArticles.length" class="space-y-3">
        <li v-for="(article, index) in hotArticles" :key="article.id">
          <RouterLink
            :to="`/post/${article.slug}`"
            class="group flex gap-3 text-sm transition-colors hover:text-primary"
          >
            <span
              class="flex h-5 w-5 shrink-0 items-center justify-center rounded text-xs font-bold"
              :class="
                index < 3
                  ? 'bg-primary/20 text-primary'
                  : 'dark:bg-white/6 dark:text-gray-400 light:bg-gray-100 light:text-gray-500'
              "
            >
              {{ index + 1 }}
            </span>
            <span class="line-clamp-2 dark:text-gray-300 light:text-gray-700 group-hover:text-primary">
              {{ article.title }}
            </span>
          </RouterLink>
        </li>
      </ul>
      <p v-else class="text-sm text-muted">暂无热门文章</p>
    </section>

    <section class="card p-5">
      <h3 class="mb-4 text-sm font-semibold dark:text-gray-200 light:text-gray-800">
        🏷️ 标签云
      </h3>
      <div v-if="siteStore.tags.length" class="flex flex-wrap gap-2">
        <RouterLink
          v-for="tag in siteStore.tags.slice(0, 20)"
          :key="tag.id"
          :to="`/tags/${tag.slug}`"
          class="tag-pill"
        >
          {{ tag.name }}
          <span v-if="tag.articleCount !== undefined" class="ml-1 opacity-60">
            {{ tag.articleCount }}
          </span>
        </RouterLink>
      </div>
      <p v-else class="text-sm text-muted">暂无标签</p>
    </section>
  </aside>
</template>

<style scoped>
.text-muted {
  color: var(--color-muted, #8b8b9a);
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
