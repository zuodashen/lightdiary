<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import {
  fetchGuestbook,
  fetchGuestbookRemaining,
  likeGuestbook,
  submitGuestbook,
} from '@/api/guestbook'
import Pagination from '@/components/common/Pagination.vue'
import Loading from '@/components/common/Loading.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import type { GuestbookMessage } from '@/types'
import {
  avatarLetter,
  formatDateTime,
  isLetterAvatar,
} from '@/utils/format'

const messages = ref<GuestbookMessage[]>([])
const pageNum = ref(1)
const totalPage = ref(1)
const total = ref(0)
const loading = ref(true)
const submitting = ref(false)
const remaining = ref(3)
const replyTarget = ref<GuestbookMessage | null>(null)
const formError = ref('')
const formSuccess = ref('')
const sortBy = ref('latest')
const likedIds = ref<Set<number>>(new Set())

const form = ref({
  authorName: '',
  authorEmail: '',
  content: '',
})

const contentLength = computed(() => form.value.content.length)
const pageSize = 10

async function loadMessages() {
  loading.value = true
  try {
    const result = await fetchGuestbook(pageNum.value, pageSize)
    messages.value = result.list
    totalPage.value = result.totalPage
    total.value = result.total
  } catch {
    messages.value = []
    totalPage.value = 1
    total.value = 0
  } finally {
    loading.value = false
  }
}

async function loadRemaining() {
  try {
    const result = await fetchGuestbookRemaining()
    remaining.value = result.remaining
  } catch {
    remaining.value = 0
  }
}

async function onSubmit() {
  formError.value = ''
  formSuccess.value = ''
  if (!form.value.authorName.trim()) {
    formError.value = '请填写昵称'
    return
  }
  if (!form.value.content.trim()) {
    formError.value = '请填写留言内容'
    return
  }
  submitting.value = true
  try {
    await submitGuestbook({
      authorName: form.value.authorName.trim(),
      authorEmail: form.value.authorEmail.trim() || undefined,
      content: form.value.content.trim(),
      parentId: replyTarget.value?.id,
    })
    formSuccess.value = replyTarget.value
      ? '回复已发布，感谢你的留言！'
      : '留言已发布，感谢你的留言！'
    form.value.content = ''
    replyTarget.value = null
    pageNum.value = 1
    await Promise.all([loadMessages(), loadRemaining()])
  } catch (e) {
    formError.value = e instanceof Error ? e.message : '发布失败'
  } finally {
    submitting.value = false
  }
}

async function onLike(message: GuestbookMessage) {
  if (likedIds.value.has(message.id)) return
  try {
    await likeGuestbook(message.id)
    message.likes = (message.likes ?? 0) + 1
    likedIds.value.add(message.id)
  } catch (e) {
    formError.value = e instanceof Error ? e.message : '点赞失败'
  }
}

function startReply(message: GuestbookMessage) {
  replyTarget.value = message
  form.value.content = `@${message.authorName} `
  document.getElementById('guestbook-form')?.scrollIntoView({ behavior: 'smooth' })
}

function cancelReply() {
  replyTarget.value = null
}

function onPageChange(page: number) {
  pageNum.value = page
  loadMessages()
  document.getElementById('guestbook-list')?.scrollIntoView({ behavior: 'smooth' })
}

function avatarUrl(message: GuestbookMessage) {
  if (isLetterAvatar(message.authorAvatar)) return ''
  return message.authorAvatar
}

onMounted(async () => {
  await Promise.all([loadMessages(), loadRemaining()])
})
</script>

