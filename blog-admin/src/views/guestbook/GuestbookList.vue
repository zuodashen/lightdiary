<script setup lang="ts">
import { onMounted, ref } from 'vue'
import {
  approveGuestbook,
  deleteGuestbook,
  fetchGuestbookList,
  rejectGuestbook,
} from '@/api/guestbook'
import type { GuestbookMessage } from '@/types'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import Pagination from '@/components/Pagination.vue'
import { useToast } from '@/composables/useToast'

const toast = useToast()
const messages = ref<GuestbookMessage[]>([])
const loading = ref(true)
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const statusFilter = ref('')
const deleteTarget = ref<GuestbookMessage | null>(null)
const deleting = ref(false)

const statusTabs = [
  { label: '全部', value: '' },
  { label: '待审核', value: 'PENDING' },
  { label: '已通过', value: 'APPROVED' },
  { label: '已拒绝', value: 'SPAM' },
]

async function loadData() {
  loading.value = true
  try {
    const result = await fetchGuestbookList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      status: statusFilter.value || undefined,
    })
    messages.value = result.list
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
  if (status === 'SPAM') return '已拒绝'
  if (status === 'PENDING') return '待审核'
  return status
}

function statusClass(status: string) {
  if (status === 'APPROVED') return 'badge-success'
  if (status === 'SPAM') return 'badge-muted'
  return 'badge-warning'
}

async function handleApprove(message: GuestbookMessage) {
  try {
    await approveGuestbook(message.id)
    toast.success('已通过')
    loadData()
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '操作失败')
  }
}

async function handleReject(message: GuestbookMessage) {
  try {
    await rejectGuestbook(message.id)
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
    await deleteGuestbook(deleteTarget.value.id)
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
    </div>

    <div class="admin-card overflow-x-auto p-0">
      <div v-if="loading" class="p-8 text-center text-sm text-gray-500">加载中...</div>
      <table v-else class="admin-table">
        <thead>
          <tr>
            <th>作者</th>
            <th>内容</th>
            <th>点赞</th>
            <th>状态</th>
            <th>时间</th>
            <th class="text-right">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="messages.length === 0">
            <td colspan="6" class="py-10 text-center text-gray-500">暂无留言</td>
          </tr>
          <tr v-for="message in messages" :key="message.id">
            <td>
              <div class="font-medium text-white">{{ message.authorName }}</div>
              <div class="text-xs text-gray-500">{{ message.authorEmail || '-' }}</div>
            </td>
            <td class="max-w-md">
              <p class="line-clamp-2 text-sm text-gray-300">{{ message.content }}</p>
            </td>
            <td>{{ message.likes ?? 0 }}</td>
            <td><span :class="statusClass(message.status)">{{ statusLabel(message.status) }}</span></td>
            <td class="whitespace-nowrap text-xs text-gray-400">{{ formatDate(message.createTime) }}</td>
            <td>
              <div class="flex justify-end gap-1">
                <button
                  v-if="message.status !== 'APPROVED'"
                  type="button"
                  class="btn-ghost px-2 py-1 text-xs"
                  @click="handleApprove(message)"
                >
                  通过
                </button>
                <button
                  v-if="message.status !== 'SPAM'"
                  type="button"
                  class="btn-ghost px-2 py-1 text-xs"
                  @click="handleReject(message)"
                >
                  拒绝
                </button>
                <button
                  type="button"
                  class="btn-ghost px-2 py-1 text-xs text-red-400"
                  @click="deleteTarget = message"
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
      title="删除留言"
      :loading="deleting"
      @close="deleteTarget = null"
      @confirm="confirmDelete"
    >
      确定删除该留言吗？
    </ConfirmDialog>
  </div>
</template>
