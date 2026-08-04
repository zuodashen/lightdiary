<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, RouterLink } from 'vue-router'
import { fetchArticles } from '@/api/article'
import ArticleCard from '@/components/article/ArticleCard.vue'
import Pagination from '@/components/common/Pagination.vue'
import Loading from '@/components/common/Loading.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import { useSiteStore } from '@/stores/site'
import type { ArticleSummary, Category } from '@/types'

const route = useRoute()
const siteStore = useSiteStore()

const category = ref<Category | null>(null)
const articles = ref<ArticleSummary[]>([])
const pageNum = ref(1)
const totalPage = ref(1)
const total = ref(0)
const loading = ref(true)

const slug = computed(() => route.params.slug as string)

async function loadArticles() {
  loading.value = true
  category.value =
    siteStore.categories.find((c) => c.slug === slug.value) ?? null

  if (!category.value) {
    articles.value = []
    loading.value = false
    return
  }

  try {
    const result = await fetchArticles({
      pageNum: pageNum.value,
      pageSize: siteStore.perPage,
      categoryId: category.value.id,
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
      <RouterLink to="/categories" class="mb-4 inline-block text-sm text-muted hover:text-primary">
        ← 全部分类
      </RouterLink>
      <h1 v-if="category" class="text-3xl font-bold dark:text-white light:text-gray-900">
        📁 {{ category.name }}
      </h1>
      <p v-if="category?.description" class="mt-2 text-muted">
        {{ category.description }}
      </p>
    </header>

    <Loading v-if="loading" text="加载文章..." />
    <EmptyState
      v-else-if="!category"
      title="分类不存在"
      description="找不到该分类"
    />
    <EmptyState
      v-else-if="!articles.length"
      title="暂无文章"
      :description="`分类「${category.name}」下还没有文章`"
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