<template>
  <div class="guestbook-page">
    <!-- Hero -->
    <section class="guestbook-hero">
      <div class="guestbook-hero-bg" aria-hidden="true" />
      <div class="guestbook-hero-stars" aria-hidden="true" />
      <div class="container-blog relative grid items-center gap-10 py-14 lg:grid-cols-[1fr_280px] lg:py-20">
        <div>
          <p class="section-eyebrow">Message Board</p>
          <h1 class="mb-5 text-4xl font-bold tracking-tight sm:text-5xl lg:text-6xl">
            <span class="gradient-text">留言板</span>
          </h1>
          <p class="max-w-xl text-base leading-relaxed text-muted sm:text-lg">
            欢迎在这里留下你的想法、建议或问候。每一条留言都是微光的一部分，期待与你交流。
          </p>
        </div>

        <div class="guestbook-hero-art hidden lg:block" aria-hidden="true">
          <div class="pen-scene">
            <div class="horizon-glow" />
            <div class="horizon-arc" />
            <svg viewBox="0 0 240 220" class="pen-svg" fill="none">
              <!-- trail -->
              <path
                d="M36 168 C78 118, 120 88, 168 52"
                stroke="url(#gbTrail)"
                stroke-width="2.2"
                stroke-linecap="round"
                opacity="0.75"
              />
              <path
                d="M48 172 C90 128, 130 96, 174 62"
                stroke="url(#gbTrail)"
                stroke-width="1"
                stroke-dasharray="3 7"
                opacity="0.4"
              />
              <!-- pen body -->
              <g transform="translate(168,40) rotate(-38)">
                <rect x="0" y="18" width="16" height="72" rx="4" fill="url(#gbPen)" />
                <rect x="2" y="18" width="4" height="72" rx="1" fill="rgba(255,255,255,0.25)" />
                <path d="M0 18 L8 0 L16 18 Z" fill="url(#gbNib)" />
                <circle cx="8" cy="6" r="1.5" fill="#fde68a" />
              </g>
              <defs>
                <linearGradient id="gbTrail" x1="36" y1="168" x2="168" y2="52">
                  <stop stop-color="#f59e0b" stop-opacity="0.15" />
                  <stop offset="0.4" stop-color="#a855f7" />
                  <stop offset="1" stop-color="#c084fc" />
                </linearGradient>
                <linearGradient id="gbPen" x1="0" y1="0" x2="1" y2="1">
                  <stop stop-color="#ddd6fe" />
                  <stop offset="0.45" stop-color="#7c3aed" />
                  <stop offset="1" stop-color="#4c1d95" />
                </linearGradient>
                <linearGradient id="gbNib" x1="0" y1="0" x2="0" y2="1">
                  <stop stop-color="#fef3c7" />
                  <stop offset="1" stop-color="#c084fc" />
                </linearGradient>
              </defs>
            </svg>
          </div>
        </div>
      </div>
    </section>

    <div class="container-blog pb-16">
      <!-- Form -->
      <section id="guestbook-form" class="form-card mb-10 overflow-hidden">
        <div class="form-card-glow" aria-hidden="true" />
        <div class="relative p-6 sm:p-8">
          <div class="mb-6 flex items-center gap-3">
            <span class="form-icon-badge">💬</span>
            <h2 class="text-lg font-semibold dark:text-white light:text-gray-900">
              欢迎留言
            </h2>
          </div>

          <div
            v-if="replyTarget"
            class="reply-banner mb-5 flex items-center justify-between rounded-xl px-4 py-2.5 text-sm"
          >
            <span class="text-muted">
              回复 <strong class="text-primary">{{ replyTarget.authorName }}</strong>
            </span>
            <button type="button" class="btn-ghost py-1 text-xs" @click="cancelReply">
              取消回复
            </button>
          </div>

          <form class="space-y-5" @submit.prevent="onSubmit">
            <div class="grid gap-5 sm:grid-cols-2">
              <label class="form-field">
                <span class="form-label">
                  你的昵称 <span class="text-red-400">*</span>
                </span>
                <div class="input-wrap">
                  <span class="input-icon" aria-hidden="true">👤</span>
                  <input
                    v-model="form.authorName"
                    type="text"
                    maxlength="50"
                    placeholder="请输入你的名字"
                    class="form-input form-input-icon"
                  />
                </div>
              </label>
              <label class="form-field">
                <span class="form-label">邮箱（选填）</span>
                <div class="input-wrap">
                  <span class="input-icon" aria-hidden="true">✉️</span>
                  <input
                    v-model="form.authorEmail"
                    type="email"
                    placeholder="方便我回复你"
                    class="form-input form-input-icon"
                  />
                </div>
              </label>
            </div>

            <label class="form-field">
              <span class="form-label">
                留言内容 <span class="text-red-400">*</span>
              </span>
              <textarea
                v-model="form.content"
                rows="5"
                maxlength="500"
                placeholder="说点什么吧..."
                class="form-input form-textarea"
              />
              <span class="form-counter">{{ contentLength }} / 500</span>
            </label>

            <p v-if="formError" class="text-sm text-red-400">{{ formError }}</p>
            <p v-if="formSuccess" class="text-sm text-emerald-400">{{ formSuccess }}</p>

            <div class="flex flex-wrap items-center gap-4">
              <button type="submit" class="btn-primary submit-btn gap-2" :disabled="submitting">
                <span v-if="submitting">发布中...</span>
                <template v-else>
                  <span>发布留言</span>
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
                  </svg>
                </template>
              </button>
              <span class="remaining-hint text-xs">
                今日还可留言 <strong>{{ remaining }}</strong> 次
              </span>
            </div>
          </form>
        </div>
      </section>

      <!-- List -->
      <section id="guestbook-list">
        <div class="mb-6 flex flex-wrap items-end justify-between gap-4">
          <div>
            <h2 class="text-xl font-bold dark:text-white light:text-gray-900">
              最新留言
            </h2>
            <p class="mt-1 text-sm text-muted">
              第 {{ pageNum }} 页 / 共 {{ total }} 条留言
            </p>
          </div>
          <label class="sort-select-wrap">
            <select v-model="sortBy" class="sort-select" disabled title="暂仅支持最新排序">
              <option value="latest">最新留言</option>
            </select>
          </label>
        </div>

        <Loading v-if="loading" text="加载留言..." />

        <EmptyState
          v-else-if="!messages.length"
          title="还没有留言"
          description="成为第一个留言的人吧"
          icon="💬"
        />

        <div v-else class="space-y-4">
          <article
            v-for="message in messages"
            :key="message.id"
            class="message-card"
          >
            <div class="flex gap-4">
              <div
                v-if="avatarUrl(message)"
                class="h-12 w-12 shrink-0 overflow-hidden rounded-full ring-2 ring-primary/20"
              >
                <img
                  :src="avatarUrl(message)"
                  :alt="message.authorName"
                  class="h-full w-full object-cover"
                />
              </div>
              <div v-else class="avatar-letter shrink-0">
                {{ avatarLetter(message.authorAvatar, message.authorName) }}
              </div>

              <div class="min-w-0 flex-1">
                <div class="mb-2 flex items-start justify-between gap-3">
                  <div class="flex min-w-0 flex-wrap items-center gap-x-3 gap-y-1">
                    <span class="font-semibold dark:text-white light:text-gray-900">
                      {{ message.authorName }}
                    </span>
                    <span class="text-xs text-muted">
                      {{ formatDateTime(message.createTime) }}
                    </span>
                  </div>
                  <div class="flex shrink-0 items-center gap-1">
                    <button type="button" class="action-btn" @click="startReply(message)">
                      <span class="action-btn-icon">↩</span>
                      <span class="hidden sm:inline">回复</span>
                    </button>
                    <button
                      type="button"
                      class="action-btn"
                      :class="{ 'action-btn-liked': likedIds.has(message.id) }"
                      @click="onLike(message)"
                    >
                      <span class="action-btn-icon">👍</span>
                      {{ message.likes ?? 0 }}
                    </button>
                  </div>
                </div>
                <p class="message-content whitespace-pre-wrap text-sm leading-relaxed">
                  {{ message.content }}
                </p>

                <div
                  v-if="message.children?.length"
                  class="reply-thread mt-4 space-y-3"
                >
                  <div
                    v-for="reply in message.children"
                    :key="reply.id"
                    class="reply-item"
                  >
                    <div class="mb-1 flex flex-wrap items-center gap-2">
                      <span class="text-sm font-medium dark:text-gray-200 light:text-gray-800">
                        {{ reply.authorName }}
                      </span>
                      <span class="text-xs text-muted">
                        {{ formatDateTime(reply.createTime) }}
                      </span>
                    </div>
                    <p class="text-sm leading-relaxed text-muted">
                      {{ reply.content }}
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </article>
        </div>

        <Pagination
          v-if="!loading && messages.length"
          :page-num="pageNum"
          :total-page="totalPage"
          :total="total"
          unit="条"
          @change="onPageChange"
        />
      </section>
    </div>
  </div>
