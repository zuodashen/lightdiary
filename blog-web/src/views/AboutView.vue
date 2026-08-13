<script setup lang="ts">
import { computed, nextTick, onMounted, ref } from 'vue'
import { fetchPageBySlug } from '@/api/page'
import Loading from '@/components/common/Loading.vue'
import { useMermaid } from '@/composables/useMermaid'
import { useSiteStore } from '@/stores/site'
import type { PageContent } from '@/types'

const siteStore = useSiteStore()
const page = ref<PageContent | null>(null)
const loading = ref(true)
const contentRef = ref<HTMLElement | null>(null)

const fallbackLines = [
  '记录思考，沉淀时光。',
  '在代码与文字之间，寻找属于自己的光。',
  '相信长期主义，相信简洁的力量。',
]

const hasCmsContent = computed(
  () => Boolean(page.value?.contentHtml?.trim() || page.value?.content?.trim()),
)

const mermaidEnabled = computed(() => !loading.value && hasCmsContent.value)
const mermaidContentKey = computed(
  () => page.value?.contentHtml || page.value?.content || '',
)
useMermaid(contentRef, mermaidEnabled, mermaidContentKey)

onMounted(async () => {
  try {
    page.value = await fetchPageBySlug('about')
    if (page.value?.title) {
      document.title = `${page.value.title} · ${siteStore.siteTitle}`
    } else {
      document.title = `关于 · ${siteStore.siteTitle}`
    }
  } catch {
    document.title = `关于 · ${siteStore.siteTitle}`
  } finally {
    loading.value = false
    await nextTick()
  }
})
</script>

<template>
  <div class="container-blog max-w-2xl">
    <Loading v-if="loading" text="加载页面..." />

    <article v-else class="about-hero">
      <div class="relative">
        <p class="mb-3 text-xs font-medium tracking-[0.2em] uppercase text-muted">
          About
        </p>

        <h1 class="mb-10 text-3xl font-bold tracking-tight sm:text-4xl">
          <span class="gradient-text">{{ page?.title || '关于' }}</span>
        </h1>

        <div
          v-if="hasCmsContent"
          ref="contentRef"
          class="prose-blog text-left"
          v-html="page!.contentHtml || page!.content"
        />

        <div v-else class="space-y-6">
          <p
            v-for="(line, idx) in fallbackLines"
            :key="idx"
            class="text-base leading-relaxed sm:text-lg"
            :class="idx === 0 ? 'font-medium dark:text-white light:text-gray-900' : 'text-muted'"
          >
            {{ line }}
          </p>
        </div>

        <p
          v-if="siteStore.siteAuthor"
          class="mt-12 text-sm text-muted"
        >
          — {{ siteStore.siteAuthor }}
        </p>
      </div>
    </article>
  </div>
</template>

<style scoped>
.text-muted {
  color: var(--color-muted, #8b8b9a);
}
</style>
