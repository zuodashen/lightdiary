<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import {
  createResource,
  createResourceCategory,
  deleteResource,
  deleteResourceCategory,
  fetchResourceCategories,
  fetchResources,
  updateResource,
  updateResourceCategory,
} from '@/api/resource'
import type { UmsResource, UmsResourceCategory } from '@/types'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import FormModal from '@/components/FormModal.vue'
import Pagination from '@/components/Pagination.vue'
import { useToast } from '@/composables/useToast'

const toast = useToast()
const resources = ref<UmsResource[]>([])
const categories = ref<UmsResourceCategory[]>([])
const loading = ref(true)
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const categoryId = ref<number | undefined>()
const nameKeyword = ref('')
const urlKeyword = ref('')

const showResourceForm = ref(false)
const showCategoryForm = ref(false)
const editingResource = ref<UmsResource | null>(null)
const editingCategory = ref<UmsResourceCategory | null>(null)
const saving = ref(false)
const deleteResourceTarget = ref<UmsResource | null>(null)
const deleteCategoryTarget = ref<UmsResourceCategory | null>(null)
const deleting = ref(false)

const resourceForm = ref({ name: '', url: '', description: '', categoryId: undefined as number | undefined })
const categoryForm = ref({ name: '', sort: 0 })

const categoryMap = computed(() =>
  Object.fromEntries(categories.value.map((c) => [c.id, c.name])),
)

async function loadCategories() {
  categories.value = await fetchResourceCategories()
}

async function loadResources() {
  loading.value = true
  try {
    const result = await fetchResources({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      categoryId: categoryId.value,
      nameKeyword: nameKeyword.value || undefined,
      urlKeyword: urlKeyword.value || undefined,
    })
    resources.value = result.list
    total.value = result.total
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await loadCategories()
  await loadResources()
})

function selectCategory(id?: number) {
  categoryId.value = id
  pageNum.value = 1
  loadResources()
}

function openCreateResource() {
  editingResource.value = null
  resourceForm.value = {
    name: '',
    url: '',
    description: '',
    categoryId: categoryId.value ?? categories.value[0]?.id,
  }
  showResourceForm.value = true
}

function openEditResource(res: UmsResource) {
  editingResource.value = res
  resourceForm.value = {
    name: res.name,
    url: res.url,
    description: res.description || '',
    categoryId: res.categoryId,
  }
  showResourceForm.value = true
}

async function saveResource() {
  if (!resourceForm.value.name.trim() || !resourceForm.value.url.trim()) {
    toast.error('请填写名称和 URL')
    return
  }
  saving.value = true
  try {
    if (editingResource.value) {
      await updateResource(editingResource.value.id, resourceForm.value)
      toast.success('更新成功')
    } else {
      await createResource(resourceForm.value)
      toast.success('创建成功')
    }
    showResourceForm.value = false
    loadResources()
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '保存失败')
  } finally {
    saving.value = false
  }
}

function openCreateCategory() {
  editingCategory.value = null
  categoryForm.value = { name: '', sort: 0 }
  showCategoryForm.value = true
}

function openEditCategory(cat: UmsResourceCategory) {
  editingCategory.value = cat
  categoryForm.value = { name: cat.name, sort: cat.sort ?? 0 }
  showCategoryForm.value = true
}

async function saveCategory() {
  if (!categoryForm.value.name.trim()) {
    toast.error('请填写分类名称')
    return
  }
  saving.value = true
  try {
    if (editingCategory.value) {
      await updateResourceCategory(editingCategory.value.id, categoryForm.value)
      toast.success('更新成功')
    } else {
      await createResourceCategory(categoryForm.value)
      toast.success('创建成功')
    }
    showCategoryForm.value = false
    await loadCategories()
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '保存失败')
  } finally {
    saving.value = false
  }
}

async function confirmDeleteResource() {
  if (!deleteResourceTarget.value) return
  deleting.value = true
  try {
    await deleteResource(deleteResourceTarget.value.id)
    toast.success('删除成功')
    deleteResourceTarget.value = null
    loadResources()
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '删除失败')
  } finally {
    deleting.value = false
  }
}

async function confirmDeleteCategory() {
  if (!deleteCategoryTarget.value) return
  const deletedId = deleteCategoryTarget.value.id
  deleting.value = true
  try {
    await deleteResourceCategory(deletedId)
    toast.success('删除成功')
    deleteCategoryTarget.value = null
    if (categoryId.value === deletedId) categoryId.value = undefined
    await loadCategories()
    loadResources()
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '删除失败')
  } finally {
    deleting.value = false
  }
}
</script>

