<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { createTag, deleteTag, fetchTags } from '@/api/tag'
import type { Tag } from '@/types'
import FormModal from '@/components/FormModal.vue'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import { useToast } from '@/composables/useToast'

const toast = useToast()
const tags = ref<Tag[]>([])
const loading = ref(true)
const showModal = ref(false)
const tagName = ref('')
const saving = ref(false)
const deleteTarget = ref<Tag | null>(null)
const deleting = ref(false)

async function loadData() {
  loading.value = true
  try {
    tags.value = await fetchTags()
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(loadData)

async function handleSave() {
  if (!tagName.value.trim()) {
    toast.error('请填写标签名称')
    return
  }
  saving.value = true
  try {
    await createTag(tagName.value.trim())
    toast.success('创建成功')
    showModal.value = false
    tagName.value = ''
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
    await deleteTag(deleteTarget.value.id)
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
      <button type="button" class="btn-primary" @click="showModal = true">+ 新建标签</button>
    </div>

    <div class="admin-card overflow-x-auto p-0">
      <div v-if="loading" class="p-8 text-center text-sm text-gray-500">加载中...</div>
      <table v-else class="admin-table">
        <thead>
          <tr>
            <th>名称</th>
            <th>Slug</th>
            <th>文章数</th>
            <th class="text-right">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="tags.length === 0">
            <td colspan="4" class="py-10 text-center text-gray-500">暂无标签</td>
          </tr>
          <tr v-for="tag in tags" :key="tag.id">
            <td class="font-medium text-white">{{ tag.name }}</td>
            <td class="text-gray-400">{{ tag.slug }}</td>
            <td>{{ tag.articleCount ?? 0 }}</td>
            <td>
              <div class="flex justify-end">
                <button
                  type="button"
                  class="btn-ghost px-2 py-1 text-xs text-red-400"
                  @click="deleteTarget = tag"
                >
                  删除
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <FormModal :show="showModal" title="新建标签" :loading="saving" @close="showModal = false">
      <div>
        <label class="admin-label">标签名称 *</label>
        <input v-model="tagName" type="text" class="admin-input" placeholder="例如：Vue" />
      </div>
      <template #actions>
        <button type="button" class="btn-primary" :disabled="saving" @click="handleSave">
          {{ saving ? '创建中...' : '创建' }}
        </button>
      </template>
    </FormModal>

    <ConfirmDialog
      :show="!!deleteTarget"
      title="删除标签"
      :loading="deleting"
      @close="deleteTarget = null"
      @confirm="confirmDelete"
    >
      确定删除标签「{{ deleteTarget?.name }}」吗？
    </ConfirmDialog>
  </div>
</template>
