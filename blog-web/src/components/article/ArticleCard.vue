<script setup lang="ts">
import { RouterLink } from 'vue-router'
import type { ArticleSummary } from '@/types'
import { formatDateShort } from '@/utils/format'

defineProps<{
  article: ArticleSummary
}>()
</script>

<template>
  <article class="card card-hover group overflow-hidden">
    <RouterLink :to="`/post/${article.slug}`" class="block p-5 sm:p-6">
      <div v-if="article.coverImage" class="mb-4 -mx-5 -mt-5 sm:-mx-6 sm:-mt-6">
        <img
          :src="article.coverImage"
          :alt="article.title"
          class="h-44 w-full object-cover transition-transform duration-500 group-hover:scale-[1.02]"
          loading="lazy"
        />
      </div>

      <div class="mb-3 flex flex-wrap items-center gap-2 text-xs text-muted">
        <span v-if="article.isTop" class="tag-pill !bg-primary/20 !text-primary">
          置顶
        </span>
        <span v-if="article.categoryName">{{ article.categoryName }}</span>
        <span v-if="article.publishTime">· {{ formatDateShort(article.publishTime) }}</span>
        <span v-if="article.views !== undefined">· {{ article.views }} 阅读</span>
      </div>

      <h2
        class="mb-2 text-lg font-semibold transition-colors group-hover:text-primary sm:text-xl dark:text-gray-100 light:text-gray-900"
      >
        {{ article.title }}
      </h2>

      <p
        v-if="article.summary"
        class="line-clamp-2 text-sm leading-relaxed text-muted"
      >
        {{ article.summary }}
      </p>

      <div v-if="article.tags?.length" class="mt-4 flex flex-wrap gap-2">
        <RouterLink
          v-for="tag in article.tags"
          :key="tag.id"
          :to="`/tags/${tag.slug}`"
          class="tag-pill"
          @click.stop
        >
          #{{ tag.name }}
        </RouterLink>
      </div>
    </RouterLink>
  </article>
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
