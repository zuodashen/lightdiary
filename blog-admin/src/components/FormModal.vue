<script setup lang="ts">
defineProps<{
  show: boolean
  title: string
  loading?: boolean
  confirmText?: string
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
          class="flex max-h-[90vh] w-full max-w-2xl flex-col overflow-hidden rounded-xl border border-[var(--color-border)] bg-[var(--color-surface-elevated)] shadow-2xl"
        >
          <div class="flex shrink-0 items-center justify-between border-b border-[var(--color-border)] px-5 py-4">
            <h3 class="text-base font-semibold text-white">{{ title }}</h3>
            <button type="button" class="btn-ghost px-2 py-1" @click="emit('close')">✕</button>
          </div>
          <form class="flex min-h-0 flex-1 flex-col" @submit.prevent="emit('confirm')">
            <div class="min-h-0 flex-1 overflow-y-auto p-5">
              <slot />
            </div>
            <div class="flex shrink-0 justify-end gap-3 border-t border-[var(--color-border)] px-5 py-4">
              <button type="button" class="btn-secondary" :disabled="loading" @click="emit('close')">
                取消
              </button>
              <slot name="actions">
                <button type="submit" class="btn-primary" :disabled="loading">
                  {{ loading ? '保存中...' : (confirmText || '保存') }}
                </button>
              </slot>
            </div>
          </form>
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
