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

/** 暂时隐藏首页「关于我 / 技术栈」板块 */
const showAboutSection = false

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
    <!-- 整页氛围背景：一直铺到内容区，避免 Hero 处硬切 -->
    <div
      class="home-atmosphere"
      :style="siteStore.bannerImage ? { backgroundImage: `url(${siteStore.bannerImage})` } : undefined"
      aria-hidden="true"
    >
      <div v-if="!siteStore.bannerImage" class="hero-scene">
        <div class="hero-stars" />
        <div class="hero-mountains" />
        <div class="hero-portal">
          <div class="portal-ring portal-ring-1" />
          <div class="portal-ring portal-ring-2" />
          <div class="portal-core" />
          <div class="portal-silhouette" />
        </div>
      </div>
      <div class="home-atmosphere-fade" />
    </div>

    <!-- Hero -->
    <section class="home-hero">
      <div class="container-blog relative z-10 flex min-h-[58vh] flex-col items-center justify-center py-12 text-center lg:min-h-[62vh] lg:py-16">
        <h1 class="hero-title gradient-text">
          {{ siteStore.bannerTitle || siteStore.siteTitle }}
        </h1>
        <div class="hero-subtitle-row">
          <span class="hero-line" />
          <span class="hero-subtitle-text">{{ siteStore.siteSubtitle || '微光日记' }}</span>
          <span class="hero-line" />
        </div>
        <p class="hero-tagline mx-auto mt-5 max-w-xl text-base leading-relaxed sm:text-lg">
          {{ siteStore.bannerSubtitle || '记录技术，也记录生活\n让一些微小的想法持续发光' }}
        </p>

        <div class="mt-8 flex flex-wrap items-center justify-center gap-4">
          <button type="button" class="btn-primary gap-2" @click="scrollToRecent">
            <span>开始阅读</span>
            <svg class="btn-icon" viewBox="0 0 24 24" fill="none" aria-hidden="true">
              <path
                d="M4 19.5A2.5 2.5 0 016.5 17H20"
                stroke="currentColor"
                stroke-width="1.75"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
              <path
                d="M6.5 2H20v20H6.5A2.5 2.5 0 014 19.5v-15A2.5 2.5 0 016.5 2z"
                stroke="currentColor"
                stroke-width="1.75"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
              <path
                d="M8 7h8M8 11h5"
                stroke="currentColor"
                stroke-width="1.75"
                stroke-linecap="round"
              />
            </svg>
          </button>
          <RouterLink to="/lab" class="btn-outline gap-2">
            <span>探索实验室</span>
            <svg class="btn-icon" viewBox="0 0 24 24" fill="none" aria-hidden="true">
              <path
                d="M9 3h6M10 3v5.2L5.4 18.1A2.2 2.2 0 007.4 21h9.2a2.2 2.2 0 002-2.9L14 8.2V3"
                stroke="currentColor"
                stroke-width="1.75"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
              <path
                d="M8.2 14h7.6"
                stroke="currentColor"
                stroke-width="1.75"
                stroke-linecap="round"
              />
            </svg>
          </RouterLink>
        </div>
      </div>

      <!-- Stats：玻璃卡片浮在氛围上 -->
      <div class="container-blog relative z-10 pb-8">
        <div class="stats-grid stats-grid-hero">
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
      </div>
    </section>

    <!-- Recent articles -->
    <section id="recent" class="home-panel container-blog relative z-10 mb-16 scroll-mt-24">
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
              layout="horizontal"
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
          <div class="lab-preview-cover">
            <img
              v-if="item.coverImage"
              :src="item.coverImage"
              :alt="item.title"
              class="h-full w-full object-cover"
              loading="lazy"
            />
            <div v-else class="lab-preview-placeholder">⚗️</div>
          </div>
          <div class="p-5">
            <div class="mb-3 flex items-center justify-between gap-2">
              <span :class="['status-pill', statusMeta[item.status || 'BUILDING']?.class]">
                {{ statusMeta[item.status || 'BUILDING']?.label }}
              </span>
              <div v-if="item.githubUrl || item.demoUrl" class="flex gap-2">
                <a
                  v-if="item.githubUrl"
                  :href="item.githubUrl"
                  target="_blank"
                  rel="noopener noreferrer"
                  class="lab-link-icon"
                  title="GitHub"
                  @click.stop
                >
                  ↗
                </a>
              </div>
            </div>
            <h3 class="mb-2 font-semibold dark:text-gray-100 light:text-gray-900">
              {{ item.title }}
            </h3>
            <p v-if="item.summary" class="mb-4 line-clamp-2 text-sm text-muted">
              {{ item.summary }}
            </p>
            <div v-if="techTags(item.techStack).length" class="mb-4 flex flex-wrap gap-1.5">
              <span
                v-for="tag in techTags(item.techStack).slice(0, 4)"
                :key="tag"
                class="tag-pill text-xs"
              >
                {{ tag }}
              </span>
            </div>
            <div v-if="item.demoUrl || item.githubUrl" class="flex flex-wrap gap-3 text-xs">
              <a
                v-if="item.demoUrl"
                :href="item.demoUrl"
                target="_blank"
                rel="noopener noreferrer"
                class="lab-action"
              >
                Demo →
              </a>
              <a
                v-if="item.githubUrl"
                :href="item.githubUrl"
                target="_blank"
                rel="noopener noreferrer"
                class="lab-action"
              >
                GitHub →
              </a>
            </div>
          </div>
        </article>
      </div>
    </section>

    <!-- About（暂时隐藏） -->
    <section v-if="showAboutSection" class="container-blog mb-8">
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
  position: relative;
  /* 把氛围背景拉到视口最顶，盖住顶栏背后的黑边 */
  margin-top: calc(-1 * var(--home-header-offset, 4.75rem));
}

