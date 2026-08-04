<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import {
  allocRoleMenus,
  allocRoleResources,
  createRole,
  deleteRoles,
  fetchRoleMenus,
  fetchRoleResources,
  fetchRoles,
  updateRole,
  updateRoleStatus,
} from '@/api/role'
import { fetchAllResources } from '@/api/resource'
import { fetchMenuTree } from '@/api/menu'
import type { UmsMenuNode, UmsRole } from '@/types'
import CheckboxPicker from '@/components/CheckboxPicker.vue'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import FormModal from '@/components/FormModal.vue'
import Pagination from '@/components/Pagination.vue'
import { useToast } from '@/composables/useToast'

const toast = useToast()
const roles = ref<UmsRole[]>([])
const menuTree = ref<UmsMenuNode[]>([])
const allResources = ref<{ id: number; name: string; url: string; categoryId?: number }[]>([])
const loading = ref(true)
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const keyword = ref('')

const showForm = ref(false)
const editing = ref<UmsRole | null>(null)
const saving = ref(false)
const deleteTarget = ref<UmsRole | null>(null)
const deleting = ref(false)

const showMenuPicker = ref(false)
const showResourcePicker = ref(false)
const pickerTarget = ref<UmsRole | null>(null)
const selectedIds = ref<number[]>([])
const pickerSaving = ref(false)

const form = ref({ name: '', description: '', sort: 0, status: 1 })

function flattenMenus(nodes: UmsMenuNode[], level = 0): { id: number; label: string }[] {
  const result: { id: number; label: string }[] = []
  for (const node of nodes) {
    result.push({ id: node.id, label: `${'　'.repeat(level)}${node.title}` })
    if (node.children?.length) {
      result.push(...flattenMenus(node.children, level + 1))
    }
  }
  return result
}

const menuOptions = computed(() => flattenMenus(menuTree.value))

const resourceOptions = computed(() =>
  allResources.value.map((r) => ({
    id: r.id,
    label: `${r.name} (${r.url})`,
  })),
)

async function loadData() {
  loading.value = true
  try {
    const result = await fetchRoles({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: keyword.value || undefined,
    })
    roles.value = result.list
    total.value = result.total
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  try {
    const [menus, resources] = await Promise.all([fetchMenuTree(), fetchAllResources()])
    menuTree.value = menus
    allResources.value = resources
  } catch {
    // ignore
  }
  await loadData()
})

function openCreate() {
  editing.value = null
  form.value = { name: '', description: '', sort: 0, status: 1 }
  showForm.value = true
}

function openEdit(role: UmsRole) {
  editing.value = role
  form.value = {
    name: role.name,
    description: role.description || '',
    sort: role.sort ?? 0,
    status: role.status ?? 1,
  }
  showForm.value = true
}

async function handleSave() {
  if (!form.value.name.trim()) {
    toast.error('请填写角色名称')
    return
  }
  saving.value = true
  try {
    if (editing.value) {
      await updateRole(editing.value.id, form.value)
      toast.success('更新成功')
    } else {
      await createRole(form.value)
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

async function toggleStatus(role: UmsRole) {
  const next = role.status === 1 ? 0 : 1
  try {
    await updateRoleStatus(role.id, next)
    toast.success(next === 1 ? '已启用' : '已禁用')
    loadData()
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '操作失败')
  }
}

async function openMenuPicker(role: UmsRole) {
  pickerTarget.value = role
  try {
    const menus = await fetchRoleMenus(role.id)
    selectedIds.value = menus.map((m) => m.id)
    showMenuPicker.value = true
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '加载菜单失败')
  }
}

async function openResourcePicker(role: UmsRole) {
  pickerTarget.value = role
  try {
    const resources = await fetchRoleResources(role.id)
    selectedIds.value = resources.map((r) => r.id)
    showResourcePicker.value = true
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '加载资源失败')
  }
}

async function saveMenus(ids: number[]) {
  if (!pickerTarget.value) return
  pickerSaving.value = true
  try {
    await allocRoleMenus(pickerTarget.value.id, ids)
    toast.success('菜单分配成功')
    showMenuPicker.value = false
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '保存失败')
  } finally {
    pickerSaving.value = false
  }
}

async function saveResources(ids: number[]) {
  if (!pickerTarget.value) return
  pickerSaving.value = true
  try {
    await allocRoleResources(pickerTarget.value.id, ids)
    toast.success('资源分配成功')
    showResourcePicker.value = false
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '保存失败')
  } finally {
    pickerSaving.value = false
  }
}

