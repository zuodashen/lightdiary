<script setup lang="ts">
defineProps<{
  text?: string
  fullscreen?: boolean
}>()
</script>

<template>
  <div
    class="flex flex-col items-center justify-center gap-3"
    :class="fullscreen ? 'fixed inset-0 z-50 bg-[#0a0a0c]/80 backdrop-blur-sm' : 'py-16'"
  >
    <div class="relative h-10 w-10">
      <div
        class="absolute inset-0 animate-spin rounded-full border-2 border-transparent border-t-primary border-r-secondary"
      />
      <div
        class="absolute inset-1 animate-pulse rounded-full bg-primary/20"
      />
    </div>
    <p v-if="text" class="text-sm text-muted">{{ text }}</p>
  </div>
</template>

<style scoped>
.text-muted {
  color: var(--color-muted, #8b8b9a);
}
</style>
