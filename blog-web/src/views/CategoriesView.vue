<script setup lang="ts">
import { computed } from 'vue'
import { RouterLink } from 'vue-router'
import { useSiteStore } from '@/stores/site'
import EmptyState from '@/components/common/EmptyState.vue'

const siteStore = useSiteStore()
const categories = computed(() => siteStore.categories)
</script>

<template>
  <div class="container-blog">
    <header class="mb-10 text-center">
      <h1 class="text-3xl font-bold dark:text-white light:text-gray-900">📁 分类</h1>
      <p class="mt-2 text-sm text-muted">浏览所有文章分类</p>
    </header>

    <EmptyState
      v-if="!categories.length"
      title="暂无分类"
      description="还没有创建任何分类"
    />
    <div v-else class="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
      <RouterLink
        v-for="category in categories"
        :key="category.id"
        :to="`/categories/${category.slug}`"
        class="card card-hover group p-6"
      >
        <div class="mb-3 flex items-center justify-between">
          <span class="text-2xl">{{ category.icon ? '📂' : '📁' }}</span>
          <span
            v-if="category.articleCount !== undefined"
            class="rounded-full bg-primary/10 px-2.5 py-0.5 text-xs font-medium text-primary"
          >
            {{ category.articleCount }} 篇
          </span>
        </div>
        <h2
          class="mb-1 text-lg font-semibold transition-colors group-hover:text-primary dark:text-gray-100 light:text-gray-900"
        >
          {{ category.name }}
        </h2>
        <p v-if="category.description" class="line-clamp-2 text-sm text-muted">
          {{ category.description }}
        </p>
      </RouterLink>
    </div>
  </div>
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
