<script setup lang="ts">
import { onMounted, ref } from 'vue'
import {
  createCategory,
  deleteCategory,
  fetchCategories,
  updateCategory,
} from '@/api/category'
import type { Category, CategoryParam } from '@/types'
import FormModal from '@/components/FormModal.vue'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import { useToast } from '@/composables/useToast'

const toast = useToast()
const categories = ref<Category[]>([])
const loading = ref(true)
const showModal = ref(false)
const editing = ref<Category | null>(null)
const saving = ref(false)
const deleteTarget = ref<Category | null>(null)
const deleting = ref(false)

const form = ref<CategoryParam>({
  name: '',
  slug: '',
  description: '',
  sortOrder: 0,
  icon: '',
})

async function loadData() {
  loading.value = true
  try {
    categories.value = await fetchCategories()
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(loadData)

function openCreate() {
  editing.value = null
  form.value = { name: '', slug: '', description: '', sortOrder: 0, icon: '' }
  showModal.value = true
}

function openEdit(cat: Category) {
  editing.value = cat
  form.value = {
    name: cat.name,
    slug: cat.slug,
    description: cat.description || '',
    sortOrder: cat.sortOrder ?? 0,
    icon: cat.icon || '',
  }
  showModal.value = true
}

async function handleSave() {
  if (!form.value.name.trim()) {
    toast.error('请填写分类名称')
    return
  }
  saving.value = true
  try {
    if (editing.value) {
      await updateCategory(editing.value.id, form.value)
      toast.success('更新成功')
    } else {
      await createCategory(form.value)
      toast.success('创建成功')
    }
    showModal.value = false
    loadData()
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '保存失败')
  } finally {
    saving.value = false
  }
}

async function confirmDelete() {
  if (!deleteTarget.value) return
  deleting.value = true
  try {
    await deleteCategory(deleteTarget.value.id)
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
    <div class="mb-5 flex justify-end">
      <button type="button" class="btn-primary" @click="openCreate">+ 新建分类</button>
    </div>

    <div class="admin-card overflow-x-auto p-0">
      <div v-if="loading" class="p-8 text-center text-sm text-gray-500">加载中...</div>
      <table v-else class="admin-table">
        <thead>
          <tr>
            <th>名称</th>
            <th>Slug</th>
            <th>描述</th>
            <th>排序</th>
            <th class="text-right">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="categories.length === 0">
            <td colspan="5" class="py-10 text-center text-gray-500">暂无分类</td>
          </tr>
          <tr v-for="cat in categories" :key="cat.id">
            <td class="font-medium text-white">{{ cat.name }}</td>
            <td class="text-gray-400">{{ cat.slug }}</td>
            <td class="max-w-xs truncate text-gray-400">{{ cat.description || '-' }}</td>
            <td>{{ cat.sortOrder ?? 0 }}</td>
            <td>
              <div class="flex justify-end gap-1">
                <button type="button" class="btn-ghost px-2 py-1 text-xs" @click="openEdit(cat)">
                  编辑
                </button>
                <button
                  type="button"
                  class="btn-ghost px-2 py-1 text-xs text-red-400"
                  @click="deleteTarget = cat"
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
      :show="showModal"
      :title="editing ? '编辑分类' : '新建分类'"
      :loading="saving"
      @close="showModal = false"
    >
      <div class="space-y-4">
        <div>
          <label class="admin-label">名称 *</label>
          <input v-model="form.name" type="text" class="admin-input" />
        </div>
        <div>
          <label class="admin-label">Slug</label>
          <input v-model="form.slug" type="text" class="admin-input" />
        </div>
        <div>
          <label class="admin-label">描述</label>
          <textarea v-model="form.description" rows="2" class="admin-input resize-none" />
        </div>
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="admin-label">排序</label>
            <input v-model.number="form.sortOrder" type="number" class="admin-input" />
          </div>
          <div>
            <label class="admin-label">图标</label>
            <input v-model="form.icon" type="text" class="admin-input" placeholder="可选" />
          </div>
        </div>
      </div>
      <template #actions>
        <button type="button" class="btn-primary" :disabled="saving" @click="handleSave">
          {{ saving ? '保存中...' : '保存' }}
        </button>
      </template>
    </FormModal>

    <ConfirmDialog
      :show="!!deleteTarget"
      title="删除分类"
      :loading="deleting"
      @close="deleteTarget = null"
      @confirm="confirmDelete"
    >
      确定删除分类「{{ deleteTarget?.name }}」吗？
    </ConfirmDialog>
  </div>
</template>
