<script setup lang="ts">
defineProps<{
  show: boolean
  title: string
  loading?: boolean
}>()

const emit = defineEmits<{
  close: []
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
          class="max-h-[90vh] w-full max-w-2xl overflow-y-auto rounded-xl border border-[var(--color-border)] bg-[var(--color-surface-elevated)] shadow-2xl"
        >
          <div class="sticky top-0 flex items-center justify-between border-b border-[var(--color-border)] bg-[var(--color-surface-elevated)] px-5 py-4">
            <h3 class="text-base font-semibold text-white">{{ title }}</h3>
            <button type="button" class="btn-ghost px-2 py-1" @click="emit('close')">✕</button>
          </div>
          <form class="p-5" @submit.prevent>
            <slot />
            <div class="mt-6 flex justify-end gap-3">
              <button type="button" class="btn-secondary" :disabled="loading" @click="emit('close')">
                取消
              </button>
              <slot name="actions" />
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
