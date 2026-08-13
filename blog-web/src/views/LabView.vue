<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { fetchInnovations } from '@/api/innovation'
import Loading from '@/components/common/Loading.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import type { Innovation } from '@/types'

const items = ref<Innovation[]>([])
const loading = ref(true)
const activeFilter = ref('ALL')

const filters = [
  { value: 'ALL', label: '全部' },
  { value: 'LIVE', label: '已上线' },
  { value: 'BUILDING', label: '进行中' },
  { value: 'IDEA', label: '构思中' },
]

const statusMeta: Record<string, { label: string; class: string }> = {
  LIVE: { label: '已上线', class: 'status-live' },
  BUILDING: { label: '进行中', class: 'status-building' },
  IDEA: { label: '构思中', class: 'status-idea' },
}

const filteredItems = computed(() => {
  if (activeFilter.value === 'ALL') return items.value
  return items.value.filter((item) => item.status === activeFilter.value)
})

const featuredItem = computed(() => items.value.find((item) => item.isFeatured === 1))

function techTags(stack?: string) {
  if (!stack) return []
  return stack.split(/[,，]/).map((s) => s.trim()).filter(Boolean)
}

onMounted(async () => {
  try {
    items.value = await fetchInnovations()
  } catch {
    items.value = []
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="container-blog">
    <header class="lab-header mb-10 text-center">
      <p class="mb-2 text-xs font-medium tracking-[0.25em] uppercase text-muted">Innovation Lab</p>
      <h1 class="text-3xl font-bold sm:text-4xl">
        <span class="gradient-text">⚗️ 微光实验室</span>
      </h1>
      <p class="mx-auto mt-3 max-w-lg text-sm text-muted sm:text-base">
        实验性项目、小工具与创意探索——在这里记录那些正在发光的想法
      </p>
    </header>

    <Loading v-if="loading" text="加载项目..." />

    <EmptyState
      v-else-if="!items.length"
      title="实验室空置中"
      description="还没有创新项目，敬请期待"
      icon="⚗️"
    />

    <template v-else>
      <section v-if="featuredItem" class="mb-10">
        <div class="featured-card card overflow-hidden">
          <div class="featured-glow" aria-hidden="true" />
          <div class="relative flex flex-col gap-6 p-6 sm:flex-row sm:items-center sm:p-8">
            <div
              class="flex h-16 w-16 shrink-0 items-center justify-center rounded-2xl text-3xl"
              style="background: linear-gradient(135deg, var(--color-secondary), var(--color-primary))"
            >
              ✨
            </div>
            <div class="min-w-0 flex-1">
              <div class="mb-2 flex flex-wrap items-center gap-2">
                <span class="featured-badge">精选项目</span>
                <span :class="['status-pill', statusMeta[featuredItem.status || 'BUILDING']?.class]">
                  {{ statusMeta[featuredItem.status || 'BUILDING']?.label }}
                </span>
              </div>
              <h2 class="text-xl font-bold dark:text-white light:text-gray-900 sm:text-2xl">
                {{ featuredItem.title }}
              </h2>
              <p v-if="featuredItem.summary" class="mt-2 text-sm text-muted">
                {{ featuredItem.summary }}
              </p>
              <div v-if="techTags(featuredItem.techStack).length" class="mt-4 flex flex-wrap gap-2">
                <span
                  v-for="tag in techTags(featuredItem.techStack)"
                  :key="tag"
                  class="tag-pill"
                >
                  {{ tag }}
                </span>
              </div>
            </div>
            <div class="flex shrink-0 flex-wrap gap-2">
              <a
                v-if="featuredItem.demoUrl"
                :href="featuredItem.demoUrl"
                target="_blank"
                rel="noopener noreferrer"
                class="btn-primary"
              >
                在线体验
              </a>
              <a
                v-if="featuredItem.githubUrl"
                :href="featuredItem.githubUrl"
                target="_blank"
                rel="noopener noreferrer"
                class="btn-ghost border px-4 dark:border-white/10 light:border-black/10"
              >
                查看源码
              </a>
            </div>
          </div>
        </div>
      </section>

      <div class="mb-6 flex flex-wrap justify-center gap-2">
        <button
          v-for="filter in filters"
          :key="filter.value"
          type="button"
          class="filter-pill"
          :class="{ 'filter-pill-active': activeFilter === filter.value }"
          @click="activeFilter = filter.value"
        >
          {{ filter.label }}
        </button>
      </div>

      <div v-if="filteredItems.length" class="grid gap-5 sm:grid-cols-2 lg:grid-cols-3">
        <article
          v-for="item in filteredItems"
          :key="item.id"
          class="card card-hover lab-card overflow-hidden"
        >
          <div v-if="item.coverImage" class="h-36 shrink-0 overflow-hidden">
            <img :src="item.coverImage" :alt="item.title" class="h-full w-full object-cover" loading="lazy" />
          </div>
          <div
            v-else
            class="lab-card-cover flex h-28 shrink-0 items-center justify-center text-4xl"
          >
            ⚗️
          </div>
          <div class="lab-card-body">
            <div class="mb-3 flex items-center justify-between gap-2">
              <span :class="['status-pill', statusMeta[item.status || 'BUILDING']?.class]">
                {{ statusMeta[item.status || 'BUILDING']?.label }}
              </span>
              <span v-if="item.isFeatured" class="text-xs text-primary">✨ 精选</span>
            </div>
            <h3 class="mb-2 text-lg font-semibold dark:text-gray-100 light:text-gray-900">
              {{ item.title }}
            </h3>
            <p v-if="item.summary" class="mb-4 line-clamp-2 text-sm text-muted">
              {{ item.summary }}
            </p>

            <!-- 技术栈 + 按钮：同一底部区域，左对齐 -->
            <div class="lab-card-footer">
              <div v-if="techTags(item.techStack).length" class="lab-card-tags">
                <span
                  v-for="tag in techTags(item.techStack).slice(0, 4)"
                  :key="tag"
                  class="tag-pill text-xs"
                >
                  {{ tag }}
                </span>
              </div>
              <div v-if="item.demoUrl || item.githubUrl" class="lab-card-actions">
                <a
                  v-if="item.demoUrl"
                  :href="item.demoUrl"
                  target="_blank"
                  rel="noopener noreferrer"
                  class="btn-primary py-1.5 text-xs"
                >
                  体验
                </a>
                <a
                  v-if="item.githubUrl"
                  :href="item.githubUrl"
                  target="_blank"
                  rel="noopener noreferrer"
                  class="btn-ghost py-1.5 text-xs"
                >
                  GitHub
                </a>
              </div>
            </div>
          </div>
        </article>
      </div>

      <EmptyState
        v-else
        title="该分类暂无项目"
        description="切换其他标签看看"
        icon="🔍"
      />
    </template>
  </div>
</template>

<style scoped>
.text-muted {
  color: var(--color-muted, #8b8b9a);
}

.featured-card {
  position: relative;
}

.featured-glow {
  position: absolute;
  inset: 0;
  background: radial-gradient(
    ellipse at 20% 50%,
    color-mix(in srgb, var(--color-primary) 18%, transparent) 0%,
    transparent 60%
  );
  pointer-events: none;
}

.featured-badge {
  display: inline-flex;
  border-radius: 9999px;
  padding: 0.125rem 0.625rem;
  font-size: 0.75rem;
  font-weight: 500;
  background: linear-gradient(135deg, var(--color-secondary), var(--color-primary));
  color: white;
}

.status-pill {
  display: inline-flex;
  border-radius: 9999px;
  padding: 0.125rem 0.625rem;
  font-size: 0.6875rem;
  font-weight: 500;
}

.status-live {
  background: color-mix(in srgb, #22c55e 15%, transparent);
  color: #4ade80;
}

.status-building {
  background: color-mix(in srgb, var(--color-secondary) 15%, transparent);
  color: var(--color-secondary);
}

.status-idea {
  background: color-mix(in srgb, #f59e0b 15%, transparent);
  color: #fbbf24;
}

.light .status-live { color: #16a34a; }
.light .status-building { color: var(--color-secondary); }
.light .status-idea { color: #d97706; }

.filter-pill {
  border-radius: 9999px;
  padding: 0.375rem 1rem;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.dark .filter-pill {
  color: #9ca3af;
}

.dark .filter-pill:hover {
  background: rgba(255, 255, 255, 0.06);
  color: #e5e7eb;
}

.light .filter-pill {
  color: #6b7280;
}

.filter-pill-active {
  background: linear-gradient(135deg, var(--color-secondary), var(--color-primary));
  color: white !important;
  box-shadow: 0 4px 16px color-mix(in srgb, var(--color-primary) 25%, transparent);
}

.lab-card-cover {
  background: linear-gradient(
    135deg,
    color-mix(in srgb, var(--color-secondary) 12%, transparent),
    color-mix(in srgb, var(--color-primary) 12%, transparent)
  );
}

.lab-card {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.lab-card-body {
  display: flex;
  flex: 1;
  flex-direction: column;
  padding: 1.25rem;
}

.lab-card-footer {
  margin-top: auto;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 0.75rem;
}

.lab-card-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.375rem;
  width: 100%;
}

.lab-card-actions {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 0.5rem;
  width: 100%;
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
