<script setup lang="ts">
import { computed } from 'vue'
import { RouterView, useRoute } from 'vue-router'
import AppHeader from '@/components/layout/AppHeader.vue'
import AppFooter from '@/components/layout/AppFooter.vue'
import Loading from '@/components/common/Loading.vue'
import { useSiteStore } from '@/stores/site'

const siteStore = useSiteStore()
const route = useRoute()
const showLoader = computed(() => siteStore.loading && !siteStore.initialized)
const isHome = computed(() => route.path === '/')
</script>

<template>
  <div class="flex min-h-screen flex-col">
    <div class="ambient-bg" aria-hidden="true" />
    <AppHeader />
    <main class="flex-1" :class="isHome ? 'pb-8' : 'py-8'">
      <Loading v-if="showLoader" fullscreen text="加载中..." />
      <RouterView v-slot="{ Component, route }">
        <Transition name="page" mode="out-in">
          <component :is="Component" :key="route.path" />
        </Transition>
      </RouterView>
    </main>
    <AppFooter />
  </div>
</template>
