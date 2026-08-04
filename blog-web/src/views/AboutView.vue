<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { fetchPageBySlug } from '@/api/page'
import Loading from '@/components/common/Loading.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import { useSiteStore } from '@/stores/site'
import type { PageContent } from '@/types'

const siteStore = useSiteStore()
const page = ref<PageContent | null>(null)
const loading = ref(true)
const error = ref('')

onMounted(async () => {
  try {
    page.value = await fetchPageBySlug('about')
    if (page.value?.title) {
      document.title = `${page.value.title} · ${siteStore.siteTitle}`
    }
  } catch (e) {
    error.value = e instanceof Error ? e.message : '加载失败'
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="container-blog max-w-3xl">
    <Loading v-if="loading" text="加载页面..." />
    <EmptyState
      v-else-if="error || !page"
      title="页面不存在"
      :description="error || '找不到关于页面'"
      icon="👤"
    />
    <article v-else>
      <header class="mb-8 text-center">
        <h1 class="text-3xl font-bold dark:text-white light:text-gray-900">
          {{ page.title }}
        </h1>
      </header>
      <div
        class="prose-blog"
        v-html="page.contentHtml || page.content"
      />
    </article>
  </div>
</template>