/* 整页氛围：高度盖住 Hero + 最近更新，内容玻璃浮在上面 */
.home-atmosphere {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: min(135vh, 1180px);
  z-index: 0;
  pointer-events: none;
  overflow: hidden;
  background-color: #06060a;
  background-size: cover;
  background-position: center top;
  background-repeat: no-repeat;
}

.home-atmosphere:not([style*='background-image']) {
  background-image:
    radial-gradient(ellipse 90% 70% at 50% 28%, rgba(117, 9, 182, 0.38) 0%, transparent 55%),
    radial-gradient(ellipse 60% 50% at 18% 8%, rgba(1, 83, 229, 0.22) 0%, transparent 50%),
    linear-gradient(180deg, #06060a 0%, #0c0618 40%, #06060a 100%);
}

.home-atmosphere-fade {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    to bottom,
    rgba(6, 6, 10, 0.12) 0%,
    rgba(6, 6, 10, 0.05) 22%,
    rgba(6, 6, 10, 0.28) 48%,
    rgba(6, 6, 10, 0.72) 72%,
    rgba(6, 6, 10, 0.94) 88%,
    #06060a 100%
  );
}

.home-hero {
  position: relative;
  z-index: 1;
  padding-top: var(--home-header-offset, 4.75rem);
}

.home-panel {
  position: relative;
  z-index: 1;
}

.hero-scene {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
}

.hero-stars {
  position: absolute;
  inset: 0;
  background-image:
    radial-gradient(1.5px 1.5px at 12% 18%, rgba(255, 255, 255, 0.7) 0%, transparent 100%),
    radial-gradient(1px 1px at 28% 42%, rgba(255, 255, 255, 0.45) 0%, transparent 100%),
    radial-gradient(1.5px 1.5px at 48% 12%, rgba(255, 255, 255, 0.55) 0%, transparent 100%),
    radial-gradient(1px 1px at 68% 28%, rgba(255, 255, 255, 0.4) 0%, transparent 100%),
    radial-gradient(1.5px 1.5px at 82% 16%, rgba(255, 255, 255, 0.65) 0%, transparent 100%),
    radial-gradient(1px 1px at 90% 48%, rgba(255, 255, 255, 0.35) 0%, transparent 100%),
    radial-gradient(1px 1px at 8% 62%, rgba(255, 255, 255, 0.3) 0%, transparent 100%);
  animation: stars-twinkle 6s ease-in-out infinite alternate;
}

@keyframes stars-twinkle {
  from { opacity: 0.55; }
  to { opacity: 1; }
}

