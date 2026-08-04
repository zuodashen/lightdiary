<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { fetchBookmarks } from '@/api/bookmark'
import Loading from '@/components/common/Loading.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import type { BookmarkCategory } from '@/types'

const categories = ref<BookmarkCategory[]>([])
const loading = ref(true)

onMounted(async () => {
  try {
    categories.value = await fetchBookmarks()
  } catch {
    categories.value = []
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="container-blog">
    <header class="mb-10 text-center">
      <h1 class="text-3xl font-bold dark:text-white light:text-gray-900">🔖 书签</h1>
      <p class="mt-2 text-sm text-muted">收藏的链接与资源</p>
    </header>

    <Loading v-if="loading" text="加载书签..." />
    <EmptyState
      v-else-if="!categories.length"
      title="暂无书签"
      description="还没有添加任何书签"
    />
    <div v-else class="space-y-10">
      <section v-for="category in categories" :key="category.id">
        <h2 class="mb-4 flex items-center gap-2 text-lg font-semibold dark:text-gray-200 light:text-gray-800">
          <span>{{ category.icon ? '🔗' : '📌' }}</span>
          {{ category.name }}
        </h2>
        <div class="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
          <a
            v-for="bookmark in category.bookmarks"
            :key="bookmark.id"
            :href="bookmark.link"
            target="_blank"
            rel="noopener noreferrer"
            class="card card-hover group flex items-start gap-3 p-4"
          >
            <img
              v-if="bookmark.image"
              :src="bookmark.image"
              :alt="bookmark.name"
              class="h-10 w-10 shrink-0 rounded-lg object-cover"
              loading="lazy"
            />
            <div
              v-else
              class="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-primary/10 text-primary"
            >
              🔗
            </div>
            <div class="min-w-0 flex-1">
              <h3
                class="truncate font-medium transition-colors group-hover:text-primary dark:text-gray-200 light:text-gray-800"
              >
                {{ bookmark.name }}
              </h3>
              <p
                v-if="bookmark.description"
                class="mt-0.5 line-clamp-2 text-xs text-muted"
              >
                {{ bookmark.description }}
              </p>
            </div>
          </a>
        </div>
      </section>
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
