<script setup lang="ts">
import { onMounted, ref } from 'vue'
import {
  deleteAdmin,
  fetchAdminRoles,
  fetchAdmins,
  registerAdmin,
  updateAdmin,
  updateAdminRoles,
  updateAdminStatus,
} from '@/api/admin'
import { fetchAllRoles } from '@/api/role'
import type { UmsAdmin, UmsAdminParam, UmsRole } from '@/types'
import CheckboxPicker from '@/components/CheckboxPicker.vue'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import FormModal from '@/components/FormModal.vue'
import Pagination from '@/components/Pagination.vue'
import { useToast } from '@/composables/useToast'

const toast = useToast()
const admins = ref<UmsAdmin[]>([])
const allRoles = ref<UmsRole[]>([])
const loading = ref(true)
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const keyword = ref('')

const showForm = ref(false)
const editing = ref<UmsAdmin | null>(null)
const saving = ref(false)
const deleteTarget = ref<UmsAdmin | null>(null)
const deleting = ref(false)

const showRolePicker = ref(false)
const roleTarget = ref<UmsAdmin | null>(null)
const selectedRoleIds = ref<number[]>([])
const roleSaving = ref(false)

const form = ref<UmsAdminParam & { status?: number }>({
  username: '',
  password: '',
  email: '',
  nickName: '',
  note: '',
  icon: '',
  status: 1,
})

async function loadData() {
  loading.value = true
  try {
    const result = await fetchAdmins({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: keyword.value || undefined,
    })
    admins.value = result.list
    total.value = result.total
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  try {
    allRoles.value = await fetchAllRoles()
  } catch {
    // ignore
  }
  await loadData()
})

function formatDate(value?: string) {
  if (!value) return '-'
  return new Date(value).toLocaleString('zh-CN')
}

function openCreate() {
  editing.value = null
  form.value = { username: '', password: '', email: '', nickName: '', note: '', icon: '', status: 1 }
  showForm.value = true
}

function openEdit(admin: UmsAdmin) {
  editing.value = admin
  form.value = {
    username: admin.username,
    password: '',
    email: admin.email || '',
    nickName: admin.nickName || '',
    note: admin.note || '',
    icon: admin.icon || '',
    status: admin.status ?? 1,
  }
  showForm.value = true
}

async function handleSave() {
  if (!form.value.username.trim()) {
    toast.error('请填写用户名')
    return
  }
  if (!editing.value && !form.value.password) {
    toast.error('请填写密码')
    return
  }
  saving.value = true
  try {
    if (editing.value) {
      const payload: Partial<UmsAdmin> = {
        email: form.value.email,
        nickName: form.value.nickName,
        note: form.value.note,
        icon: form.value.icon,
        status: form.value.status,
      }
      if (form.value.password) payload.password = form.value.password
      await updateAdmin(editing.value.id, payload)
      toast.success('更新成功')
    } else {
      await registerAdmin(form.value)
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

async function toggleStatus(admin: UmsAdmin) {
  const next = admin.status === 1 ? 0 : 1
  try {
    await updateAdminStatus(admin.id, next)
    toast.success(next === 1 ? '已启用' : '已禁用')
    loadData()
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '操作失败')
  }
}

async function openRolePicker(admin: UmsAdmin) {
  roleTarget.value = admin
  try {
    const roles = await fetchAdminRoles(admin.id)
    selectedRoleIds.value = roles.map((r) => r.id)
    showRolePicker.value = true
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '加载角色失败')
  }
}

async function saveRoles(ids: number[]) {
  if (!roleTarget.value) return
  roleSaving.value = true
  try {
    await updateAdminRoles(roleTarget.value.id, ids)
    toast.success('角色分配成功')
    showRolePicker.value = false
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '保存失败')
  } finally {
    roleSaving.value = false
  }
}

async function confirmDelete() {
  if (!deleteTarget.value) return
  deleting.value = true
  try {
    await deleteAdmin(deleteTarget.value.id)
    toast.success('删除成功')
    deleteTarget.value = null
    loadData()
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '删除失败')
  } finally {
    deleting.value = false
  }
}

const roleOptions = () => allRoles.value.map((r) => ({ id: r.id, label: r.name }))
</script>

