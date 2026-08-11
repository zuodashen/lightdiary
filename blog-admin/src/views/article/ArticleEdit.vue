<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { createArticle, fetchArticle, updateArticle } from '@/api/article'
import { fetchCategories } from '@/api/category'
import { fetchTags } from '@/api/tag'
import type { ArticleParam, Category, Tag } from '@/types'
import { useToast } from '@/composables/useToast'

const route = useRoute()
const router = useRouter()
const toast = useToast()

const isEdit = computed(() => !!route.params.id)
const articleId = computed(() => Number(route.params.id))

const categories = ref<Category[]>([])
const tags = ref<Tag[]>([])
const loading = ref(false)
const saving = ref(false)
const meta = ref({ publishTime: '', updateTime: '', status: '' })

const isPublished = computed(() => form.value.status === 'PUBLISHED')

const form = ref<ArticleParam>({
  title: '',
  slug: '',
  summary: '',
  content: '',
  coverImage: '',
  categoryId: undefined,
  tagIds: [],
  status: 'DRAFT',
  isTop: 0,
  allowComment: 1,
})

onMounted(async () => {
  loading.value = true
  try {
    const [cats, tagList] = await Promise.all([fetchCategories(), fetchTags()])
    categories.value = cats
    tags.value = tagList

    if (isEdit.value && articleId.value) {
      const article = await fetchArticle(articleId.value)
      meta.value = {
        publishTime: article.publishTime || '',
        updateTime: article.updateTime || '',
        status: article.status || 'DRAFT',
      }
      form.value = {
        title: article.title,
        slug: article.slug,
        summary: article.summary || '',
        content: article.content || '',
        coverImage: article.coverImage || '',
        categoryId: article.categoryId,
        tagIds: article.tags?.map((t) => t.id) || [],
        status: article.status || 'DRAFT',
        isTop: article.isTop ?? 0,
        allowComment:
          typeof article.allowComment === 'boolean'
            ? article.allowComment
              ? 1
              : 0
            : (article.allowComment ?? 1),
      }
    }
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '加载失败')
  } finally {
    loading.value = false
  }
})

function toggleTag(id: number) {
  const ids = form.value.tagIds || []
  if (ids.includes(id)) {
    form.value.tagIds = ids.filter((t) => t !== id)
  } else {
    form.value.tagIds = [...ids, id]
  }
}

function formatDateTime(value?: string) {
  if (!value) return '-'
  return new Date(value).toLocaleString('zh-CN')
}

async function handleSubmit(publish = false) {
  if (!form.value.title.trim()) {
    toast.error('请填写标题')
    return
  }
  saving.value = true
  const payload: ArticleParam = {
    ...form.value,
    status: publish ? 'PUBLISHED' : form.value.status || 'DRAFT',
  }
  try {
    if (isEdit.value && articleId.value) {
      await updateArticle(articleId.value, payload)
      toast.success(
        publish || payload.status === 'PUBLISHED' ? '已保存，前台内容已更新' : '草稿已保存',
      )
    } else {
      await createArticle(payload)
      toast.success(publish ? '创建并发布成功' : '创建成功')
    }
    router.push('/articles')
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '保存失败')
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <div>
    <div class="mb-5">
      <router-link to="/articles" class="text-sm text-gray-400 hover:text-primary">
        ← 返回文章列表
      </router-link>
    </div>

    <div v-if="loading" class="text-sm text-gray-500">加载中...</div>

    <form v-else class="space-y-5" @submit.prevent="handleSubmit(false)">
      <div v-if="isEdit && (meta.publishTime || meta.updateTime)" class="admin-card">
        <div class="grid gap-3 text-sm sm:grid-cols-2">
          <div>
            <span class="text-gray-500">发布时间</span>
            <p class="mt-1 text-gray-200">{{ formatDateTime(meta.publishTime) }}</p>
          </div>
          <div>
            <span class="text-gray-500">最后更新</span>
            <p class="mt-1 text-gray-200">{{ formatDateTime(meta.updateTime) }}</p>
          </div>
        </div>
        <p v-if="isPublished" class="mt-3 text-xs text-gray-500">
          已发布文章修改后点「保存」即可，无需再点「保存并发布」；发布时间保持不变，仅更新「最后更新」时间。
        </p>
      </div>

      <div class="admin-card space-y-4">
        <div>
          <label class="admin-label">标题 *</label>
          <input v-model="form.title" type="text" class="admin-input" placeholder="文章标题" />
        </div>
        <div class="grid gap-4 md:grid-cols-2">
          <div>
            <label class="admin-label">Slug</label>
            <input v-model="form.slug" type="text" class="admin-input" placeholder="url-friendly-slug" />
          </div>
          <div>
            <label class="admin-label">封面图 URL</label>
            <input v-model="form.coverImage" type="text" class="admin-input" placeholder="https://..." />
          </div>
        </div>
        <div>
          <label class="admin-label">摘要</label>
          <textarea v-model="form.summary" rows="2" class="admin-input resize-none" placeholder="文章摘要" />
        </div>
      </div>

      <div class="admin-card space-y-4">
        <div>
          <label class="admin-label">Markdown 内容</label>
          <textarea
            v-model="form.content"
            rows="18"
            class="admin-input resize-y font-mono text-sm leading-relaxed"
            placeholder="# 标题&#10;&#10;正文内容..."
          />
        </div>
      </div>

      <div class="admin-card space-y-4">
        <div class="grid gap-4 md:grid-cols-2">
          <div>
            <label class="admin-label">分类</label>
            <select v-model="form.categoryId" class="admin-input">
              <option :value="undefined">未分类</option>
              <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
            </select>
          </div>
          <div>
            <label class="admin-label">状态</label>
            <select v-model="form.status" class="admin-input">
              <option value="DRAFT">草稿</option>
              <option value="PUBLISHED">已发布</option>
            </select>
          </div>
        </div>

        <div>
          <label class="admin-label">标签</label>
          <div class="flex flex-wrap gap-2">
            <button
              v-for="tag in tags"
              :key="tag.id"
              type="button"
              class="rounded-full border px-3 py-1 text-xs transition"
              :class="
                form.tagIds?.includes(tag.id)
                  ? 'border-primary bg-primary/20 text-primary'
                  : 'border-[var(--color-border)] text-gray-400 hover:border-primary/40'
              "
              @click="toggleTag(tag.id)"
            >
              {{ tag.name }}
            </button>
            <span v-if="tags.length === 0" class="text-sm text-gray-500">暂无标签，请先在标签管理中添加</span>
          </div>
        </div>

        <div class="flex flex-wrap gap-6">
          <label class="flex items-center gap-2 text-sm text-gray-300">
            <input v-model="form.isTop" type="checkbox" :true-value="1" :false-value="0" class="accent-primary" />
            置顶
          </label>
          <label class="flex items-center gap-2 text-sm text-gray-300">
            <input
              v-model="form.allowComment"
              type="checkbox"
              :true-value="1"
              :false-value="0"
              class="accent-primary"
            />
            允许评论
          </label>
        </div>
      </div>

      <div class="flex flex-wrap gap-3">
        <button type="submit" class="btn-primary" :disabled="saving">
          {{ saving ? '保存中...' : '保存' }}
        </button>
        <button
          v-if="!isPublished"
          type="button"
          class="btn-secondary"
          :disabled="saving"
          @click="handleSubmit(true)"
        >
          保存并发布
        </button>
        <router-link to="/articles" class="btn-ghost">取消</router-link>
      </div>
    </form>
  </div>
</template>
