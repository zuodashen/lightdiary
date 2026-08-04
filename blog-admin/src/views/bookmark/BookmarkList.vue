<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import {
  createBookmark,
  createBookmarkCategory,
  deleteBookmark,
  deleteBookmarkCategory,
  fetchBookmarkCategories,
  fetchBookmarks,
  updateBookmark,
} from '@/api/bookmark'
import type { Bookmark, BookmarkCategory, BookmarkParam } from '@/types'
import FormModal from '@/components/FormModal.vue'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import { useToast } from '@/composables/useToast'

const toast = useToast()
const bookmarks = ref<Bookmark[]>([])
const categories = ref<BookmarkCategory[]>([])
const loading = ref(true)
const showBookmarkModal = ref(false)
const showCategoryModal = ref(false)
const editing = ref<Bookmark | null>(null)
const saving = ref(false)
const deleteTarget = ref<Bookmark | null>(null)
const deleting = ref(false)

const categoryForm = ref({ name: '', icon: '', sortOrder: 0 })
const bookmarkForm = ref<BookmarkParam>({
  categoryId: 0,
  name: '',
  link: '',
  description: '',
  image: '',
  sortOrder: 0,
})

const categoryMap = computed(() =>
  Object.fromEntries(categories.value.map((c) => [c.id, c.name])),
)

async function loadData() {
  loading.value = true
  try {
    const [cats, list] = await Promise.all([fetchBookmarkCategories(), fetchBookmarks()])
    categories.value = cats
    bookmarks.value = list
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(loadData)

function openCreateBookmark() {
  editing.value = null
  bookmarkForm.value = {
    categoryId: categories.value[0]?.id || 0,
    name: '',
    link: '',
    description: '',
    image: '',
    sortOrder: 0,
  }
  showBookmarkModal.value = true
}

function openEditBookmark(item: Bookmark) {
  editing.value = item
  bookmarkForm.value = {
    categoryId: item.categoryId,
    name: item.name,
    link: item.link,
    description: item.description || '',
    image: item.image || '',
    sortOrder: item.sortOrder ?? 0,
  }
  showBookmarkModal.value = true
}

async function handleSaveBookmark() {
  if (!bookmarkForm.value.name.trim() || !bookmarkForm.value.link.trim()) {
    toast.error('请填写名称和链接')
    return
  }
  if (!bookmarkForm.value.categoryId) {
    toast.error('请选择分类')
    return
  }
  saving.value = true
  try {
    if (editing.value) {
      await updateBookmark(editing.value.id, bookmarkForm.value)
      toast.success('更新成功')
    } else {
      await createBookmark(bookmarkForm.value)
      toast.success('创建成功')
    }
    showBookmarkModal.value = false
    loadData()
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '保存失败')
  } finally {
    saving.value = false
  }
}

async function handleSaveCategory() {
  if (!categoryForm.value.name.trim()) {
    toast.error('请填写分类名称')
    return
  }
  saving.value = true
  try {
    await createBookmarkCategory(categoryForm.value)
    toast.success('分类创建成功')
    showCategoryModal.value = false
    categoryForm.value = { name: '', icon: '', sortOrder: 0 }
    loadData()
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '创建失败')
  } finally {
    saving.value = false
  }
}

async function handleDeleteCategory(cat: BookmarkCategory) {
  if (!confirm(`确定删除书签分类「${cat.name}」吗？`)) return
  try {
    await deleteBookmarkCategory(cat.id)
    toast.success('删除成功')
    loadData()
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '删除失败')
  }
}

async function confirmDelete() {
  if (!deleteTarget.value) return
  deleting.value = true
  try {
    await deleteBookmark(deleteTarget.value.id)
    toast.success('删除成功')
    deleteTarget.value = null
    loadData()
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '删除失败')
  } finally {
    deleting.value = false
  }
}
</script>

