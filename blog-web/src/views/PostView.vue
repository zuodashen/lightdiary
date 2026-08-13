<script setup lang="ts">
import { computed, nextTick, onMounted, ref, watch } from 'vue'
import { useRoute, RouterLink } from 'vue-router'
import { fetchArticleBySlug } from '@/api/article'
import GiscusComments from '@/components/comments/GiscusComments.vue'
import Loading from '@/components/common/Loading.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import { useMermaid } from '@/composables/useMermaid'
import { useSiteStore } from '@/stores/site'
import type { ArticleDetail } from '@/types'
import { formatDate, formatCompactNumber, formatReadingTime, wasUpdatedAfterPublish, formatDateShort } from '@/utils/format'

const route = useRoute()
const siteStore = useSiteStore()

const article = ref<ArticleDetail | null>(null)
const loading = ref(true)
const error = ref('')
const contentRef = ref<HTMLElement | null>(null)
const mermaidEnabled = computed(() => !loading.value && !!article.value)
const mermaidContentKey = computed(
  () => article.value?.contentHtml || article.value?.content || '',
)

useMermaid(contentRef, mermaidEnabled, mermaidContentKey)

async function loadArticle() {
  loading.value = true
  error.value = ''
  try {
    article.value = await fetchArticleBySlug(route.params.slug as string)
    document.title = `${article.value.title} · ${siteStore.siteTitle}`
  } catch (e) {
    article.value = null
    error.value = e instanceof Error ? e.message : '加载失败'
  } finally {
    loading.value = false
    await nextTick()
  }
}

watch(() => route.params.slug, loadArticle)

onMounted(loadArticle)
</script>

<template>
  <div class="container-blog max-w-3xl">
    <Loading v-if="loading" text="加载文章..." />
    <EmptyState
      v-else-if="error || !article"
      title="文章不存在"
      :description="error || '找不到该文章'"
      icon="📄"
    >
      <RouterLink to="/" class="btn-primary mt-4">返回首页</RouterLink>
    </EmptyState>
    <article v-else>
      <header class="mb-8">
        <div class="mb-4 flex flex-wrap items-center gap-2 text-sm text-muted">
          <RouterLink
            v-if="article.categoryName"
            :to="`/categories`"
            class="tag-pill"
          >
            {{ article.categoryName }}
          </RouterLink>
          <span v-if="article.publishTime">发布于 {{ formatDate(article.publishTime) }}</span>
          <span
            v-if="wasUpdatedAfterPublish(article.publishTime, article.updateTime)"
          >
            · 更新于 {{ formatDateShort(article.updateTime) }}
          </span>
          <span v-if="article.views !== undefined">· {{ article.views }} 阅读</span>
          <span v-if="article.wordCount">· {{ formatCompactNumber(article.wordCount) }} 字</span>
          <span v-if="formatReadingTime(article.readingTime, article.content)">
            · 约 {{ formatReadingTime(article.readingTime, article.content) }}
          </span>
        </div>
        <h1 class="text-3xl font-bold tracking-tight sm:text-4xl dark:text-white light:text-gray-900">
          {{ article.title }}
        </h1>
        <p v-if="article.summary" class="mt-4 text-base text-muted">
          {{ article.summary }}
        </p>
        <div v-if="article.tags?.length" class="mt-4 flex flex-wrap gap-2">
          <RouterLink
            v-for="tag in article.tags"
            :key="tag.id"
            :to="`/tags/${tag.slug}`"
            class="tag-pill"
          >
            #{{ tag.name }}
          </RouterLink>
        </div>
      </header>

      <img
        v-if="article.coverImage"
        :src="article.coverImage"
        :alt="article.title"
        class="mb-8 w-full rounded-2xl object-cover"
      />

      <div
        ref="contentRef"
        class="prose-blog"
        v-html="article.contentHtml || article.content"
      />

      <GiscusComments
        v-if="article.allowComment !== false"
        :slug="article.slug"
      />
    </article>
  </div>
</template>

<style scoped>
.text-muted {
  color: var(--color-muted, #8b8b9a);
}
</style>
