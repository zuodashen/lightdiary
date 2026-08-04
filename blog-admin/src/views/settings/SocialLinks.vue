<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { createSocialLink, deleteSocialLink, fetchSocialLinks } from '@/api/site'
import type { SocialLink } from '@/types'
import FormModal from '@/components/FormModal.vue'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import { useToast } from '@/composables/useToast'

const toast = useToast()
const links = ref<SocialLink[]>([])
const loading = ref(true)
const showModal = ref(false)
const saving = ref(false)
const deleteTarget = ref<SocialLink | null>(null)
const deleting = ref(false)

const form = ref<SocialLink>({
  id: 0,
  platform: '',
  icon: '',
  url: '',
  sortOrder: 0,
})

async function loadData() {
  loading.value = true
  try {
    links.value = await fetchSocialLinks()
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(loadData)

function openCreate() {
  form.value = { id: 0, platform: '', icon: '', url: '', sortOrder: 0 }
  showModal.value = true
}

async function handleSave() {
  if (!form.value.platform.trim() || !form.value.url.trim()) {
    toast.error('请填写平台和链接')
    return
  }
  saving.value = true
  try {
    await createSocialLink(form.value)
    toast.success('创建成功')
    showModal.value = false
    loadData()
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '创建失败')
  } finally {
    saving.value = false
  }
}

async function confirmDelete() {
  if (!deleteTarget.value) return
  deleting.value = true
  try {
    await deleteSocialLink(deleteTarget.value.id)
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
      <button type="button" class="btn-primary" @click="openCreate">+ 新建链接</button>
    </div>

    <div class="admin-card overflow-x-auto p-0">
      <div v-if="loading" class="p-8 text-center text-sm text-gray-500">加载中...</div>
      <table v-else class="admin-table">
        <thead>
          <tr>
            <th>平台</th>
            <th>图标</th>
            <th>链接</th>
            <th>排序</th>
            <th class="text-right">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="links.length === 0">
            <td colspan="5" class="py-10 text-center text-gray-500">暂无社交链接</td>
          </tr>
          <tr v-for="link in links" :key="link.id">
            <td class="font-medium text-white">{{ link.platform }}</td>
            <td>{{ link.icon || '-' }}</td>
            <td class="max-w-xs truncate">
              <a :href="link.url" target="_blank" rel="noopener" class="text-primary hover:underline">
                {{ link.url }}
              </a>
            </td>
            <td>{{ link.sortOrder ?? 0 }}</td>
            <td>
              <div class="flex justify-end">
                <button
                  type="button"
                  class="btn-ghost px-2 py-1 text-xs text-red-400"
                  @click="deleteTarget = link"
                >
                  删除
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <FormModal :show="showModal" title="新建社交链接" :loading="saving" @close="showModal = false">
      <div class="space-y-4">
        <div>
          <label class="admin-label">平台 *</label>
          <input v-model="form.platform" type="text" class="admin-input" placeholder="GitHub" />
        </div>
        <div>
          <label class="admin-label">链接 *</label>
          <input v-model="form.url" type="url" class="admin-input" placeholder="https://github.com/..." />
        </div>
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="admin-label">图标</label>
            <input v-model="form.icon" type="text" class="admin-input" placeholder="fab fa-github" />
          </div>
          <div>
            <label class="admin-label">排序</label>
            <input v-model.number="form.sortOrder" type="number" class="admin-input" />
          </div>
        </div>
      </div>
      <template #actions>
        <button type="button" class="btn-primary" :disabled="saving" @click="handleSave">
          {{ saving ? '创建中...' : '创建' }}
        </button>
      </template>
    </FormModal>

    <ConfirmDialog
      :show="!!deleteTarget"
      title="删除社交链接"
      :loading="deleting"
      @close="deleteTarget = null"
      @confirm="confirmDelete"
    >
      确定删除「{{ deleteTarget?.platform }}」吗？
    </ConfirmDialog>
  </div>
</template>
