<script setup lang="ts">
defineProps<{
  show: boolean
  title?: string
  loading?: boolean
}>()

const emit = defineEmits<{
  close: []
  confirm: []
}>()
</script>

<template>
  <Teleport to="body">
    <Transition name="modal">
      <div
        v-if="show"
        class="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4 backdrop-blur-sm"
        @click.self="emit('close')"
      >
        <div
          class="w-full max-w-lg rounded-xl border border-[var(--color-border)] bg-[var(--color-surface-elevated)] shadow-2xl"
        >
          <div class="border-b border-[var(--color-border)] px-5 py-4">
            <h3 class="text-base font-semibold text-white">{{ title || '确认' }}</h3>
          </div>
          <div class="px-5 py-4 text-sm text-gray-300">
            <slot />
          </div>
          <div class="flex justify-end gap-3 border-t border-[var(--color-border)] px-5 py-4">
            <button type="button" class="btn-secondary" :disabled="loading" @click="emit('close')">
              取消
            </button>
            <button type="button" class="btn-primary" :disabled="loading" @click="emit('confirm')">
              {{ loading ? '处理中...' : '确定' }}
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.2s ease;
}
.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}
</style>
