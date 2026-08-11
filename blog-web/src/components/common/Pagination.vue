<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  pageNum: number
  totalPage: number
  total?: number
  unit?: string
}>()

const emit = defineEmits<{
  change: [page: number]
}>()

const pages = computed(() => {
  const result: (number | '...')[] = []
  const { pageNum, totalPage } = props
  if (totalPage <= 7) {
    for (let i = 1; i <= totalPage; i++) result.push(i)
    return result
  }
  result.push(1)
  if (pageNum > 3) result.push('...')
  const start = Math.max(2, pageNum - 1)
  const end = Math.min(totalPage - 1, pageNum + 1)
  for (let i = start; i <= end; i++) result.push(i)
  if (pageNum < totalPage - 2) result.push('...')
  result.push(totalPage)
  return result
})

function goTo(page: number) {
  if (page < 1 || page > props.totalPage || page === props.pageNum) return
  emit('change', page)
}
</script>

<template>
  <nav
    v-if="totalPage > 1"
    class="mt-10 flex flex-wrap items-center justify-center gap-2"
    aria-label="分页"
  >
    <button
      class="btn-ghost disabled:opacity-40"
      :disabled="pageNum <= 1"
      @click="goTo(pageNum - 1)"
    >
      ← 上一页
    </button>

    <template v-for="(page, idx) in pages" :key="idx">
      <span v-if="page === '...'" class="px-2 text-muted">…</span>
      <button
        v-else
        class="min-w-9 rounded-full px-3 py-1.5 text-sm transition-all duration-200"
        :class="
          page === pageNum
            ? 'btn-primary !py-1.5 !px-3 min-w-9 shadow-md'
            : 'btn-ghost'
        "
        @click="goTo(page)"
      >
        {{ page }}
      </button>
    </template>

    <button
      class="btn-ghost disabled:opacity-40"
      :disabled="pageNum >= totalPage"
      @click="goTo(pageNum + 1)"
    >
      下一页 →
    </button>

    <span v-if="total !== undefined" class="ml-2 text-xs text-muted">
      共 {{ total }} {{ unit ?? '篇' }}
    </span>
  </nav>
</template>

<style scoped>
.text-muted {
  color: var(--color-muted, #8b8b9a);
}
</style>
