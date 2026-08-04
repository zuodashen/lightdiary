<script setup lang="ts">
import { RouterLink } from 'vue-router'
</script>

<template>
  <div class="container-blog flex min-h-[50vh] flex-col items-center justify-center text-center">
    <div class="mb-6 text-7xl font-bold text-primary/30">404</div>
    <h1 class="mb-2 text-2xl font-bold dark:text-white light:text-gray-900">
      页面未找到
    </h1>
    <p class="mb-8 text-muted">您访问的页面不存在或已被移除</p>
    <RouterLink to="/" class="btn-primary">返回首页</RouterLink>
  </div>
</template>

<style scoped>
.text-muted {
  color: var(--color-muted, #8b8b9a);
}
</style>
