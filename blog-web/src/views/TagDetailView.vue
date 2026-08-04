<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, RouterLink } from 'vue-router'
import { fetchArticles } from '@/api/article'
import ArticleCard from '@/components/article/ArticleCard.vue'
import Pagination from '@/components/common/Pagination.vue'
import Loading from '@/components/common/Loading.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import { useSiteStore } from '@/stores/site'
import type { ArticleSummary, Tag } from '@/types'

const route = useRoute()
const siteStore = useSiteStore()

const tag = ref<Tag | null>(null)
const articles = ref<ArticleSummary[]>([])
const pageNum = ref(1)
const totalPage = ref(1)
const total = ref(0)
const loading = ref(true)

const slug = computed(() => route.params.slug as string)

async function loadArticles() {
  loading.value = true
  tag.value = siteStore.tags.find((t) => t.slug === slug.value) ?? null

  if (!tag.value) {
    articles.value = []
    loading.value = false
    return
  }

  try {
    const result = await fetchArticles({
      pageNum: pageNum.value,
      pageSize: siteStore.perPage,
      tagId: tag.value.id,
    })
    articles.value = result.list
    totalPage.value = result.totalPage
    total.value = result.total
  } catch {
    articles.value = []
  } finally {
    loading.value = false
  }
}

function onPageChange(page: number) {
  pageNum.value = page
  loadArticles()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

watch(slug, () => {
  pageNum.value = 1
  loadArticles()
})

onMounted(loadArticles)
</script>

<template>
  <div class="container-blog">
    <header class="mb-8">
      <RouterLink to="/tags" class="mb-4 inline-block text-sm text-muted hover:text-primary">
        ← 全部标签
      </RouterLink>
      <h1 v-if="tag" class="text-3xl font-bold dark:text-white light:text-gray-900">
        🏷️ #{{ tag.name }}
      </h1>
    </header>

    <Loading v-if="loading" text="加载文章..." />
    <EmptyState
      v-else-if="!tag"
      title="标签不存在"
      description="找不到该标签"
    />
    <EmptyState
      v-else-if="!articles.length"
      title="暂无文章"
      :description="`标签「${tag.name}」下还没有文章`"
    />
    <div v-else class="space-y-5">
      <ArticleCard v-for="article in articles" :key="article.id" :article="article" />
      <Pagination
        :page-num="pageNum"
        :total-page="totalPage"
        :total="total"
        @change="onPageChange"
      />
    </div>
  </div>
</template>

<style scoped>
.text-muted {
  color: var(--color-muted, #8b8b9a);
}
</style>
