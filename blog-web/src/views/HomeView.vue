<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { fetchArticles } from '@/api/article'
import ArticleCard from '@/components/article/ArticleCard.vue'
import Sidebar from '@/components/sidebar/Sidebar.vue'
import Pagination from '@/components/common/Pagination.vue'
import Loading from '@/components/common/Loading.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import { useSiteStore } from '@/stores/site'
import type { ArticleSummary } from '@/types'

const siteStore = useSiteStore()
const route = useRoute()

const articles = ref<ArticleSummary[]>([])
const pageNum = ref(1)
const totalPage = ref(1)
const total = ref(0)
const loading = ref(true)
const keyword = ref('')

async function loadArticles() {
  loading.value = true
  try {
    const result = await fetchArticles({
      pageNum: pageNum.value,
      pageSize: siteStore.perPage,
      keyword: keyword.value || undefined,
    })
    articles.value = result.list
    totalPage.value = result.totalPage
    total.value = result.total
  } catch {
    articles.value = []
    totalPage.value = 1
    total.value = 0
  } finally {
    loading.value = false
  }
}

function onPageChange(page: number) {
  pageNum.value = page
  loadArticles()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function onSearch() {
  pageNum.value = 1
  loadArticles()
}

watch(
  () => route.query.q,
  (q) => {
    keyword.value = typeof q === 'string' ? q : ''
    pageNum.value = 1
    loadArticles()
  },
)

onMounted(() => {
  keyword.value = typeof route.query.q === 'string' ? route.query.q : ''
  loadArticles()
})
</script>

<template>
  <div>
    <!-- Banner -->
    <section class="relative mb-10 overflow-hidden">
      <div
        class="absolute inset-0 opacity-30"
        style="background: radial-gradient(ellipse at 50% 0%, var(--color-primary) 0%, transparent 60%)"
      />
      <div class="container-blog relative py-16 text-center sm:py-20">
        <h1
          class="mb-3 text-3xl font-bold tracking-tight sm:text-4xl lg:text-5xl dark:text-white light:text-gray-900"
        >
          {{ siteStore.siteTitle }}
        </h1>
        <p
          v-if="siteStore.siteSubtitle"
          class="mx-auto max-w-xl text-base text-muted sm:text-lg"
        >
          {{ siteStore.siteSubtitle }}
        </p>

        <form class="mx-auto mt-8 max-w-md" @submit.prevent="onSearch">
          <div class="relative">
            <input
              v-model="keyword"
              type="search"
              placeholder="搜索文章..."
              class="w-full rounded-2xl border py-3 pr-4 pl-11 text-sm transition-all focus:outline-none focus:ring-2 focus:ring-primary/40 dark:border-white/10 dark:bg-white/5 dark:text-white dark:placeholder:text-gray-500 light:border-gray-200 light:bg-white light:text-gray-900"
            />
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="absolute top-1/2 left-4 h-4 w-4 -translate-y-1/2 text-muted"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
              />
            </svg>
          </div>
        </form>
      </div>
    </section>

    <div class="container-blog">
      <div class="grid gap-8 lg:grid-cols-[1fr_280px]">
        <div>
          <Loading v-if="loading" text="加载文章..." />
          <EmptyState
            v-else-if="!articles.length"
            title="暂无文章"
            description="还没有发布任何文章，稍后再来看看吧"
          />
          <div v-else class="space-y-5">
            <ArticleCard
              v-for="article in articles"
              :key="article.id"
              :article="article"
            />
          </div>
          <Pagination
            v-if="!loading && articles.length"
            :page-num="pageNum"
            :total-page="totalPage"
            :total="total"
            @change="onPageChange"
          />
        </div>
        <Sidebar show-announcement />
      </div>
    </div>
  </div>
</template>

<style scoped>
.text-muted {
  color: var(--color-muted, #8b8b9a);
}
</style>