</template>

<style scoped>
.text-muted {
  color: var(--color-muted, #8b8b9a);
}

.guestbook-page {
  margin-top: -0.75rem;
}

.guestbook-hero {
  position: relative;
  margin-bottom: 2rem;
  overflow: hidden;
}

.guestbook-hero-bg {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(
      ellipse 90% 60% at 70% 90%,
      color-mix(in srgb, var(--color-primary) 32%, transparent) 0%,
      transparent 55%
    ),
    radial-gradient(
      ellipse 50% 40% at 15% 20%,
      color-mix(in srgb, var(--color-secondary) 18%, transparent) 0%,
      transparent 50%
    );
  mask-image: linear-gradient(to bottom, black 0%, black 75%, transparent 100%);
}

.pen-scene {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 240px;
}

.horizon-glow {
  position: absolute;
  bottom: 28px;
  left: 50%;
  transform: translateX(-50%);
  width: 200px;
  height: 90px;
  border-radius: 50%;
  background: radial-gradient(
    ellipse at center,
    color-mix(in srgb, var(--color-primary) 55%, transparent) 0%,
    transparent 70%
  );
  filter: blur(4px);
  animation: pen-pulse 4s ease-in-out infinite;
}

.horizon-arc {
  position: absolute;
  bottom: 36px;
  left: 50%;
  transform: translateX(-50%);
  width: 170px;
  height: 70px;
  border-radius: 50%;
  border: 2px solid transparent;
  border-top-color: color-mix(in srgb, var(--color-primary) 70%, white);
  box-shadow: 0 -8px 30px color-mix(in srgb, var(--color-primary) 35%, transparent);
  opacity: 0.85;
}

.pen-svg {
  position: relative;
  width: 240px;
  height: 220px;
  filter: drop-shadow(0 0 28px color-mix(in srgb, var(--color-primary) 45%, transparent));
}

@keyframes pen-pulse {
  0%, 100% { transform: scale(1); opacity: 0.8; }
  50% { transform: scale(1.08); opacity: 1; }
}

.section-eyebrow {
  margin-bottom: 0.75rem;
  font-size: 0.75rem;
  font-weight: 600;
  letter-spacing: 0.25em;
  text-transform: uppercase;
  color: var(--color-muted);
}

.form-card {
  position: relative;
  border-radius: 1.25rem;
  border-width: 1px;
  transition: border-color 0.3s;
}

.dark .form-card {
  border-color: color-mix(in srgb, var(--color-primary) 25%, rgba(255, 255, 255, 0.08));
  background: rgba(20, 20, 28, 0.75);
  backdrop-filter: blur(16px);
}

.light .form-card {
  border-color: color-mix(in srgb, var(--color-primary) 15%, rgba(0, 0, 0, 0.06));
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 8px 40px rgba(117, 9, 182, 0.06);
}

.form-card-glow {
  position: absolute;
  inset: 0;
  border-radius: inherit;
  background: radial-gradient(
    ellipse at 0% 0%,
    color-mix(in srgb, var(--color-primary) 12%, transparent) 0%,
    transparent 55%
  );
  pointer-events: none;
}

.form-icon-badge {
  display: flex;
  height: 2.5rem;
  width: 2.5rem;
  align-items: center;
  justify-content: center;
  border-radius: 0.75rem;
  font-size: 1.125rem;
  background: linear-gradient(
    135deg,
    color-mix(in srgb, var(--color-secondary) 20%, transparent),
    color-mix(in srgb, var(--color-primary) 20%, transparent)
  );
}

.reply-banner {
  border: 1px solid color-mix(in srgb, var(--color-primary) 20%, transparent);
  background: color-mix(in srgb, var(--color-primary) 8%, transparent);
}

.form-field {
  display: block;
}

.form-label {
  display: block;
  margin-bottom: 0.5rem;
  font-size: 0.875rem;
  font-weight: 500;
}

.input-wrap {
  position: relative;
}

.input-icon {
  position: absolute;
  left: 0.875rem;
  top: 50%;
  transform: translateY(-50%);
  font-size: 0.875rem;
  opacity: 0.55;
  pointer-events: none;
}

.form-input {
  width: 100%;
  border-radius: 0.75rem;
  border-width: 1px;
  padding: 0.75rem 1rem;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.form-input-icon {
  padding-left: 2.5rem;
}

.dark .form-input {
  border-color: rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.04);
  color: white;
}

.dark .form-input:focus {
  outline: none;
  border-color: color-mix(in srgb, var(--color-primary) 50%, transparent);
  box-shadow: 0 0 0 3px color-mix(in srgb, var(--color-primary) 15%, transparent);
}

.light .form-input {
  border-color: rgba(0, 0, 0, 0.08);
  background: white;
  color: #111;
}

.form-textarea {
  resize: vertical;
  min-height: 8.5rem;
}

.form-counter {
  display: block;
  margin-top: 0.375rem;
  text-align: right;
  font-size: 0.75rem;
  color: var(--color-muted);
}

.submit-btn {
  min-width: 8rem;
}

.remaining-hint {
  color: var(--color-muted);
}

.remaining-hint strong {
  color: var(--color-primary);
}

.sort-select-wrap {
  position: relative;
}

.sort-select {
  appearance: none;
  border-radius: 9999px;
  border-width: 1px;
  padding: 0.375rem 2rem 0.375rem 1rem;
  font-size: 0.8125rem;
  cursor: pointer;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 24 24' stroke='%238b8b9a'%3E%3Cpath stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M19 9l-7 7-7-7'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 0.625rem center;
  background-size: 1rem;
}

.dark .sort-select {
  border-color: rgba(255, 255, 255, 0.1);
  background-color: rgba(255, 255, 255, 0.04);
  color: #e5e7eb;
}

.light .sort-select {
  border-color: rgba(0, 0, 0, 0.08);
  background-color: white;
  color: #374151;
}

.avatar-letter {
  display: flex;
  height: 3rem;
  width: 3rem;
  align-items: center;
  justify-content: center;
  border-radius: 9999px;
  font-size: 0.9375rem;
  font-weight: 700;
  color: white;
  background: linear-gradient(135deg, var(--color-secondary), var(--color-primary));
  box-shadow: 0 4px 16px color-mix(in srgb, var(--color-primary) 25%, transparent);
}

.message-card {
  border-radius: 1rem;
  border-width: 1px;
  padding: 1.25rem 1.5rem;
  transition: all 0.3s;
}

.dark .message-card {
  border-color: rgba(255, 255, 255, 0.08);
  background: rgba(20, 20, 24, 0.6);
  backdrop-filter: blur(8px);
}

.light .message-card {
  border-color: rgba(0, 0, 0, 0.06);
  background: white;
}

.message-card:hover {
  border-color: color-mix(in srgb, var(--color-primary) 28%, transparent);
  transform: translateY(-1px);
}

.message-content {
  color: color-mix(in srgb, var(--color-muted) 85%, white);
}

.light .message-content {
  color: #4b5563;
}

.action-btn {
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
  border-radius: 9999px;
  padding: 0.3125rem 0.875rem;
  font-size: 0.75rem;
  transition: all 0.2s;
}

.dark .action-btn {
  border: 1px solid rgba(255, 255, 255, 0.08);
  color: #9ca3af;
  background: rgba(255, 255, 255, 0.03);
}

.dark .action-btn:hover {
  border-color: color-mix(in srgb, var(--color-primary) 30%, transparent);
  color: #e5e7eb;
  background: rgba(255, 255, 255, 0.06);
}

.light .action-btn {
  border: 1px solid rgba(0, 0, 0, 0.06);
  color: #6b7280;
  background: #f9fafb;
}

.action-btn-liked {
  border-color: color-mix(in srgb, var(--color-primary) 40%, transparent) !important;
  color: var(--color-primary) !important;
}

.reply-thread {
  border-left: 2px solid color-mix(in srgb, var(--color-primary) 25%, transparent);
  padding-left: 1rem;
}

.reply-item {
  border-radius: 0.75rem;
  padding: 0.75rem 1rem;
}

.dark .reply-item {
  background: rgba(255, 255, 255, 0.03);
}

.light .reply-item {
  background: #f9fafb;
}
</style>