<template>
  <div>
    <div class="mb-5 flex flex-wrap justify-end gap-3">
      <button type="button" class="btn-secondary" @click="showCategoryModal = true">
        + 书签分类
      </button>
      <button type="button" class="btn-primary" @click="openCreateBookmark">+ 新建书签</button>
    </div>

    <div v-if="categories.length" class="mb-4 flex flex-wrap gap-2">
      <span
        v-for="cat in categories"
        :key="cat.id"
        class="inline-flex items-center gap-2 rounded-full border border-[var(--color-border)] px-3 py-1 text-xs text-gray-400"
      >
        {{ cat.icon ? cat.icon + ' ' : '' }}{{ cat.name }}
        <button type="button" class="text-red-400 hover:text-red-300" @click="handleDeleteCategory(cat)">
          ×
        </button>
      </span>
    </div>

    <div class="admin-card overflow-x-auto p-0">
      <div v-if="loading" class="p-8 text-center text-sm text-gray-500">加载中...</div>
      <table v-else class="admin-table">
        <thead>
          <tr>
            <th>名称</th>
            <th>分类</th>
            <th>链接</th>
            <th>排序</th>
            <th class="text-right">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="bookmarks.length === 0">
            <td colspan="5" class="py-10 text-center text-gray-500">暂无书签</td>
          </tr>
          <tr v-for="item in bookmarks" :key="item.id">
            <td class="font-medium text-white">{{ item.name }}</td>
            <td>{{ categoryMap[item.categoryId] || '-' }}</td>
            <td class="max-w-xs truncate">
              <a :href="item.link" target="_blank" rel="noopener" class="text-primary hover:underline">
                {{ item.link }}
              </a>
            </td>
            <td>{{ item.sortOrder ?? 0 }}</td>
            <td>
              <div class="flex justify-end gap-1">
                <button type="button" class="btn-ghost px-2 py-1 text-xs" @click="openEditBookmark(item)">
                  编辑
                </button>
                <button
                  type="button"
                  class="btn-ghost px-2 py-1 text-xs text-red-400"
                  @click="deleteTarget = item"
                >
                  删除
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <FormModal
      :show="showBookmarkModal"
      :title="editing ? '编辑书签' : '新建书签'"
      :loading="saving"
      @close="showBookmarkModal = false"
    >
      <div class="space-y-4">
        <div>
          <label class="admin-label">分类 *</label>
          <select v-model="bookmarkForm.categoryId" class="admin-input">
            <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
          </select>
        </div>
        <div>
          <label class="admin-label">名称 *</label>
          <input v-model="bookmarkForm.name" type="text" class="admin-input" />
        </div>
        <div>
          <label class="admin-label">链接 *</label>
          <input v-model="bookmarkForm.link" type="url" class="admin-input" placeholder="https://..." />
        </div>
        <div>
          <label class="admin-label">描述</label>
          <textarea v-model="bookmarkForm.description" rows="2" class="admin-input resize-none" />
        </div>
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="admin-label">图标 URL</label>
            <input v-model="bookmarkForm.image" type="text" class="admin-input" />
          </div>
          <div>
            <label class="admin-label">排序</label>
            <input v-model.number="bookmarkForm.sortOrder" type="number" class="admin-input" />
          </div>
        </div>
      </div>
      <template #actions>
        <button type="button" class="btn-primary" :disabled="saving" @click="handleSaveBookmark">
          {{ saving ? '保存中...' : '保存' }}
        </button>
      </template>
    </FormModal>

    <FormModal
      :show="showCategoryModal"
      title="新建书签分类"
      :loading="saving"
      @close="showCategoryModal = false"
    >
      <div class="space-y-4">
        <div>
          <label class="admin-label">名称 *</label>
          <input v-model="categoryForm.name" type="text" class="admin-input" />
        </div>
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="admin-label">图标</label>
            <input v-model="categoryForm.icon" type="text" class="admin-input" placeholder="emoji 或图标" />
          </div>
          <div>
            <label class="admin-label">排序</label>
            <input v-model.number="categoryForm.sortOrder" type="number" class="admin-input" />
          </div>
        </div>
      </div>
      <template #actions>
        <button type="button" class="btn-primary" :disabled="saving" @click="handleSaveCategory">
          {{ saving ? '创建中...' : '创建' }}
        </button>
      </template>
    </FormModal>

    <ConfirmDialog
      :show="!!deleteTarget"
      title="删除书签"
      :loading="deleting"
      @close="deleteTarget = null"
      @confirm="confirmDelete"
    >
      确定删除书签「{{ deleteTarget?.name }}」吗？
    </ConfirmDialog>
  </div>
</template>