<template>
  <div class="flex gap-5">
    <aside class="w-52 shrink-0">
      <div class="admin-card p-3">
        <div class="mb-3 flex items-center justify-between">
          <span class="text-sm font-medium text-white">资源分类</span>
          <button type="button" class="btn-ghost px-2 py-1 text-xs" @click="openCreateCategory">+</button>
        </div>
        <button
          type="button"
          class="mb-1 w-full rounded-lg px-3 py-2 text-left text-sm transition"
          :class="!categoryId ? 'bg-primary/15 text-primary' : 'text-gray-400 hover:bg-white/[0.03]'"
          @click="selectCategory(undefined)"
        >
          全部
        </button>
        <div v-for="cat in categories" :key="cat.id" class="group flex items-center">
          <button
            type="button"
            class="mb-1 flex-1 rounded-lg px-3 py-2 text-left text-sm transition"
            :class="categoryId === cat.id ? 'bg-primary/15 text-primary' : 'text-gray-400 hover:bg-white/[0.03]'"
            @click="selectCategory(cat.id)"
          >
            {{ cat.name }}
          </button>
          <button type="button" class="btn-ghost hidden px-1 py-1 text-xs group-hover:inline" @click="openEditCategory(cat)">编</button>
          <button type="button" class="btn-ghost hidden px-1 py-1 text-xs text-red-400 group-hover:inline" @click="deleteCategoryTarget = cat">删</button>
        </div>
      </div>
    </aside>

    <div class="min-w-0 flex-1">
      <div class="mb-5 flex flex-wrap items-center justify-between gap-3">
        <div class="flex flex-wrap gap-3">
          <input v-model="nameKeyword" type="search" class="admin-input w-40" placeholder="名称..." @keyup.enter="() => { pageNum = 1; loadResources() }" />
          <input v-model="urlKeyword" type="search" class="admin-input w-40" placeholder="URL..." @keyup.enter="() => { pageNum = 1; loadResources() }" />
          <button type="button" class="btn-secondary" @click="() => { pageNum = 1; loadResources() }">搜索</button>
        </div>
        <button type="button" class="btn-primary" @click="openCreateResource">+ 新建资源</button>
      </div>

      <div class="admin-card overflow-x-auto p-0">
        <div v-if="loading" class="p-8 text-center text-sm text-gray-500">加载中...</div>
        <table v-else class="admin-table">
          <thead>
            <tr>
              <th>名称</th>
              <th>URL</th>
              <th>分类</th>
              <th>描述</th>
              <th class="text-right">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="resources.length === 0">
              <td colspan="5" class="py-10 text-center text-gray-500">暂无资源</td>
            </tr>
            <tr v-for="res in resources" :key="res.id">
              <td class="font-medium text-white">{{ res.name }}</td>
              <td class="font-mono text-xs text-gray-400">{{ res.url }}</td>
              <td>{{ res.categoryId ? categoryMap[res.categoryId] || '-' : '-' }}</td>
              <td class="max-w-xs truncate text-gray-400">{{ res.description || '-' }}</td>
              <td>
                <div class="flex justify-end gap-1">
                  <button type="button" class="btn-ghost px-2 py-1 text-xs" @click="openEditResource(res)">编辑</button>
                  <button type="button" class="btn-ghost px-2 py-1 text-xs text-red-400" @click="deleteResourceTarget = res">删除</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <Pagination :page-num="pageNum" :page-size="pageSize" :total="total" @change="(p) => { pageNum = p; loadResources() }" />
    </div>

    <FormModal :show="showResourceForm" :title="editingResource ? '编辑资源' : '新建资源'" :loading="saving" @close="showResourceForm = false">
      <div class="space-y-4">
        <div>
          <label class="admin-label">名称 *</label>
          <input v-model="resourceForm.name" type="text" class="admin-input" />
        </div>
        <div>
          <label class="admin-label">URL *</label>
          <input v-model="resourceForm.url" type="text" class="admin-input" placeholder="/article/**" />
        </div>
        <div>
          <label class="admin-label">分类</label>
          <select v-model="resourceForm.categoryId" class="admin-input">
            <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
          </select>
        </div>
        <div>
          <label class="admin-label">描述</label>
          <input v-model="resourceForm.description" type="text" class="admin-input" />
        </div>
      </div>
      <template #actions>
        <button type="button" class="btn-primary" :disabled="saving" @click="saveResource">保存</button>
      </template>
    </FormModal>

    <FormModal :show="showCategoryForm" :title="editingCategory ? '编辑分类' : '新建分类'" :loading="saving" @close="showCategoryForm = false">
      <div class="space-y-4">
        <div>
          <label class="admin-label">名称 *</label>
          <input v-model="categoryForm.name" type="text" class="admin-input" />
        </div>
        <div>
          <label class="admin-label">排序</label>
          <input v-model.number="categoryForm.sort" type="number" class="admin-input" />
        </div>
      </div>
      <template #actions>
        <button type="button" class="btn-primary" :disabled="saving" @click="saveCategory">保存</button>
      </template>
    </FormModal>

    <ConfirmDialog :show="!!deleteResourceTarget" title="删除资源" :loading="deleting" @close="deleteResourceTarget = null" @confirm="confirmDeleteResource">
      确定删除资源「{{ deleteResourceTarget?.name }}」吗？
    </ConfirmDialog>

    <ConfirmDialog :show="!!deleteCategoryTarget" title="删除分类" :loading="deleting" @close="deleteCategoryTarget = null" @confirm="confirmDeleteCategory">
      确定删除分类「{{ deleteCategoryTarget?.name }}」吗？
    </ConfirmDialog>
  </div>
</template>
