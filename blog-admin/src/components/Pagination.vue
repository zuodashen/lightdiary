<script setup lang="ts">
defineProps<{
  pageNum: number
  pageSize: number
  total: number
}>()

const emit = defineEmits<{
  change: [page: number]
}>()

function totalPages(total: number, pageSize: number) {
  return Math.max(1, Math.ceil(total / pageSize))
}
</script>

<template>
  <div
    v-if="total > pageSize"
    class="mt-4 flex items-center justify-between text-sm text-gray-400"
  >
    <span>共 {{ total }} 条</span>
    <div class="flex items-center gap-2">
      <button
        type="button"
        class="btn-secondary px-3 py-1"
        :disabled="pageNum <= 1"
        @click="emit('change', pageNum - 1)"
      >
        上一页
      </button>
      <span>第 {{ pageNum }} / {{ totalPages(total, pageSize) }} 页</span>
      <button
        type="button"
        class="btn-secondary px-3 py-1"
        :disabled="pageNum >= totalPages(total, pageSize)"
        @click="emit('change', pageNum + 1)"
      >
        下一页
      </button>
    </div>
  </div>
</template>
