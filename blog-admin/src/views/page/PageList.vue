<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { deletePage, fetchPages } from '@/api/page'
import type { BlogPage } from '@/types'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import { useToast } from '@/composables/useToast'

const router = useRouter()
const toast = useToast()
const pages = ref<BlogPage[]>([])
const loading = ref(true)
const deleteTarget = ref<BlogPage | null>(null)
const deleting = ref(false)

async function loadData() {
  loading.value = true
  try {
    pages.value = await fetchPages()
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(loadData)

async function confirmDelete() {
  if (!deleteTarget.value) return
  deleting.value = true
  try {
    await deletePage(deleteTarget.value.id)
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
      <router-link to="/pages/new" class="btn-primary">+ 新建页面</router-link>
    </div>

    <div class="admin-card overflow-x-auto p-0">
      <div v-if="loading" class="p-8 text-center text-sm text-gray-500">加载中...</div>
      <table v-else class="admin-table">
        <thead>
          <tr>
            <th>标题</th>
            <th>Slug</th>
            <th>状态</th>
            <th>模板</th>
            <th class="text-right">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="pages.length === 0">
            <td colspan="5" class="py-10 text-center text-gray-500">暂无页面</td>
          </tr>
          <tr v-for="page in pages" :key="page.id">
            <td class="font-medium text-white">{{ page.title }}</td>
            <td class="text-gray-400">{{ page.slug }}</td>
            <td>
              <span :class="page.status === 'PUBLISHED' ? 'badge-success' : 'badge-warning'">
                {{ page.status === 'PUBLISHED' ? '已发布' : '草稿' }}
              </span>
            </td>
            <td class="text-gray-400">{{ page.template || 'default' }}</td>
            <td>
              <div class="flex justify-end gap-1">
                <button
                  type="button"
                  class="btn-ghost px-2 py-1 text-xs"
                  @click="router.push(`/pages/${page.id}/edit`)"
                >
                  编辑
                </button>
                <button
                  type="button"
                  class="btn-ghost px-2 py-1 text-xs text-red-400"
                  @click="deleteTarget = page"
                >
                  删除
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <ConfirmDialog
      :show="!!deleteTarget"
      title="删除页面"
      :loading="deleting"
      @close="deleteTarget = null"
      @confirm="confirmDelete"
    >
      确定删除页面「{{ deleteTarget?.title }}」吗？
    </ConfirmDialog>
  </div>
</template>
