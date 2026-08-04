<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  deleteArticle,
  draftArticle,
  fetchArticles,
  publishArticle,
  toggleArticleTop,
} from '@/api/article'
import { fetchCategories } from '@/api/category'
import { fetchTags } from '@/api/tag'
import type { Article, Category, Tag } from '@/types'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import Pagination from '@/components/Pagination.vue'
import { useToast } from '@/composables/useToast'

const router = useRouter()
const toast = useToast()

const articles = ref<Article[]>([])
const categories = ref<Category[]>([])
const tags = ref<Tag[]>([])
const loading = ref(true)
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const keyword = ref('')
const statusFilter = ref('')
const categoryFilter = ref<number | ''>('')
const tagFilter = ref<number | ''>('')
const deleteTarget = ref<Article | null>(null)
const deleting = ref(false)

async function loadData() {
  loading.value = true
  try {
    const result = await fetchArticles({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: keyword.value || undefined,
      status: statusFilter.value || undefined,
      categoryId: categoryFilter.value || undefined,
      tagId: tagFilter.value || undefined,
    })
    articles.value = result.list
    total.value = result.total
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  try {
    const [cats, tagList] = await Promise.all([fetchCategories(), fetchTags()])
    categories.value = cats
    tags.value = tagList
  } catch {
    // ignore
  }
  await loadData()
})

function categoryName(id?: number) {
  return categories.value.find((c) => c.id === id)?.name || '-'
}

function formatDate(value?: string) {
  if (!value) return '-'
  return new Date(value).toLocaleString('zh-CN')
}

function handleSearch() {
  pageNum.value = 1
  loadData()
}

function goEdit(id: number) {
  router.push(`/articles/${id}/edit`)
}

async function handlePublish(article: Article) {
  try {
    if (article.status === 'PUBLISHED') {
      await draftArticle(article.id)
      toast.success('已转为草稿')
    } else {
      await publishArticle(article.id)
      toast.success('已发布')
    }
    loadData()
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '操作失败')
  }
}

async function handleToggleTop(article: Article) {
  try {
    await toggleArticleTop(article.id)
    toast.success('置顶状态已更新')
    loadData()
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '操作失败')
  }
}

async function confirmDelete() {
  if (!deleteTarget.value) return
  deleting.value = true
  try {
    await deleteArticle(deleteTarget.value.id)
    toast.success('删除成功')
    deleteTarget.value = null
    loadData()
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '删除失败')
  } finally {
    deleting.value = false
  }
}

function changePage(page: number) {
  pageNum.value = page
  loadData()
}
</script>

<template>
  <div>
    <div class="mb-5 flex flex-wrap items-center justify-between gap-3">
      <div class="flex flex-wrap items-center gap-3">
        <input
          v-model="keyword"
          type="search"
          class="admin-input w-48"
          placeholder="搜索标题或摘要..."
          @keyup.enter="handleSearch"
        />
        <select v-model="statusFilter" class="admin-input w-32" @change="handleSearch">
          <option value="">全部状态</option>
          <option value="PUBLISHED">已发布</option>
          <option value="DRAFT">草稿</option>
        </select>
        <select v-model="categoryFilter" class="admin-input w-32" @change="handleSearch">
          <option value="">全部分类</option>
          <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
        </select>
        <select v-model="tagFilter" class="admin-input w-32" @change="handleSearch">
          <option value="">全部标签</option>
          <option v-for="tag in tags" :key="tag.id" :value="tag.id">{{ tag.name }}</option>
        </select>
        <button type="button" class="btn-secondary" @click="handleSearch">搜索</button>
      </div>
      <router-link to="/articles/new" class="btn-primary">+ 新建文章</router-link>
    </div>

    <div class="admin-card overflow-x-auto p-0">
      <div v-if="loading" class="p-8 text-center text-sm text-gray-500">加载中...</div>
      <table v-else class="admin-table">
        <thead>
          <tr>
            <th>标题</th>
            <th>分类</th>
            <th>状态</th>
            <th>置顶</th>
            <th>浏览</th>
            <th>更新时间</th>
            <th class="text-right">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="articles.length === 0">
            <td colspan="7" class="py-10 text-center text-gray-500">暂无文章</td>
          </tr>
          <tr v-for="article in articles" :key="article.id">
            <td>
              <div class="max-w-xs truncate font-medium text-white">{{ article.title }}</div>
              <div class="text-xs text-gray-500">{{ article.slug }}</div>
            </td>
            <td>{{ categoryName(article.categoryId) }}</td>
            <td>
              <span
                :class="article.status === 'PUBLISHED' ? 'badge-success' : 'badge-warning'"
              >
                {{ article.status === 'PUBLISHED' ? '已发布' : '草稿' }}
              </span>
            </td>
            <td>
              <span v-if="article.isTop === 1" class="badge-muted">置顶</span>
              <span v-else class="text-gray-600">-</span>
            </td>
            <td>{{ article.views ?? 0 }}</td>
            <td class="whitespace-nowrap text-xs text-gray-400">
              {{ formatDate(article.updateTime || article.createTime) }}
            </td>
            <td>
              <div class="flex justify-end gap-1">
                <button type="button" class="btn-ghost px-2 py-1 text-xs" @click="goEdit(article.id)">
                  编辑
                </button>
                <button
                  type="button"
                  class="btn-ghost px-2 py-1 text-xs"
                  @click="handlePublish(article)"
                >
                  {{ article.status === 'PUBLISHED' ? '草稿' : '发布' }}
                </button>
                <button
                  type="button"
                  class="btn-ghost px-2 py-1 text-xs"
                  @click="handleToggleTop(article)"
                >
                  置顶
                </button>
                <button
                  type="button"
                  class="btn-ghost px-2 py-1 text-xs text-red-400"
                  @click="deleteTarget = article"
                >
                  删除
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <Pagination :page-num="pageNum" :page-size="pageSize" :total="total" @change="changePage" />

    <ConfirmDialog
      :show="!!deleteTarget"
      title="删除文章"
      :loading="deleting"
      @close="deleteTarget = null"
      @confirm="confirmDelete"
    >
      确定删除文章「{{ deleteTarget?.title }}」吗？此操作不可恢复。
    </ConfirmDialog>
  </div>
</template>
