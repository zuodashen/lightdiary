<script setup lang="ts">
import { onMounted, ref } from 'vue'
import {
  approveComment,
  deleteComment,
  fetchComments,
  rejectComment,
} from '@/api/comment'
import type { Comment } from '@/types'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import Pagination from '@/components/Pagination.vue'
import { useToast } from '@/composables/useToast'

const toast = useToast()
const comments = ref<Comment[]>([])
const loading = ref(true)
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const statusFilter = ref('')
const deleteTarget = ref<Comment | null>(null)
const deleting = ref(false)

const statusTabs = [
  { label: '全部', value: '' },
  { label: '待审核', value: 'PENDING' },
  { label: '已通过', value: 'APPROVED' },
  { label: '已拒绝', value: 'REJECTED' },
]

async function loadData() {
  loading.value = true
  try {
    const result = await fetchComments({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      status: statusFilter.value || undefined,
    })
    comments.value = result.list
    total.value = result.total
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(loadData)

function setStatus(value: string) {
  statusFilter.value = value
  pageNum.value = 1
  loadData()
}

function formatDate(value?: string) {
  if (!value) return '-'
  return new Date(value).toLocaleString('zh-CN')
}

function statusLabel(status: string) {
  if (status === 'APPROVED') return '已通过'
  if (status === 'REJECTED') return '已拒绝'
  if (status === 'PENDING') return '待审核'
  return status
}

function statusClass(status: string) {
  if (status === 'APPROVED') return 'badge-success'
  if (status === 'REJECTED') return 'badge-muted'
  return 'badge-warning'
}

async function handleApprove(comment: Comment) {
  try {
    await approveComment(comment.id)
    toast.success('已通过')
    loadData()
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '操作失败')
  }
}

async function handleReject(comment: Comment) {
  try {
    await rejectComment(comment.id)
    toast.success('已拒绝')
    loadData()
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '操作失败')
  }
}

async function confirmDelete() {
  if (!deleteTarget.value) return
  deleting.value = true
  try {
    await deleteComment(deleteTarget.value.id)
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
    <div class="mb-5 flex flex-wrap gap-2">
      <button
        v-for="tab in statusTabs"
        :key="tab.value"
        type="button"
        class="rounded-lg px-3 py-1.5 text-sm transition"
        :class="
          statusFilter === tab.value
            ? 'bg-primary/15 font-medium text-primary'
            : 'text-gray-400 hover:bg-[var(--color-surface-hover)]'
        "
        @click="setStatus(tab.value)"
      >
        {{ tab.label }}
      </button>
      <router-link to="/comments/settings" class="btn-secondary ml-auto text-sm">
        评论系统配置
      </router-link>
    </div>

    <div class="admin-card overflow-x-auto p-0">
      <div v-if="loading" class="p-8 text-center text-sm text-gray-500">加载中...</div>
      <table v-else class="admin-table">
        <thead>
          <tr>
            <th>作者</th>
            <th>内容</th>
            <th>文章ID</th>
            <th>状态</th>
            <th>时间</th>
            <th class="text-right">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="comments.length === 0">
            <td colspan="6" class="py-10 text-center text-gray-500">暂无评论</td>
          </tr>
          <tr v-for="comment in comments" :key="comment.id">
            <td>
              <div class="font-medium text-white">{{ comment.authorName }}</div>
              <div class="text-xs text-gray-500">{{ comment.authorEmail || '-' }}</div>
            </td>
            <td class="max-w-md">
              <p class="line-clamp-2 text-sm text-gray-300">{{ comment.content }}</p>
            </td>
            <td>{{ comment.articleId }}</td>
            <td><span :class="statusClass(comment.status)">{{ statusLabel(comment.status) }}</span></td>
            <td class="whitespace-nowrap text-xs text-gray-400">{{ formatDate(comment.createTime) }}</td>
            <td>
              <div class="flex justify-end gap-1">
                <button
                  v-if="comment.status !== 'APPROVED'"
                  type="button"
                  class="btn-ghost px-2 py-1 text-xs"
                  @click="handleApprove(comment)"
                >
                  通过
                </button>
                <button
                  v-if="comment.status !== 'REJECTED'"
                  type="button"
                  class="btn-ghost px-2 py-1 text-xs"
                  @click="handleReject(comment)"
                >
                  拒绝
                </button>
                <button
                  type="button"
                  class="btn-ghost px-2 py-1 text-xs text-red-400"
                  @click="deleteTarget = comment"
                >
                  删除
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <Pagination
      :page-num="pageNum"
      :page-size="pageSize"
      :total="total"
      @change="(p) => { pageNum = p; loadData() }"
    />

    <ConfirmDialog
      :show="!!deleteTarget"
      title="删除评论"
      :loading="deleting"
      @close="deleteTarget = null"
      @confirm="confirmDelete"
    >
      确定删除该评论吗？
    </ConfirmDialog>
  </div>
</template>
