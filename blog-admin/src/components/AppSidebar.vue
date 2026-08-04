<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

interface NavItem {
  label: string
  to: string
  icon: string
}

interface NavGroup {
  title: string
  items: NavItem[]
}

const route = useRoute()

const groups: NavGroup[] = [
  {
    title: '概览',
    items: [{ label: '仪表盘', to: '/dashboard', icon: '📊' }],
  },
  {
    title: '内容',
    items: [
      { label: '文章', to: '/articles', icon: '📝' },
      { label: '分类', to: '/categories', icon: '📁' },
      { label: '标签', to: '/tags', icon: '🏷️' },
      { label: '书签', to: '/bookmarks', icon: '🔖' },
      { label: '页面', to: '/pages', icon: '📄' },
      { label: '评论', to: '/comments', icon: '💬' },
    ],
  },
  {
    title: '站点',
    items: [
      { label: '站点设置', to: '/settings/site', icon: '⚙️' },
      { label: '导航', to: '/settings/nav', icon: '🧭' },
      { label: '社交链接', to: '/settings/social', icon: '🔗' },
    ],
  },
  {
    title: '系统',
    items: [
      { label: '用户管理', to: '/system/admins', icon: '👤' },
      { label: '角色管理', to: '/system/roles', icon: '🛡️' },
      { label: '菜单管理', to: '/system/menus', icon: '📋' },
      { label: '资源管理', to: '/system/resources', icon: '🔐' },
    ],
  },
]

function isActive(path: string) {
  if (path === '/articles') return route.path.startsWith('/articles')
  if (path === '/pages') return route.path.startsWith('/pages')
  if (path === '/comments') return route.path.startsWith('/comments')
  if (path === '/system/admins') return route.path.startsWith('/system/admins')
  if (path === '/system/roles') return route.path.startsWith('/system/roles')
  if (path === '/system/menus') return route.path.startsWith('/system/menus')
  if (path === '/system/resources') return route.path.startsWith('/system/resources')
  return route.path === path || route.path.startsWith(path + '/')
}

const portalUrl = computed(() => 'http://localhost:5173')
</script>

<template>
  <aside
    class="sticky top-0 flex h-screen w-60 shrink-0 flex-col border-r border-[var(--color-border)] bg-[var(--color-surface)]"
  >
    <div class="flex items-center gap-3 border-b border-[var(--color-border)] px-5 py-5">
      <div
        class="flex h-9 w-9 items-center justify-center rounded-lg bg-primary text-sm font-bold text-white"
      >
        微
      </div>
      <div>
        <div class="text-sm font-semibold text-white">微光博客</div>
        <div class="text-xs text-gray-500">管理后台</div>
      </div>
    </div>

    <nav class="flex-1 overflow-y-auto px-3 py-4">
      <div v-for="group in groups" :key="group.title" class="mb-5">
        <div class="mb-2 px-3 text-xs font-medium uppercase tracking-wider text-gray-500">
          {{ group.title }}
        </div>
        <router-link
          v-for="item in group.items"
          :key="item.to"
          :to="item.to"
          class="mb-1 flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm transition"
          :class="
            isActive(item.to)
              ? 'bg-primary/15 font-medium text-primary'
              : 'text-gray-400 hover:bg-[var(--color-surface-hover)] hover:text-gray-200'
          "
        >
          <span>{{ item.icon }}</span>
          <span>{{ item.label }}</span>
        </router-link>
      </div>
    </nav>

    <div class="space-y-1 border-t border-[var(--color-border)] p-4">
      <router-link
        to="/account/profile"
        class="flex items-center gap-2 rounded-lg px-3 py-2 text-sm text-gray-400 transition hover:bg-[var(--color-surface-hover)] hover:text-gray-200"
      >
        <span>⚙️</span>
        <span>账户设置</span>
      </router-link>
      <a
        :href="portalUrl"
        target="_blank"
        rel="noopener noreferrer"
        class="flex items-center gap-2 rounded-lg px-3 py-2 text-sm text-gray-400 transition hover:bg-[var(--color-surface-hover)] hover:text-gray-200"
      >
        <span>🌐</span>
        <span>查看前台站点</span>
      </a>
    </div>
  </aside>
</template>
