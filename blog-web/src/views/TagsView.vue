<script setup lang="ts">
import { computed } from 'vue'
import { RouterLink } from 'vue-router'
import { useSiteStore } from '@/stores/site'
import EmptyState from '@/components/common/EmptyState.vue'

const siteStore = useSiteStore()
const tags = computed(() => siteStore.tags)

function tagSize(count?: number) {
  const c = count ?? 1
  if (c >= 10) return 'text-lg'
  if (c >= 5) return 'text-base'
  return 'text-sm'
}
</script>

<template>
  <div class="container-blog">
    <header class="mb-10 text-center">
      <h1 class="text-3xl font-bold dark:text-white light:text-gray-900">🏷️ 标签</h1>
      <p class="mt-2 text-sm text-muted">浏览所有文章标签</p>
    </header>

    <EmptyState
      v-if="!tags.length"
      title="暂无标签"
      description="还没有创建任何标签"
    />
    <div v-else class="flex flex-wrap justify-center gap-3">
      <RouterLink
        v-for="tag in tags"
        :key="tag.id"
        :to="`/tags/${tag.slug}`"
        class="tag-pill transition-transform hover:scale-105"
        :class="tagSize(tag.articleCount)"
      >
        #{{ tag.name }}
        <span v-if="tag.articleCount !== undefined" class="ml-1 opacity-60">
          {{ tag.articleCount }}
        </span>
      </RouterLink>
    </div>
  </div>
</template>

<style scoped>
.text-muted {
  color: var(--color-muted, #8b8b9a);
}
</style>
