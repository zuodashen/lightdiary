<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { fetchSiteStats } from '@/api/site'
import { formatCompactNumber } from '@/utils/format'
import type { SiteStats } from '@/types'

const { variant } = withDefaults(
  defineProps<{
    variant?: 'sidebar' | 'hero'
  }>(),
  { variant: 'sidebar' },
)

const stats = ref<SiteStats | null>(null)
const loading = ref(true)

const items = [
  { key: 'articleCount', label: '文章', icon: '📝' },
  { key: 'totalWords', label: '累计字数', icon: '✍️' },
  { key: 'totalViews', label: '总阅读', icon: '👀' },
  { key: 'categoryCount', label: '分类', icon: '📂' },
  { key: 'tagCount', label: '标签', icon: '🏷️' },
  { key: 'runningDays', label: '运行天数', icon: '🌱' },
] as const

onMounted(async () => {
  try {
    stats.value = await fetchSiteStats()
  } catch {
    stats.value = null
  } finally {
    loading.value = false
  }
})

function displayValue(key: (typeof items)[number]['key']) {
  if (!stats.value) return '-'
  const value = stats.value[key]
  if (key === 'runningDays') return `${value} 天`
  return formatCompactNumber(value)
}
</script>

<template>
  <section
    class="site-stats"
    :class="variant === 'hero' ? 'site-stats-hero' : 'card p-5'"
  >
    <h3
      v-if="variant === 'sidebar'"
      class="mb-4 text-sm font-semibold dark:text-gray-200 light:text-gray-800"
    >
      📊 博客概览
    </h3>

    <div v-if="loading" class="grid gap-3" :class="variant === 'hero' ? 'grid-cols-3 sm:grid-cols-6' : 'grid-cols-2'">
      <div
        v-for="i in (variant === 'hero' ? 6 : 4)"
        :key="i"
        class="h-14 animate-pulse rounded-xl dark:bg-white/5 light:bg-black/5"
      />
    </div>

    <div
      v-else-if="stats"
      class="grid gap-3"
      :class="variant === 'hero' ? 'grid-cols-3 sm:grid-cols-6' : 'grid-cols-2'"
    >
      <div
        v-for="item in items"
        :key="item.key"
        class="stat-item"
        :class="{ 'stat-item-hero': variant === 'hero' }"
      >
        <span class="stat-icon">{{ item.icon }}</span>
        <div class="stat-value">{{ displayValue(item.key) }}</div>
        <div class="stat-label">{{ item.label }}</div>
      </div>
    </div>

    <p v-else class="text-sm text-muted">暂无统计数据</p>
  </section>
</template>

<style scoped>
.text-muted {
  color: var(--color-muted, #8b8b9a);
}

.site-stats-hero {
  margin-top: 2rem;
}

.stat-item {
  border-radius: 0.75rem;
  border-width: 1px;
  padding: 0.75rem;
  text-align: center;
  transition: all 0.3s;
}

.dark .stat-item {
  border-color: rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
}

.light .stat-item {
  border-color: rgba(0, 0, 0, 0.06);
  background: rgba(255, 255, 255, 0.6);
}

.stat-item-hero:hover {
  transform: translateY(-2px);
}

.dark .stat-item-hero:hover {
  border-color: color-mix(in srgb, var(--color-primary) 30%, transparent);
  box-shadow: 0 8px 24px color-mix(in srgb, var(--color-primary) 12%, transparent);
}

.stat-icon {
  display: block;
  font-size: 1rem;
  margin-bottom: 0.25rem;
}

.stat-value {
  font-size: 1.125rem;
  font-weight: 700;
  line-height: 1.3;
  background: linear-gradient(
    135deg,
    var(--color-secondary) 0%,
    var(--color-primary) 100%
  );
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.stat-label {
  margin-top: 0.125rem;
  font-size: 0.6875rem;
  color: var(--color-muted, #8b8b9a);
}
</style>
