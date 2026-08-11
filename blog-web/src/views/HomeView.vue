<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import { fetchArticles } from '@/api/article'
import { fetchInnovations } from '@/api/innovation'
import { fetchSiteStats } from '@/api/site'
import ArticleCard from '@/components/article/ArticleCard.vue'
import Sidebar from '@/components/sidebar/Sidebar.vue'
import Pagination from '@/components/common/Pagination.vue'
import Loading from '@/components/common/Loading.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import { useSiteStore } from '@/stores/site'
import {
  formatCompactNumber,
  formatSiteStartDate,
} from '@/utils/format'
import type { ArticleSummary, Innovation, SiteStats } from '@/types'

const siteStore = useSiteStore()

const articles = ref<ArticleSummary[]>([])
const labItems = ref<Innovation[]>([])
const stats = ref<SiteStats | null>(null)
const pageNum = ref(1)
const totalPage = ref(1)
const total = ref(0)
const loading = ref(true)
const loadingLab = ref(true)

async function loadArticles() {
  loading.value = true
  try {
    const result = await fetchArticles({
      pageNum: pageNum.value,
      pageSize: siteStore.perPage,
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

async function loadLab() {
  loadingLab.value = true
  try {
    const items = await fetchInnovations()
    labItems.value = items.slice(0, 3)
  } catch {
    labItems.value = []
  } finally {
    loadingLab.value = false
  }
}

async function loadStats() {
  try {
    stats.value = await fetchSiteStats()
  } catch {
    stats.value = null
  }
}

function onPageChange(page: number) {
  pageNum.value = page
  loadArticles()
  document.getElementById('recent')?.scrollIntoView({ behavior: 'smooth' })
}

function scrollToRecent() {
  document.getElementById('recent')?.scrollIntoView({ behavior: 'smooth' })
}

function techTags(stack?: string) {
  if (!stack) return []
  return stack.split(/[,，]/).map((s) => s.trim()).filter(Boolean)
}

const statusMeta: Record<string, { label: string; class: string }> = {
  LIVE: { label: '已上线', class: 'status-live' },
  BUILDING: { label: '开发中', class: 'status-building' },
  IDEA: { label: '构思中', class: 'status-idea' },
}

onMounted(async () => {
  await Promise.all([loadArticles(), loadLab(), loadStats()])
})
</script>

<template>
  <div class="home-page">
    <!-- Hero -->
    <section class="home-hero">
      <div
        class="home-hero-bg"
        :style="siteStore.bannerImage ? { backgroundImage: `url(${siteStore.bannerImage})` } : undefined"
        aria-hidden="true"
      />
      <div class="home-hero-overlay" aria-hidden="true" />
      <div class="container-blog relative flex min-h-[70vh] flex-col items-center justify-center py-20 text-center">
        <h1 class="mb-2 text-4xl font-bold tracking-tight sm:text-5xl lg:text-6xl">
          <span class="gradient-text">{{ siteStore.bannerTitle }}</span>
        </h1>
        <p
          v-if="siteStore.bannerSubtitle"
          class="mx-auto max-w-2xl text-base leading-relaxed text-muted sm:text-lg"
        >
          {{ siteStore.bannerSubtitle }}
        </p>

        <div class="mt-8 flex flex-wrap items-center justify-center gap-4">
          <button type="button" class="btn-primary gap-2" @click="scrollToRecent">
            <span>开始阅读</span>
            <span aria-hidden="true">📖</span>
          </button>
          <RouterLink to="/lab" class="btn-outline gap-2">
            <span>探索实验室</span>
            <span aria-hidden="true">⚗️</span>
          </RouterLink>
        </div>

        <button
          type="button"
          class="scroll-hint mt-12"
          aria-label="向下滚动"
          @click="scrollToRecent"
        >
          <span class="scroll-mouse" />
        </button>
      </div>
    </section>

    <!-- Stats -->
    <section class="container-blog -mt-6 mb-12">
      <div class="stats-grid">
        <div class="stat-card">
          <span class="stat-card-icon">👀</span>
          <div class="stat-card-value">
            {{ stats ? formatCompactNumber(stats.totalViews) : '-' }}
          </div>
          <div class="stat-card-label">访问量</div>
        </div>
        <RouterLink to="/guestbook" class="stat-card stat-card-link">
          <span class="stat-card-icon">💬</span>
          <div class="stat-card-value">
            {{ stats ? formatCompactNumber(stats.messageCount ?? 0) : '-' }}
          </div>
          <div class="stat-card-label">留言数</div>
        </RouterLink>
        <div class="stat-card">
          <span class="stat-card-icon">🌱</span>
          <div class="stat-card-value">
            {{ stats ? `${stats.runningDays} 天` : '-' }}
          </div>
          <div class="stat-card-label">运行时间</div>
        </div>
        <div class="stat-card">
          <span class="stat-card-icon">📅</span>
          <div class="stat-card-value stat-card-value-sm">
            {{ formatSiteStartDate(siteStore.siteStartTime) }}
          </div>
          <div class="stat-card-label">建站时间</div>
        </div>
      </div>
    </section>

    <!-- Recent articles -->
    <section id="recent" class="container-blog mb-16 scroll-mt-24">
      <div class="section-heading mb-8">
        <p class="section-eyebrow">Recent Updates</p>
        <h2 class="text-2xl font-bold sm:text-3xl">
          <span class="gradient-text">最近更新</span>
        </h2>
      </div>

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
    </section>

    <!-- Guestbook CTA -->
    <section class="container-blog mb-16">
      <RouterLink to="/guestbook" class="guestbook-cta card block overflow-hidden">
        <div class="guestbook-cta-glow" aria-hidden="true" />
        <div class="relative flex flex-col items-start gap-4 p-6 sm:flex-row sm:items-center sm:justify-between sm:p-8">
          <div>
            <p class="section-eyebrow !mb-2">Message Board</p>
            <h2 class="text-xl font-bold dark:text-white light:text-gray-900 sm:text-2xl">
              来留言板聊聊吧
            </h2>
            <p class="mt-2 max-w-lg text-sm text-muted">
              分享你的想法、建议或问候——每一条留言都会在这里留下微光。
            </p>
          </div>
          <span class="btn-primary shrink-0">前往留言板 →</span>
        </div>
      </RouterLink>
    </section>

    <!-- Lab preview -->
    <section class="container-blog mb-16">
      <div class="section-heading mb-8 flex flex-wrap items-end justify-between gap-4">
        <div>
          <p class="section-eyebrow">Innovation Lab</p>
          <h2 class="text-2xl font-bold sm:text-3xl">
            <span class="gradient-text">⚗️ 微光实验室</span>
          </h2>
        </div>
        <RouterLink to="/lab" class="text-sm text-primary hover:underline">
          查看全部 →
        </RouterLink>
      </div>

      <Loading v-if="loadingLab" text="加载项目..." />

      <div v-else-if="labItems.length" class="grid gap-5 sm:grid-cols-2 lg:grid-cols-3">
        <article
          v-for="item in labItems"
          :key="item.id"
          class="card card-hover lab-preview-card overflow-hidden"
        >
          <div class="lab-preview-cover flex h-24 items-center justify-center text-3xl">
            ⚗️
          </div>
          <div class="p-5">
            <div class="mb-3 flex items-center justify-between gap-2">
              <span :class="['status-pill', statusMeta[item.status || 'BUILDING']?.class]">
                {{ statusMeta[item.status || 'BUILDING']?.label }}
              </span>
            </div>
            <h3 class="mb-2 font-semibold dark:text-gray-100 light:text-gray-900">
              {{ item.title }}
            </h3>
            <p v-if="item.summary" class="mb-4 line-clamp-2 text-sm text-muted">
              {{ item.summary }}
            </p>
            <div v-if="techTags(item.techStack).length" class="flex flex-wrap gap-1.5">
              <span
                v-for="tag in techTags(item.techStack).slice(0, 3)"
                :key="tag"
                class="tag-pill text-xs"
              >
                {{ tag }}
              </span>
            </div>
          </div>
        </article>
      </div>
    </section>

    <!-- About -->
    <section class="container-blog mb-8">
      <div class="about-section card overflow-hidden">
        <div class="about-section-glow" aria-hidden="true" />
        <div class="relative grid gap-8 p-6 sm:p-8 lg:grid-cols-[1fr_1.2fr] lg:items-center">
          <div class="text-center lg:text-left">
            <div
              v-if="siteStore.siteAvatar"
              class="mx-auto mb-4 h-24 w-24 overflow-hidden rounded-full ring-2 ring-primary/30 lg:mx-0"
            >
              <img
                :src="siteStore.siteAvatar"
                :alt="siteStore.siteAuthor"
                class="h-full w-full object-cover"
              />
            </div>
            <div
              v-else
              class="about-avatar mx-auto mb-4 lg:mx-0"
            >
              {{ siteStore.siteAuthor.charAt(0).toUpperCase() }}
            </div>
            <h2 class="text-2xl font-bold dark:text-white light:text-gray-900">
              {{ siteStore.siteAuthor }}
            </h2>
            <p class="mt-1 text-sm text-primary">Developer · Blogger</p>
            <p class="mt-4 text-sm leading-relaxed text-muted">
              {{ siteStore.siteDescription }}
            </p>
            <div class="mt-5 flex flex-wrap justify-center gap-3 lg:justify-start">
              <a
                v-for="link in siteStore.socialLinks"
                :key="link.id"
                :href="link.url"
                target="_blank"
                rel="noopener noreferrer"
                class="social-chip"
              >
                {{ link.platform }}
              </a>
            </div>
            <RouterLink to="/about" class="btn-ghost mt-5 inline-flex text-sm">
              了解更多 →
            </RouterLink>
          </div>

          <div>
            <h3 class="mb-4 text-sm font-semibold tracking-wide text-muted uppercase">
              技术栈
            </h3>
            <div class="tech-grid">
              <div
                v-for="tech in siteStore.aboutTechStack"
                :key="tech"
                class="tech-item"
              >
                <span class="tech-icon">⚡</span>
                <span>{{ tech }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.text-muted {
  color: var(--color-muted, #8b8b9a);
}

.home-page {
  margin-top: -0.75rem;
}

.home-hero {
  position: relative;
  overflow: hidden;
}

.home-hero-bg {
  position: absolute;
  inset: 0;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.home-hero-bg:not([style*='background-image']) {
  background:
    radial-gradient(
      ellipse 80% 60% at 30% 0%,
      color-mix(in srgb, var(--color-secondary) 28%, transparent) 0%,
      transparent 70%
    ),
    radial-gradient(
      ellipse 70% 55% at 70% 10%,
      color-mix(in srgb, var(--color-primary) 22%, transparent) 0%,
      transparent 65%
    );
}

.home-hero-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    to bottom,
    rgba(6, 6, 10, 0.35) 0%,
    rgba(6, 6, 10, 0.75) 70%,
    rgba(6, 6, 10, 1) 100%
  );
}

.light .home-hero-overlay {
  background: linear-gradient(
    to bottom,
    rgba(244, 246, 251, 0.2) 0%,
    rgba(244, 246, 251, 0.85) 70%,
    rgba(244, 246, 251, 1) 100%
  );
}

.btn-outline {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 9999px;
  border-width: 1px;
  padding: 0.625rem 1.25rem;
  font-size: 0.875rem;
  font-weight: 500;
  transition: all 0.3s;
}

.dark .btn-outline {
  border-color: rgba(255, 255, 255, 0.15);
  color: #e5e7eb;
  background: rgba(255, 255, 255, 0.04);
}

.dark .btn-outline:hover {
  border-color: color-mix(in srgb, var(--color-primary) 40%, transparent);
  background: rgba(255, 255, 255, 0.08);
}

.light .btn-outline {
  border-color: rgba(0, 0, 0, 0.1);
  color: #374151;
  background: rgba(255, 255, 255, 0.7);
}

.scroll-hint {
  animation: bounce 2s infinite;
}

.scroll-mouse {
  display: block;
  height: 2rem;
  width: 1.25rem;
  border-radius: 9999px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  position: relative;
}

.scroll-mouse::after {
  content: '';
  position: absolute;
  top: 0.375rem;
  left: 50%;
  transform: translateX(-50%);
  height: 0.375rem;
  width: 0.125rem;
  border-radius: 9999px;
  background: rgba(255, 255, 255, 0.5);
  animation: scroll-dot 2s infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(6px); }
}

@keyframes scroll-dot {
  0% { opacity: 1; transform: translateX(-50%) translateY(0); }
  100% { opacity: 0; transform: translateX(-50%) translateY(8px); }
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 0.75rem;
}

@media (min-width: 768px) {
  .stats-grid {
    grid-template-columns: repeat(4, 1fr);
    gap: 1rem;
  }
}

.stat-card {
  border-radius: 1rem;
  border-width: 1px;
  padding: 1.25rem 1rem;
  text-align: center;
  transition: all 0.3s;
}

.dark .stat-card {
  border-color: rgba(255, 255, 255, 0.08);
  background: rgba(20, 20, 24, 0.8);
  backdrop-filter: blur(12px);
}

.light .stat-card {
  border-color: rgba(0, 0, 0, 0.06);
  background: rgba(255, 255, 255, 0.85);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
}

.stat-card-link {
  cursor: pointer;
}

.stat-card-link:hover {
  transform: translateY(-2px);
  border-color: color-mix(in srgb, var(--color-primary) 35%, transparent);
}

.stat-card-icon {
  display: block;
  font-size: 1.25rem;
  margin-bottom: 0.375rem;
}

.stat-card-value {
  font-size: 1.375rem;
  font-weight: 700;
  background: linear-gradient(135deg, var(--color-secondary), var(--color-primary));
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.stat-card-value-sm {
  font-size: 1rem;
}

.stat-card-label {
  margin-top: 0.25rem;
  font-size: 0.75rem;
  color: var(--color-muted);
}

.section-eyebrow {
  margin-bottom: 0.5rem;
  font-size: 0.75rem;
  font-weight: 600;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  color: var(--color-muted);
}

.guestbook-cta {
  position: relative;
  transition: all 0.3s;
}

.guestbook-cta:hover {
  border-color: color-mix(in srgb, var(--color-primary) 35%, transparent);
  transform: translateY(-2px);
}

.guestbook-cta-glow {
  position: absolute;
  inset: 0;
  background: radial-gradient(
    ellipse at 0% 50%,
    color-mix(in srgb, var(--color-primary) 15%, transparent) 0%,
    transparent 60%
  );
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

.lab-preview-cover {
  background: linear-gradient(
    135deg,
    color-mix(in srgb, var(--color-secondary) 12%, transparent),
    color-mix(in srgb, var(--color-primary) 12%, transparent)
  );
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.about-section {
  position: relative;
}

.about-section-glow {
  position: absolute;
  inset: 0;
  background: radial-gradient(
    ellipse at 80% 20%,
    color-mix(in srgb, var(--color-primary) 12%, transparent) 0%,
    transparent 55%
  );
}

.about-avatar {
  display: flex;
  height: 6rem;
  width: 6rem;
  align-items: center;
  justify-content: center;
  border-radius: 9999px;
  font-size: 1.75rem;
  font-weight: 700;
  color: white;
  background: linear-gradient(135deg, var(--color-secondary), var(--color-primary));
  box-shadow: 0 8px 32px color-mix(in srgb, var(--color-primary) 25%, transparent);
}

.social-chip {
  border-radius: 9999px;
  padding: 0.375rem 0.875rem;
  font-size: 0.75rem;
  transition: all 0.2s;
}

.dark .social-chip {
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: #d1d5db;
}

.dark .social-chip:hover {
  border-color: color-mix(in srgb, var(--color-primary) 40%, transparent);
  color: var(--color-primary);
}

.light .social-chip {
  border: 1px solid rgba(0, 0, 0, 0.08);
  color: #4b5563;
}

.tech-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 0.75rem;
}

@media (min-width: 640px) {
  .tech-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

.tech-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.375rem;
  border-radius: 0.875rem;
  padding: 0.875rem 0.5rem;
  font-size: 0.8125rem;
  text-align: center;
  transition: all 0.2s;
}

.dark .tech-item {
  border: 1px solid rgba(255, 255, 255, 0.06);
  background: rgba(255, 255, 255, 0.03);
  color: #d1d5db;
}

.dark .tech-item:hover {
  border-color: color-mix(in srgb, var(--color-primary) 30%, transparent);
}

.light .tech-item {
  border: 1px solid rgba(0, 0, 0, 0.06);
  background: white;
  color: #374151;
}

.tech-icon {
  font-size: 1.25rem;
}
</style>
