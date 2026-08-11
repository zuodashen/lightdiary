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
  try {
    await likeGuestbook(message.id)
    message.likes = (message.likes ?? 0) + 1
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
  window.scrollTo({ top: 0, behavior: 'smooth' })
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
      <div class="container-blog relative grid items-center gap-8 py-14 lg:grid-cols-[1fr_auto] lg:py-20">
        <div>
          <p class="section-eyebrow">Message Board</p>
          <h1 class="mb-4 text-4xl font-bold tracking-tight sm:text-5xl">
            <span class="gradient-text">留言板</span>
          </h1>
          <p class="max-w-xl text-base leading-relaxed text-muted sm:text-lg">
            欢迎在这里留下你的想法、建议或问候。每一条留言都是微光的一部分，期待与你交流。
          </p>
        </div>
        <div class="guestbook-hero-icon hidden lg:flex" aria-hidden="true">
          ✒️
        </div>
      </div>
    </section>

    <div class="container-blog pb-16">
      <!-- Form -->
      <section id="guestbook-form" class="card mb-10 overflow-hidden p-6 sm:p-8">
        <div class="mb-6 flex items-center gap-2">
          <span class="text-xl">💬</span>
          <h2 class="text-lg font-semibold dark:text-white light:text-gray-900">
            欢迎留言
          </h2>
        </div>

        <div
          v-if="replyTarget"
          class="mb-4 flex items-center justify-between rounded-xl px-4 py-2 text-sm dark:bg-white/5 light:bg-gray-50"
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
              <span class="form-label">你的昵称 <span class="text-red-400">*</span></span>
              <input
                v-model="form.authorName"
                type="text"
                maxlength="50"
                placeholder="请输入你的名字"
                class="form-input"
              />
            </label>
            <label class="form-field">
              <span class="form-label">邮箱（选填）</span>
              <input
                v-model="form.authorEmail"
                type="email"
                placeholder="方便我回复你"
                class="form-input"
              />
            </label>
          </div>

          <label class="form-field">
            <span class="form-label">留言内容 <span class="text-red-400">*</span></span>
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
            <button type="submit" class="btn-primary gap-2" :disabled="submitting">
              <span v-if="submitting">发布中...</span>
              <template v-else>
                <span>发布留言</span>
                <span aria-hidden="true">✈️</span>
              </template>
            </button>
            <span class="text-xs text-muted">
              今日还可留言 {{ remaining }} 次
            </span>
          </div>
        </form>
      </section>

      <!-- List -->
      <section>
        <div class="mb-6 flex flex-wrap items-end justify-between gap-4">
          <div>
            <h2 class="text-xl font-bold dark:text-white light:text-gray-900">
              最新留言
            </h2>
            <p class="mt-1 text-sm text-muted">
              第 {{ pageNum }} 页 / 共 {{ total }} 条留言
            </p>
          </div>
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
            class="card message-card p-5 sm:p-6"
          >
            <div class="flex gap-4">
              <div
                v-if="avatarUrl(message)"
                class="h-11 w-11 shrink-0 overflow-hidden rounded-full"
              >
                <img
                  :src="avatarUrl(message)"
                  :alt="message.authorName"
                  class="h-full w-full object-cover"
                />
              </div>
              <div
                v-else
                class="avatar-letter shrink-0"
              >
                {{ avatarLetter(message.authorAvatar, message.authorName) }}
              </div>

              <div class="min-w-0 flex-1">
                <div class="mb-2 flex flex-wrap items-center gap-2">
                  <span class="font-semibold dark:text-white light:text-gray-900">
                    {{ message.authorName }}
                  </span>
                  <span class="text-xs text-muted">
                    {{ formatDateTime(message.createTime) }}
                  </span>
                </div>
                <p class="whitespace-pre-wrap text-sm leading-relaxed text-muted">
                  {{ message.content }}
                </p>

                <div class="mt-4 flex flex-wrap items-center gap-3">
                  <button
                    type="button"
                    class="action-btn"
                    @click="startReply(message)"
                  >
                    ↩ 回复
                  </button>
                  <button
                    type="button"
                    class="action-btn"
                    @click="onLike(message)"
                  >
                    👍 {{ message.likes ?? 0 }}
                  </button>
                </div>

                <!-- Replies -->
                <div
                  v-if="message.children?.length"
                  class="mt-4 space-y-3 border-l-2 pl-4 dark:border-white/10 light:border-gray-200"
                >
                  <div
                    v-for="reply in message.children"
                    :key="reply.id"
                    class="rounded-xl p-3 dark:bg-white/3 light:bg-gray-50"
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
      ellipse 70% 80% at 80% 20%,
      color-mix(in srgb, var(--color-primary) 22%, transparent) 0%,
      transparent 60%
    ),
    radial-gradient(
      ellipse 60% 70% at 20% 80%,
      color-mix(in srgb, var(--color-secondary) 18%, transparent) 0%,
      transparent 55%
    );
  mask-image: linear-gradient(to bottom, black 0%, black 80%, transparent 100%);
}

.guestbook-hero-icon {
  height: 8rem;
  width: 8rem;
  align-items: center;
  justify-content: center;
  border-radius: 9999px;
  font-size: 3rem;
  background: radial-gradient(
    circle,
    color-mix(in srgb, var(--color-primary) 25%, transparent) 0%,
    transparent 70%
  );
  box-shadow: 0 0 60px color-mix(in srgb, var(--color-primary) 30%, transparent);
}

.section-eyebrow {
  margin-bottom: 0.75rem;
  font-size: 0.75rem;
  font-weight: 600;
  letter-spacing: 0.25em;
  text-transform: uppercase;
  color: var(--color-muted);
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

.form-input {
  width: 100%;
  border-radius: 0.75rem;
  border-width: 1px;
  padding: 0.75rem 1rem;
  font-size: 0.875rem;
  transition: all 0.2s;
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
  min-height: 8rem;
}

.form-counter {
  display: block;
  margin-top: 0.375rem;
  text-align: right;
  font-size: 0.75rem;
  color: var(--color-muted);
}

.avatar-letter {
  display: flex;
  height: 2.75rem;
  width: 2.75rem;
  align-items: center;
  justify-content: center;
  border-radius: 9999px;
  font-size: 0.875rem;
  font-weight: 700;
  color: white;
  background: linear-gradient(135deg, var(--color-secondary), var(--color-primary));
}

.action-btn {
  border-radius: 9999px;
  padding: 0.25rem 0.75rem;
  font-size: 0.75rem;
  transition: all 0.2s;
}

.dark .action-btn {
  color: #9ca3af;
}

.dark .action-btn:hover {
  background: rgba(255, 255, 255, 0.06);
  color: #e5e7eb;
}

.light .action-btn {
  color: #6b7280;
}

.light .action-btn:hover {
  background: rgba(0, 0, 0, 0.04);
  color: #374151;
}

.message-card {
  transition: border-color 0.3s;
}

.message-card:hover {
  border-color: color-mix(in srgb, var(--color-primary) 25%, transparent);
}
</style>
