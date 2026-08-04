<script setup lang="ts">
import { useToast } from '@/composables/useToast'

const { toasts } = useToast()

function typeClass(type: string) {
  if (type === 'success') return 'border-emerald-500/30 bg-emerald-500/10 text-emerald-300'
  if (type === 'error') return 'border-red-500/30 bg-red-500/10 text-red-300'
  return 'border-blue-500/30 bg-blue-500/10 text-blue-300'
}
</script>

<template>
  <div class="fixed right-4 top-4 z-50 flex flex-col gap-2">
    <TransitionGroup name="toast">
      <div
        v-for="toast in toasts"
        :key="toast.id"
        class="min-w-[240px] rounded-lg border px-4 py-3 text-sm shadow-lg backdrop-blur"
        :class="typeClass(toast.type)"
      >
        {{ toast.message }}
      </div>
    </TransitionGroup>
  </div>
</template>

<style scoped>
.toast-enter-active,
.toast-leave-active {
  transition: all 0.25s ease;
}
.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translateX(20px);
}
</style>