async function confirmDelete() {
  if (!deleteTarget.value) return
  deleting.value = true
  try {
    await deleteRoles([deleteTarget.value.id])
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
    <div class="mb-5 flex flex-wrap items-center justify-between gap-3">
      <div class="flex gap-3">
        <input
          v-model="keyword"
          type="search"
          class="admin-input w-56"
          placeholder="搜索角色..."
          @keyup.enter="() => { pageNum = 1; loadData() }"
        />
        <button type="button" class="btn-secondary" @click="() => { pageNum = 1; loadData() }">搜索</button>
      </div>
      <button type="button" class="btn-primary" @click="openCreate">+ 新建角色</button>
    </div>

    <div class="admin-card overflow-x-auto p-0">
      <div v-if="loading" class="p-8 text-center text-sm text-gray-500">加载中...</div>
      <table v-else class="admin-table">
        <thead>
          <tr>
            <th>名称</th>
            <th>描述</th>
            <th>用户数</th>
            <th>排序</th>
            <th>状态</th>
            <th class="text-right">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="roles.length === 0">
            <td colspan="6" class="py-10 text-center text-gray-500">暂无角色</td>
          </tr>
          <tr v-for="role in roles" :key="role.id">
            <td class="font-medium text-white">{{ role.name }}</td>
            <td class="text-gray-400">{{ role.description || '-' }}</td>
            <td>{{ role.adminCount ?? 0 }}</td>
            <td>{{ role.sort ?? 0 }}</td>
            <td>
              <span :class="role.status === 1 ? 'badge-success' : 'badge-muted'">
                {{ role.status === 1 ? '启用' : '禁用' }}
              </span>
            </td>
            <td>
              <div class="flex flex-wrap justify-end gap-1">
                <button type="button" class="btn-ghost px-2 py-1 text-xs" @click="openEdit(role)">编辑</button>
                <button type="button" class="btn-ghost px-2 py-1 text-xs" @click="openMenuPicker(role)">菜单</button>
                <button type="button" class="btn-ghost px-2 py-1 text-xs" @click="openResourcePicker(role)">资源</button>
                <button type="button" class="btn-ghost px-2 py-1 text-xs" @click="toggleStatus(role)">
                  {{ role.status === 1 ? '禁用' : '启用' }}
                </button>
                <button type="button" class="btn-ghost px-2 py-1 text-xs text-red-400" @click="deleteTarget = role">
                  删除
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <Pagination :page-num="pageNum" :page-size="pageSize" :total="total" @change="(p) => { pageNum = p; loadData() }" />

    <FormModal :show="showForm" :title="editing ? '编辑角色' : '新建角色'" :loading="saving" @close="showForm = false">
      <div class="space-y-4">
        <div>
          <label class="admin-label">名称 *</label>
          <input v-model="form.name" type="text" class="admin-input" />
        </div>
        <div>
          <label class="admin-label">描述</label>
          <input v-model="form.description" type="text" class="admin-input" />
        </div>
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="admin-label">排序</label>
            <input v-model.number="form.sort" type="number" class="admin-input" />
          </div>
          <div>
            <label class="admin-label">状态</label>
            <select v-model="form.status" class="admin-input">
              <option :value="1">启用</option>
              <option :value="0">禁用</option>
            </select>
          </div>
        </div>
      </div>
      <template #actions>
        <button type="button" class="btn-primary" :disabled="saving" @click="handleSave">保存</button>
      </template>
    </FormModal>

    <CheckboxPicker
      :show="showMenuPicker"
      title="分配菜单"
      :options="menuOptions"
      :selected="selectedIds"
      :loading="pickerSaving"
      @close="showMenuPicker = false"
      @confirm="saveMenus"
    />

    <CheckboxPicker
      :show="showResourcePicker"
      title="分配资源"
      :options="resourceOptions"
      :selected="selectedIds"
      :loading="pickerSaving"
      @close="showResourcePicker = false"
      @confirm="saveResources"
    />

    <ConfirmDialog :show="!!deleteTarget" title="删除角色" :loading="deleting" @close="deleteTarget = null" @confirm="confirmDelete">
      确定删除角色「{{ deleteTarget?.name }}」吗？
    </ConfirmDialog>
  </div>
</template>
