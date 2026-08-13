<script setup lang="ts">
import { RouterLink } from 'vue-router'
import type { ArticleSummary } from '@/types'
import { formatDateShort, formatReadingTime, wasUpdatedAfterPublish } from '@/utils/format'

withDefaults(
  defineProps<{
    article: ArticleSummary
    /** horizontal：有封面时左图右文；无封面纯文字 */
    layout?: 'horizontal' | 'stack'
  }>(),
  { layout: 'stack' },
)
</script>

<template>
  <article class="card card-hover group overflow-hidden">
    <RouterLink
      :to="`/post/${article.slug}`"
      class="block"
      :class="
        layout === 'horizontal' && article.coverImage
          ? 'article-h'
          : 'p-5 sm:p-6'
      "
    >
      <!-- 仅有真实封面时显示左侧图，不再用首字占位 -->
      <div
        v-if="layout === 'horizontal' && article.coverImage"
        class="article-h-thumb"
      >
        <img
          :src="article.coverImage"
          :alt="article.title"
          class="h-full w-full object-cover transition-transform duration-500 group-hover:scale-[1.04]"
          loading="lazy"
        />
      </div>

      <div :class="layout === 'horizontal' && article.coverImage ? 'article-h-body' : ''">
        <div
          v-if="layout === 'stack' && article.coverImage"
          class="mb-4 -mx-5 -mt-5 sm:-mx-6 sm:-mt-6"
        >
          <img
            :src="article.coverImage"
            :alt="article.title"
            class="h-44 w-full object-cover transition-transform duration-500 group-hover:scale-[1.02]"
            loading="lazy"
          />
        </div>

        <div class="mb-2 flex flex-wrap items-center gap-2 text-xs text-muted sm:mb-3">
          <span v-if="article.isTop" class="tag-pill !bg-primary/20 !text-primary">置顶</span>
          <span v-if="article.categoryName">{{ article.categoryName }}</span>
          <span v-if="article.publishTime">
            · {{ wasUpdatedAfterPublish(article.publishTime, article.updateTime) ? '更新' : '' }}
            {{ formatDateShort(wasUpdatedAfterPublish(article.publishTime, article.updateTime) ? article.updateTime : article.publishTime) }}
          </span>
          <span v-if="article.views !== undefined">· {{ article.views }} 阅读</span>
          <span v-if="article.readingTime || article.summary">
            · {{ formatReadingTime(article.readingTime, article.summary) }}
          </span>
        </div>

        <h2
          class="mb-2 font-semibold transition-colors group-hover:text-primary dark:text-gray-100 light:text-gray-900"
          :class="layout === 'horizontal' ? 'text-base sm:text-lg' : 'text-lg sm:text-xl'"
        >
          {{ article.title }}
        </h2>

        <p v-if="article.summary" class="line-clamp-2 text-sm leading-relaxed text-muted">
          {{ article.summary }}
        </p>

        <div v-if="article.tags?.length" class="mt-3 flex flex-wrap gap-2 sm:mt-4">
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

.article-h {
  display: flex;
  gap: 0;
  min-height: 8.5rem;
}

.article-h-thumb {
  position: relative;
  width: 8.5rem;
  flex-shrink: 0;
  overflow: hidden;
}

@media (min-width: 640px) {
  .article-h-thumb {
    width: 10.5rem;
  }
}

.article-h-body {
  flex: 1;
  min-width: 0;
  padding: 1rem 1.25rem;
}

@media (min-width: 640px) {
  .article-h-body {
    padding: 1.25rem 1.5rem;
  }
}
</style>