<template>
  <div>
    <div class="mb-5 flex flex-wrap items-center justify-between gap-3">
      <div class="flex gap-3">
        <input
          v-model="keyword"
          type="search"
          class="admin-input w-56"
          placeholder="搜索用户名..."
          @keyup.enter="() => { pageNum = 1; loadData() }"
        />
        <button type="button" class="btn-secondary" @click="() => { pageNum = 1; loadData() }">搜索</button>
      </div>
      <button type="button" class="btn-primary" @click="openCreate">+ 新建用户</button>
    </div>

    <div class="admin-card overflow-x-auto p-0">
      <div v-if="loading" class="p-8 text-center text-sm text-gray-500">加载中...</div>
      <table v-else class="admin-table">
        <thead>
          <tr>
            <th>用户名</th>
            <th>昵称</th>
            <th>邮箱</th>
            <th>状态</th>
            <th>最后登录</th>
            <th class="text-right">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="admins.length === 0">
            <td colspan="6" class="py-10 text-center text-gray-500">暂无用户</td>
          </tr>
          <tr v-for="admin in admins" :key="admin.id">
            <td class="font-medium text-white">{{ admin.username }}</td>
            <td>{{ admin.nickName || '-' }}</td>
            <td class="text-gray-400">{{ admin.email || '-' }}</td>
            <td>
              <span :class="admin.status === 1 ? 'badge-success' : 'badge-muted'">
                {{ admin.status === 1 ? '启用' : '禁用' }}
              </span>
            </td>
            <td class="text-xs text-gray-400">{{ formatDate(admin.loginTime) }}</td>
            <td>
              <div class="flex justify-end gap-1">
                <button type="button" class="btn-ghost px-2 py-1 text-xs" @click="openEdit(admin)">编辑</button>
                <button type="button" class="btn-ghost px-2 py-1 text-xs" @click="openRolePicker(admin)">角色</button>
                <button type="button" class="btn-ghost px-2 py-1 text-xs" @click="toggleStatus(admin)">
                  {{ admin.status === 1 ? '禁用' : '启用' }}
                </button>
                <button type="button" class="btn-ghost px-2 py-1 text-xs text-red-400" @click="deleteTarget = admin">
                  删除
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <Pagination :page-num="pageNum" :page-size="pageSize" :total="total" @change="(p) => { pageNum = p; loadData() }" />

    <FormModal :show="showForm" :title="editing ? '编辑用户' : '新建用户'" :loading="saving" @close="showForm = false">
      <div class="space-y-4">
        <div>
          <label class="admin-label">用户名 *</label>
          <input v-model="form.username" type="text" class="admin-input" :disabled="!!editing" />
        </div>
        <div>
          <label class="admin-label">{{ editing ? '新密码（留空不修改）' : '密码 *' }}</label>
          <input v-model="form.password" type="password" class="admin-input" />
        </div>
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="admin-label">昵称</label>
            <input v-model="form.nickName" type="text" class="admin-input" />
          </div>
          <div>
            <label class="admin-label">邮箱</label>
            <input v-model="form.email" type="email" class="admin-input" />
          </div>
        </div>
        <div>
          <label class="admin-label">备注</label>
          <input v-model="form.note" type="text" class="admin-input" />
        </div>
        <div v-if="editing">
          <label class="admin-label">状态</label>
          <select v-model="form.status" class="admin-input">
            <option :value="1">启用</option>
            <option :value="0">禁用</option>
          </select>
        </div>
      </div>
      <template #actions>
        <button type="button" class="btn-primary" :disabled="saving" @click="handleSave">
          {{ saving ? '保存中...' : '保存' }}
        </button>
      </template>
    </FormModal>

    <CheckboxPicker
      :show="showRolePicker"
      title="分配角色"
      :options="roleOptions()"
      :selected="selectedRoleIds"
      :loading="roleSaving"
      @close="showRolePicker = false"
      @confirm="saveRoles"
    />

    <ConfirmDialog :show="!!deleteTarget" title="删除用户" :loading="deleting" @close="deleteTarget = null" @confirm="confirmDelete">
      确定删除用户「{{ deleteTarget?.username }}」吗？
    </ConfirmDialog>
  </div>
</template>
