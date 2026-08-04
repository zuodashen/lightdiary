<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { createNavItem, deleteNavItem, fetchNavItems, updateNavItem } from '@/api/site'
import type { NavItem, NavItemParam } from '@/types'
import FormModal from '@/components/FormModal.vue'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import { useToast } from '@/composables/useToast'

const toast = useToast()
const items = ref<NavItem[]>([])
const loading = ref(true)
const showModal = ref(false)
const editing = ref<NavItem | null>(null)
const saving = ref(false)
const deleteTarget = ref<NavItem | null>(null)
const deleting = ref(false)

const form = ref<NavItemParam>({
  name: '',
  path: '',
  icon: '',
  parentId: undefined,
  sortOrder: 0,
  isExternal: 0,
})

async function loadData() {
  loading.value = true
  try {
    items.value = await fetchNavItems()
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(loadData)

function openCreate() {
  editing.value = null
  form.value = { name: '', path: '', icon: '', parentId: undefined, sortOrder: 0, isExternal: 0 }
  showModal.value = true
}

function openEdit(item: NavItem) {
  editing.value = item
  form.value = {
    name: item.name,
    path: item.path || '',
    icon: item.icon || '',
    parentId: item.parentId,
    sortOrder: item.sortOrder ?? 0,
    isExternal: item.isExternal ?? 0,
  }
  showModal.value = true
}

async function handleSave() {
  if (!form.value.name.trim()) {
    toast.error('请填写名称')
    return
  }
  saving.value = true
  try {
    if (editing.value) {
      await updateNavItem(editing.value.id, form.value)
      toast.success('更新成功')
    } else {
      await createNavItem(form.value)
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
    await deleteNavItem(deleteTarget.value.id)
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
      <button type="button" class="btn-primary" @click="openCreate">+ 新建导航</button>
    </div>

    <div class="admin-card overflow-x-auto p-0">
      <div v-if="loading" class="p-8 text-center text-sm text-gray-500">加载中...</div>
      <table v-else class="admin-table">
        <thead>
          <tr>
            <th>名称</th>
            <th>路径</th>
            <th>图标</th>
            <th>排序</th>
            <th>外链</th>
            <th class="text-right">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="items.length === 0">
            <td colspan="6" class="py-10 text-center text-gray-500">暂无导航项</td>
          </tr>
          <tr v-for="item in items" :key="item.id">
            <td class="font-medium text-white">{{ item.name }}</td>
            <td class="text-gray-400">{{ item.path || '-' }}</td>
            <td>{{ item.icon || '-' }}</td>
            <td>{{ item.sortOrder ?? 0 }}</td>
            <td>{{ item.isExternal ? '是' : '否' }}</td>
            <td>
              <div class="flex justify-end gap-1">
                <button type="button" class="btn-ghost px-2 py-1 text-xs" @click="openEdit(item)">
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
      :show="showModal"
      :title="editing ? '编辑导航' : '新建导航'"
      :loading="saving"
      @close="showModal = false"
    >
      <div class="space-y-4">
        <div>
          <label class="admin-label">名称 *</label>
          <input v-model="form.name" type="text" class="admin-input" />
        </div>
        <div>
          <label class="admin-label">路径</label>
          <input v-model="form.path" type="text" class="admin-input" placeholder="/about" />
        </div>
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="admin-label">图标</label>
            <input v-model="form.icon" type="text" class="admin-input" />
          </div>
          <div>
            <label class="admin-label">排序</label>
            <input v-model.number="form.sortOrder" type="number" class="admin-input" />
          </div>
        </div>
        <label class="flex items-center gap-2 text-sm text-gray-300">
          <input v-model="form.isExternal" type="checkbox" :true-value="1" :false-value="0" class="accent-primary" />
          外部链接
        </label>
      </div>
      <template #actions>
        <button type="button" class="btn-primary" :disabled="saving" @click="handleSave">
          {{ saving ? '保存中...' : '保存' }}
        </button>
      </template>
    </FormModal>

    <ConfirmDialog
      :show="!!deleteTarget"
      title="删除导航"
      :loading="deleting"
      @close="deleteTarget = null"
      @confirm="confirmDelete"
    >
      确定删除导航「{{ deleteTarget?.name }}」吗？
    </ConfirmDialog>
  </div>
</template>
