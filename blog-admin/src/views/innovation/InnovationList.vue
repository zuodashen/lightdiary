<script setup lang="ts">
import { onMounted, ref } from 'vue'
import {
  createInnovation,
  deleteInnovation,
  fetchInnovations,
  updateInnovation,
} from '@/api/innovation'
import type { Innovation, InnovationParam } from '@/types'
import FormModal from '@/components/FormModal.vue'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import { useToast } from '@/composables/useToast'

const toast = useToast()
const items = ref<Innovation[]>([])
const loading = ref(true)
const showModal = ref(false)
const editing = ref<Innovation | null>(null)
const saving = ref(false)
const deleteTarget = ref<Innovation | null>(null)
const deleting = ref(false)

const statusOptions = [
  { value: 'IDEA', label: '构思中' },
  { value: 'BUILDING', label: '进行中' },
  { value: 'LIVE', label: '已上线' },
]

const form = ref<InnovationParam>({
  title: '',
  slug: '',
  summary: '',
  description: '',
  coverImage: '',
  demoUrl: '',
  githubUrl: '',
  techStack: '',
  status: 'BUILDING',
  isFeatured: 0,
  sortOrder: 0,
})

function statusLabel(status?: string) {
  return statusOptions.find((s) => s.value === status)?.label || status || '-'
}

async function loadData() {
  loading.value = true
  try {
    items.value = await fetchInnovations()
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(loadData)

function openCreate() {
  editing.value = null
  form.value = {
    title: '',
    slug: '',
    summary: '',
    description: '',
    coverImage: '',
    demoUrl: '',
    githubUrl: '',
    techStack: '',
    status: 'BUILDING',
    isFeatured: 0,
    sortOrder: 0,
  }
  showModal.value = true
}

function openEdit(item: Innovation) {
  editing.value = item
  form.value = {
    title: item.title,
    slug: item.slug || '',
    summary: item.summary || '',
    description: item.description || '',
    coverImage: item.coverImage || '',
    demoUrl: item.demoUrl || '',
    githubUrl: item.githubUrl || '',
    techStack: item.techStack || '',
    status: item.status || 'BUILDING',
    isFeatured: item.isFeatured ?? 0,
    sortOrder: item.sortOrder ?? 0,
  }
  showModal.value = true
}

async function handleSave() {
  if (!form.value.title.trim()) {
    toast.error('请填写项目名称')
    return
  }
  saving.value = true
  try {
    if (editing.value) {
      await updateInnovation(editing.value.id, form.value)
      toast.success('更新成功')
    } else {
      await createInnovation(form.value)
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
    await deleteInnovation(deleteTarget.value.id)
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
      <button type="button" class="btn-primary" @click="openCreate">+ 新建项目</button>
    </div>

    <div class="admin-card overflow-x-auto p-0">
      <div v-if="loading" class="p-8 text-center text-sm text-gray-500">加载中...</div>
      <table v-else class="admin-table">
        <thead>
          <tr>
            <th>项目名称</th>
            <th>状态</th>
            <th>技术栈</th>
            <th>精选</th>
            <th>排序</th>
            <th class="text-right">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="items.length === 0">
            <td colspan="6" class="py-10 text-center text-gray-500">暂无项目</td>
          </tr>
          <tr v-for="item in items" :key="item.id">
            <td>
              <div class="font-medium text-white">{{ item.title }}</div>
              <div v-if="item.summary" class="text-xs text-gray-500">{{ item.summary }}</div>
            </td>
            <td>{{ statusLabel(item.status) }}</td>
            <td class="max-w-xs truncate text-gray-400">{{ item.techStack || '-' }}</td>
            <td>{{ item.isFeatured ? '是' : '否' }}</td>
            <td>{{ item.sortOrder ?? 0 }}</td>
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
      :title="editing ? '编辑项目' : '新建项目'"
      :loading="saving"
      @close="showModal = false"
      @confirm="handleSave"
    >
      <div class="space-y-4">
        <div>
          <label class="admin-label">项目名称 *</label>
          <input v-model="form.title" class="admin-input" />
        </div>
        <div>
          <label class="admin-label">Slug</label>
          <input v-model="form.slug" class="admin-input" placeholder="留空自动生成" />
        </div>
        <div>
          <label class="admin-label">一句话介绍</label>
          <input v-model="form.summary" class="admin-input" />
        </div>
        <div>
          <label class="admin-label">详细描述</label>
          <textarea v-model="form.description" rows="4" class="admin-input resize-y" />
        </div>
        <div class="grid gap-4 sm:grid-cols-2">
          <div>
            <label class="admin-label">演示地址</label>
            <input v-model="form.demoUrl" class="admin-input" placeholder="https://..." />
          </div>
          <div>
            <label class="admin-label">GitHub 地址</label>
            <input v-model="form.githubUrl" class="admin-input" placeholder="https://github.com/..." />
          </div>
        </div>
        <div>
          <label class="admin-label">技术栈</label>
          <input v-model="form.techStack" class="admin-input" placeholder="Vue,Spring Boot,Agent" />
        </div>
        <div>
          <label class="admin-label">封面图 URL</label>
          <input v-model="form.coverImage" class="admin-input" />
        </div>
        <div class="grid gap-4 sm:grid-cols-3">
          <div>
            <label class="admin-label">状态</label>
            <select v-model="form.status" class="admin-input">
              <option v-for="opt in statusOptions" :key="opt.value" :value="opt.value">
                {{ opt.label }}
              </option>
            </select>
          </div>
          <div>
            <label class="admin-label">排序</label>
            <input v-model.number="form.sortOrder" type="number" class="admin-input" />
          </div>
          <label class="flex items-end gap-2 pb-2 text-sm text-gray-300">
            <input v-model="form.isFeatured" type="checkbox" :true-value="1" :false-value="0" class="accent-primary" />
            精选展示
          </label>
        </div>
      </div>
    </FormModal>

    <ConfirmDialog
      :show="!!deleteTarget"
      title="删除项目"
      :loading="deleting"
      @close="deleteTarget = null"
      @confirm="confirmDelete"
    >
      确定删除「{{ deleteTarget?.title }}」吗？
    </ConfirmDialog>
  </div>
</template>
