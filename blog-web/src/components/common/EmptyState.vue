<script setup lang="ts">
defineProps<{
  title?: string
  description?: string
  icon?: string
}>()
</script>

<template>
  <div class="flex flex-col items-center justify-center py-20 text-center">
    <div
      class="mb-4 flex h-16 w-16 items-center justify-center rounded-2xl bg-primary/10 text-3xl"
    >
      {{ icon || '📭' }}
    </div>
    <h3 class="mb-2 text-lg font-medium dark:text-gray-200 light:text-gray-800">
      {{ title || '暂无内容' }}
    </h3>
    <p v-if="description" class="max-w-sm text-sm text-muted">
      {{ description }}
    </p>
    <slot />
  </div>
</template>

<style scoped>
.text-muted {
  color: var(--color-muted, #8b8b9a);
}
</style>