.hero-mountains {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 42%;
  background:
    linear-gradient(180deg, transparent 0%, rgba(20, 8, 40, 0.85) 40%, #0a0614 100%),
    radial-gradient(ellipse 120% 80% at 50% 100%, #1a0a2e 0%, transparent 70%);
  clip-path: polygon(
    0% 100%, 0% 55%, 8% 48%, 18% 58%, 28% 42%, 38% 52%, 48% 35%,
    58% 48%, 68% 38%, 78% 55%, 88% 45%, 100% 58%, 100% 100%
  );
}

.hero-portal {
  position: absolute;
  left: 50%;
  bottom: 18%;
  transform: translateX(-50%);
  width: min(42vw, 280px);
  height: min(52vw, 340px);
}

.portal-ring {
  position: absolute;
  inset: 0;
  border-radius: 50% 50% 45% 45% / 55% 55% 40% 40%;
  border: 2px solid transparent;
  background:
    linear-gradient(#06060a, #06060a) padding-box,
    linear-gradient(180deg, #c084fc, #7509b6, #0153e5) border-box;
  box-shadow:
    0 0 40px rgba(117, 9, 182, 0.55),
    0 0 80px rgba(117, 9, 182, 0.25),
    inset 0 0 40px rgba(192, 132, 252, 0.25);
  animation: portal-glow 4s ease-in-out infinite alternate;
}

.portal-ring-1 {
  transform: scale(1);
}

.portal-ring-2 {
  inset: 8%;
  opacity: 0.7;
  animation-delay: 0.6s;
}

.portal-core {
  position: absolute;
  inset: 18%;
  border-radius: 50% 50% 45% 45% / 55% 55% 40% 40%;
  background:
    radial-gradient(circle at 50% 40%, rgba(251, 207, 232, 0.9) 0%, rgba(192, 132, 252, 0.55) 35%, rgba(117, 9, 182, 0.2) 70%, transparent 100%);
  filter: blur(2px);
}

.portal-silhouette {
  position: absolute;
  left: 50%;
  bottom: 6%;
  transform: translateX(-50%);
  width: 18px;
  height: 36px;
  background: #050508;
  border-radius: 40% 40% 30% 30%;
  box-shadow: 0 0 12px rgba(0, 0, 0, 0.8);
}

.portal-silhouette::before {
  content: '';
  position: absolute;
  top: -10px;
  left: 50%;
  transform: translateX(-50%);
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #050508;
}

@keyframes portal-glow {
  from {
    box-shadow:
      0 0 30px rgba(117, 9, 182, 0.4),
      0 0 60px rgba(117, 9, 182, 0.2),
      inset 0 0 30px rgba(192, 132, 252, 0.2);
  }
  to {
    box-shadow:
      0 0 50px rgba(117, 9, 182, 0.7),
      0 0 100px rgba(168, 85, 247, 0.35),
      inset 0 0 50px rgba(192, 132, 252, 0.35);
  }
}

.hero-title {
  font-size: clamp(2.75rem, 8vw, 5rem);
  font-weight: 800;
  letter-spacing: -0.02em;
  line-height: 1.05;
  text-shadow: 0 0 60px rgba(117, 9, 182, 0.35);
}

.hero-subtitle-row {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-top: 1rem;
}

.hero-line {
  display: block;
  width: 2.5rem;
  height: 1px;
  background: linear-gradient(
    90deg,
    transparent,
    color-mix(in srgb, var(--color-primary) 70%, white)
  );
}

.hero-line:last-child {
  background: linear-gradient(
    90deg,
    color-mix(in srgb, var(--color-primary) 70%, white),
    transparent
  );
}

.hero-subtitle-text {
  font-size: 1rem;
  letter-spacing: 0.35em;
  color: color-mix(in srgb, var(--color-primary) 60%, white);
}

.hero-tagline {
  white-space: pre-line;
  color: rgba(229, 231, 235, 0.78);
}

.light .hero-tagline {
  color: #4b5563;
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

.btn-icon {
  width: 1.05rem;
  height: 1.05rem;
  flex-shrink: 0;
  opacity: 0.95;
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
  padding: 1.125rem 1rem;
  text-align: center;
  transition: all 0.3s;
}

.dark .stat-card {
  border-color: rgba(255, 255, 255, 0.12);
  background: rgba(18, 18, 28, 0.45);
  backdrop-filter: blur(18px);
  box-shadow:
    0 8px 32px rgba(0, 0, 0, 0.25),
    inset 0 1px 0 rgba(255, 255, 255, 0.06);
}

.light .stat-card {
  border-color: rgba(0, 0, 0, 0.06);
  background: rgba(255, 255, 255, 0.72);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
  backdrop-filter: blur(12px);
}

/* 首页内容卡片：更透，浮在氛围上 */
.home-page :deep(.card) {
  background: rgba(16, 16, 24, 0.48);
  backdrop-filter: blur(16px);
  border-color: rgba(255, 255, 255, 0.1);
}

.light .home-page :deep(.card) {
  background: rgba(255, 255, 255, 0.78);
  border-color: rgba(0, 0, 0, 0.06);
}

.guestbook-cta,
.lab-preview-card,
.about-section {
  position: relative;
  z-index: 1;
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
  height: 8.5rem;
  overflow: hidden;
  background: linear-gradient(
    135deg,
    color-mix(in srgb, var(--color-secondary) 12%, transparent),
    color-mix(in srgb, var(--color-primary) 12%, transparent)
  );
}

.lab-preview-placeholder {
  display: flex;
  height: 100%;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
}

.lab-link-icon {
  display: inline-flex;
  height: 1.75rem;
  width: 1.75rem;
  align-items: center;
  justify-content: center;
  border-radius: 9999px;
  font-size: 0.75rem;
  transition: all 0.2s;
}

.dark .lab-link-icon {
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: #9ca3af;
}

.dark .lab-link-icon:hover {
  border-color: color-mix(in srgb, var(--color-primary) 40%, transparent);
  color: var(--color-primary);
}

.lab-action {
  color: var(--color-primary);
  transition: opacity 0.2s;
}

.lab-action:hover {
  opacity: 0.8;
  text-decoration: underline;
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
