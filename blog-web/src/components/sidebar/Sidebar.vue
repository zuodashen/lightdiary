<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import { fetchHotArticles } from '@/api/article'
import { fetchSiteStats } from '@/api/site'
import { useSiteStore } from '@/stores/site'
import { formatCompactNumber } from '@/utils/format'
import type { ArticleSummary, SiteStats } from '@/types'

defineProps<{
  showAnnouncement?: boolean
}>()

const siteStore = useSiteStore()
const stats = ref<SiteStats | null>(null)
const hotArticles = ref<ArticleSummary[]>([])

onMounted(async () => {
  const [statsResult, hotResult] = await Promise.allSettled([
    fetchSiteStats(),
    fetchHotArticles(5),
  ])
  stats.value = statsResult.status === 'fulfilled' ? statsResult.value : null
  hotArticles.value = hotResult.status === 'fulfilled' ? hotResult.value : []
})
</script>

<template>
  <aside class="space-y-5">
    <!-- 公告 -->
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

    <!-- 热门标签 -->
    <section class="card p-5">
      <h3 class="mb-4 text-sm font-semibold dark:text-gray-200 light:text-gray-800">
        🏷️ 热门标签
      </h3>
      <div v-if="siteStore.tags.length" class="flex flex-wrap gap-2">
        <RouterLink
          v-for="tag in siteStore.tags.slice(0, 16)"
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

    <!-- 阅读排行 -->
    <section v-if="hotArticles.length" class="card p-5">
      <h3 class="mb-4 text-sm font-semibold dark:text-gray-200 light:text-gray-800">
        🔥 阅读 TOP 5
      </h3>
      <ol class="hot-article-list">
        <li v-for="(article, index) in hotArticles" :key="article.id">
          <span class="hot-rank" :class="{ 'hot-rank-top': index < 3 }">{{ index + 1 }}</span>
          <div class="hot-article-body">
            <RouterLink :to="`/post/${article.slug}`" class="hot-article-title">
              {{ article.title }}
            </RouterLink>
            <span v-if="article.views !== undefined" class="hot-article-views">
              {{ formatCompactNumber(article.views) }} 阅读
            </span>
          </div>
        </li>
      </ol>
    </section>

    <!-- 站点信息 -->
    <section class="card p-5">
      <h3 class="mb-4 text-sm font-semibold dark:text-gray-200 light:text-gray-800">
        📊 站点信息
      </h3>
      <ul class="site-info-list">
        <li>
          <span>文章</span>
          <strong>{{ stats ? formatCompactNumber(stats.articleCount) : '-' }}</strong>
        </li>
        <li>
          <span>分类</span>
          <strong>{{ stats ? formatCompactNumber(stats.categoryCount) : '-' }}</strong>
        </li>
        <li>
          <span>标签</span>
          <strong>{{ stats ? formatCompactNumber(stats.tagCount) : '-' }}</strong>
        </li>
        <li>
          <span>运行</span>
          <strong>{{ stats ? `${stats.runningDays} 天` : '-' }}</strong>
        </li>
      </ul>
    </section>
  </aside>
</template>

<style scoped>
.text-muted {
  color: var(--color-muted, #8b8b9a);
}

.site-info-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  margin: 0;
  padding: 0;
  list-style: none;
}

.site-info-list li {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 0.875rem;
  color: var(--color-muted);
}

.site-info-list strong {
  font-weight: 600;
  background: linear-gradient(135deg, var(--color-secondary), var(--color-primary));
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.hot-article-list {
  display: flex;
  flex-direction: column;
  gap: 0.875rem;
  margin: 0;
  padding: 0;
  list-style: none;
}

.hot-article-list li {
  display: flex;
  align-items: flex-start;
  gap: 0.625rem;
}

.hot-rank {
  flex-shrink: 0;
  width: 1.375rem;
  height: 1.375rem;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 0.375rem;
  font-size: 0.6875rem;
  font-weight: 700;
  color: var(--color-muted);
  background: rgba(255, 255, 255, 0.06);
}

.light .hot-rank {
  background: rgba(0, 0, 0, 0.05);
}

.hot-rank-top {
  color: #fff;
  background: linear-gradient(135deg, var(--color-secondary), var(--color-primary));
}

.hot-article-body {
  min-width: 0;
  flex: 1;
}

.hot-article-title {
  display: block;
  font-size: 0.8125rem;
  line-height: 1.45;
  font-weight: 500;
  color: inherit;
  transition: color 0.2s;
}

.hot-article-title:hover {
  color: var(--color-primary);
}

.hot-article-views {
  display: block;
  margin-top: 0.125rem;
  font-size: 0.6875rem;
  color: var(--color-muted);
}
</style>
