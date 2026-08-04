<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import { fetchArchives } from '@/api/article'
import Loading from '@/components/common/Loading.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import type { ArchiveItem } from '@/types'
import { formatDateShort } from '@/utils/format'

const archives = ref<ArchiveItem[]>([])
const loading = ref(true)

onMounted(async () => {
  try {
    archives.value = await fetchArchives()
  } catch {
    archives.value = []
  } finally {
    loading.value = false
  }
})

function monthLabel(year: number, month: number) {
  return `${year} 年 ${month} 月`
}
</script>

<template>
  <div class="container-blog max-w-3xl">
    <header class="mb-10 text-center">
      <h1 class="text-3xl font-bold dark:text-white light:text-gray-900">📚 归档</h1>
      <p class="mt-2 text-sm text-muted">按时间浏览所有文章</p>
    </header>

    <Loading v-if="loading" text="加载归档..." />
    <EmptyState
      v-else-if="!archives.length"
      title="暂无归档"
      description="还没有任何文章"
    />
    <div v-else class="relative space-y-10">
      <div
        class="absolute top-0 bottom-0 left-[7px] w-px dark:bg-white/10 light:bg-gray-200"
      />
      <section
        v-for="group in archives"
        :key="`${group.year}-${group.month}`"
        class="relative pl-8"
      >
        <div
          class="absolute top-1.5 left-0 h-3.5 w-3.5 rounded-full border-2 border-primary bg-[#0a0a0c] light:bg-gray-50"
        />
        <h2 class="mb-4 text-lg font-semibold text-primary">
          {{ monthLabel(group.year, group.month) }}
          <span class="ml-2 text-sm font-normal text-muted">
            {{ group.count ?? group.articles?.length ?? 0 }} 篇
          </span>
        </h2>
        <ul class="space-y-3">
          <li
            v-for="article in group.articles"
            :key="article.id"
            class="flex items-baseline gap-3 text-sm"
          >
            <span class="shrink-0 text-muted">
              {{ formatDateShort(article.publishTime) }}
            </span>
            <RouterLink
              :to="`/post/${article.slug}`"
              class="transition-colors hover:text-primary dark:text-gray-300 light:text-gray-700"
            >
              {{ article.title }}
            </RouterLink>
          </li>
        </ul>
      </section>
    </div>
  </div>
</template>

<style scoped>
.text-muted {
  color: var(--color-muted, #8b8b9a);
}
</style>
