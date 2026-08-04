<script setup lang="ts">
import { onMounted, ref } from 'vue'
import {
  createMenu,
  deleteMenu,
  fetchMenuTree,
  updateMenu,
  updateMenuHidden,
} from '@/api/menu'
import type { UmsMenuNode } from '@/types'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import FormModal from '@/components/FormModal.vue'
import { useToast } from '@/composables/useToast'

const toast = useToast()
const menuTree = ref<UmsMenuNode[]>([])
const loading = ref(true)
const showForm = ref(false)
const editing = ref<UmsMenuNode | null>(null)
const parentForCreate = ref<UmsMenuNode | null>(null)
const saving = ref(false)
const deleteTarget = ref<UmsMenuNode | null>(null)
const deleting = ref(false)

const form = ref({
  parentId: 0 as number,
  title: '',
  name: '',
  icon: '',
  sort: 0,
  hidden: 0,
})

async function loadData() {
  loading.value = true
  try {
    menuTree.value = await fetchMenuTree()
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(loadData)

function openCreate(parent?: UmsMenuNode) {
  editing.value = null
  parentForCreate.value = parent || null
  form.value = {
    parentId: parent?.id ?? 0,
    title: '',
    name: '',
    icon: '',
    sort: 0,
    hidden: 0,
  }
  showForm.value = true
}

function openEdit(menu: UmsMenuNode) {
  editing.value = menu
  parentForCreate.value = null
  form.value = {
    parentId: menu.parentId ?? 0,
    title: menu.title,
    name: menu.name || '',
    icon: menu.icon || '',
    sort: menu.sort ?? 0,
    hidden: menu.hidden ?? 0,
  }
  showForm.value = true
}

async function handleSave() {
  if (!form.value.title.trim()) {
    toast.error('请填写菜单标题')
    return
  }
  saving.value = true
  try {
    const payload = { ...form.value, parentId: form.value.parentId || 0 }
    if (editing.value) {
      await updateMenu(editing.value.id, payload)
      toast.success('更新成功')
    } else {
      await createMenu(payload)
      toast.success('创建成功')
    }
    showForm.value = false
    loadData()
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '保存失败')
  } finally {
    saving.value = false
  }
}

async function toggleHidden(menu: UmsMenuNode) {
  const next = menu.hidden === 1 ? 0 : 1
  try {
    await updateMenuHidden(menu.id, next)
    toast.success(next === 1 ? '已隐藏' : '已显示')
    loadData()
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '操作失败')
  }
}

async function confirmDelete() {
  if (!deleteTarget.value) return
  deleting.value = true
  try {
    await deleteMenu(deleteTarget.value.id)
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
      <button type="button" class="btn-primary" @click="openCreate()">+ 新建顶级菜单</button>
    </div>

    <div class="admin-card overflow-x-auto p-0">
      <div v-if="loading" class="p-8 text-center text-sm text-gray-500">加载中...</div>
      <table v-else class="admin-table">
        <thead>
          <tr>
            <th>标题</th>
            <th>前端名称</th>
            <th>图标</th>
            <th>排序</th>
            <th>显示</th>
            <th class="text-right">操作</th>
          </tr>
        </thead>
        <tbody>
          <template v-if="menuTree.length === 0">
            <tr><td colspan="6" class="py-10 text-center text-gray-500">暂无菜单</td></tr>
          </template>
          <template v-for="menu in menuTree" :key="menu.id">
            <tr>
              <td class="font-medium text-white">{{ menu.title }}</td>
              <td class="text-gray-400">{{ menu.name || '-' }}</td>
              <td>{{ menu.icon || '-' }}</td>
              <td>{{ menu.sort ?? 0 }}</td>
              <td>
                <span :class="menu.hidden === 1 ? 'badge-muted' : 'badge-success'">
                  {{ menu.hidden === 1 ? '隐藏' : '显示' }}
                </span>
              </td>
              <td>
                <div class="flex justify-end gap-1">
                  <button type="button" class="btn-ghost px-2 py-1 text-xs" @click="openCreate(menu)">子菜单</button>
                  <button type="button" class="btn-ghost px-2 py-1 text-xs" @click="openEdit(menu)">编辑</button>
                  <button type="button" class="btn-ghost px-2 py-1 text-xs" @click="toggleHidden(menu)">
                    {{ menu.hidden === 1 ? '显示' : '隐藏' }}
                  </button>
                  <button type="button" class="btn-ghost px-2 py-1 text-xs text-red-400" @click="deleteTarget = menu">删除</button>
                </div>
              </td>
            </tr>
            <tr v-for="child in menu.children || []" :key="child.id">
              <td class="pl-8 text-gray-200">↳ {{ child.title }}</td>
              <td class="text-gray-400">{{ child.name || '-' }}</td>
              <td>{{ child.icon || '-' }}</td>
              <td>{{ child.sort ?? 0 }}</td>
              <td>
                <span :class="child.hidden === 1 ? 'badge-muted' : 'badge-success'">
                  {{ child.hidden === 1 ? '隐藏' : '显示' }}
                </span>
              </td>
              <td>
                <div class="flex justify-end gap-1">
                  <button type="button" class="btn-ghost px-2 py-1 text-xs" @click="openEdit(child)">编辑</button>
                  <button type="button" class="btn-ghost px-2 py-1 text-xs" @click="toggleHidden(child)">
                    {{ child.hidden === 1 ? '显示' : '隐藏' }}
                  </button>
                  <button type="button" class="btn-ghost px-2 py-1 text-xs text-red-400" @click="deleteTarget = child">删除</button>
                </div>
              </td>
            </tr>
          </template>
        </tbody>
      </table>
    </div>

    <FormModal
      :show="showForm"
      :title="editing ? '编辑菜单' : parentForCreate ? `新建子菜单 (${parentForCreate.title})` : '新建菜单'"
      :loading="saving"
      @close="showForm = false"
    >
      <div class="space-y-4">
        <div>
          <label class="admin-label">标题 *</label>
          <input v-model="form.title" type="text" class="admin-input" />
        </div>
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="admin-label">前端名称</label>
            <input v-model="form.name" type="text" class="admin-input" />
          </div>
          <div>
            <label class="admin-label">图标</label>
            <input v-model="form.icon" type="text" class="admin-input" />
          </div>
        </div>
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="admin-label">排序</label>
            <input v-model.number="form.sort" type="number" class="admin-input" />
          </div>
          <div>
            <label class="admin-label">隐藏</label>
            <select v-model="form.hidden" class="admin-input">
              <option :value="0">显示</option>
              <option :value="1">隐藏</option>
            </select>
          </div>
        </div>
      </div>
      <template #actions>
        <button type="button" class="btn-primary" :disabled="saving" @click="handleSave">保存</button>
      </template>
    </FormModal>

    <ConfirmDialog :show="!!deleteTarget" title="删除菜单" :loading="deleting" @close="deleteTarget = null" @confirm="confirmDelete">
      确定删除菜单「{{ deleteTarget?.title }}」吗？子菜单可能一并受影响。
    </ConfirmDialog>
  </div>
</template>
