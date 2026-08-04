<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { fetchArticles } from '@/api/article'
import { fetchCategories } from '@/api/category'
import { fetchTags } from '@/api/tag'
import { fetchBookmarks } from '@/api/bookmark'
import { fetchPages } from '@/api/page'
import { fetchComments } from '@/api/comment'
import { fetchAdmins } from '@/api/admin'
import { fetchAllRoles } from '@/api/role'

const stats = ref([
  { label: '文章', value: '-', icon: '📝', to: '/articles' },
  { label: '分类', value: '-', icon: '📁', to: '/categories' },
  { label: '标签', value: '-', icon: '🏷️', to: '/tags' },
  { label: '书签', value: '-', icon: '🔖', to: '/bookmarks' },
  { label: '页面', value: '-', icon: '📄', to: '/pages' },
  { label: '待审评论', value: '-', icon: '💬', to: '/comments' },
  { label: '管理员', value: '-', icon: '👤', to: '/system/admins' },
  { label: '角色', value: '-', icon: '🛡️', to: '/system/roles' },
])
const loading = ref(true)

onMounted(async () => {
  try {
    const [articles, categories, tags, bookmarks, pages, pendingComments, admins, roles] =
      await Promise.all([
        fetchArticles({ pageNum: 1, pageSize: 1 }),
        fetchCategories(),
        fetchTags(),
        fetchBookmarks(),
        fetchPages(),
        fetchComments({ pageNum: 1, pageSize: 1, status: 'PENDING' }),
        fetchAdmins({ pageNum: 1, pageSize: 1 }),
        fetchAllRoles(),
      ])
    stats.value = [
      { label: '文章', value: String(articles.total), icon: '📝', to: '/articles' },
      { label: '分类', value: String(categories.length), icon: '📁', to: '/categories' },
      { label: '标签', value: String(tags.length), icon: '🏷️', to: '/tags' },
      { label: '书签', value: String(bookmarks.length), icon: '🔖', to: '/bookmarks' },
      { label: '页面', value: String(pages.length), icon: '📄', to: '/pages' },
      { label: '待审评论', value: String(pendingComments.total), icon: '💬', to: '/comments' },
      { label: '管理员', value: String(admins.total), icon: '👤', to: '/system/admins' },
      { label: '角色', value: String(roles.length), icon: '🛡️', to: '/system/roles' },
    ]
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div>
    <p class="mb-6 text-sm text-gray-400">欢迎使用微光博客管理后台</p>

    <div v-if="loading" class="text-sm text-gray-500">加载统计数据...</div>

    <div v-else class="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
      <router-link
        v-for="item in stats"
        :key="item.label"
        :to="item.to"
        class="admin-card flex items-center gap-4 transition hover:border-primary/30"
      >
        <div class="text-2xl">{{ item.icon }}</div>
        <div>
          <div class="text-2xl font-bold text-white">{{ item.value }}</div>
          <div class="text-sm text-gray-500">{{ item.label }}</div>
        </div>
      </router-link>
    </div>

    <div class="mt-8 grid gap-4 lg:grid-cols-2">
      <div class="admin-card">
        <h3 class="mb-3 font-medium text-white">快速操作</h3>
        <div class="flex flex-wrap gap-3">
          <router-link to="/articles/new" class="btn-primary">新建文章</router-link>
          <router-link to="/comments" class="btn-secondary">审核评论</router-link>
          <router-link to="/settings/site" class="btn-secondary">站点设置</router-link>
          <router-link to="/system/admins" class="btn-secondary">用户管理</router-link>
        </div>
      </div>
      <div class="admin-card">
        <h3 class="mb-3 font-medium text-white">提示</h3>
        <ul class="space-y-2 text-sm text-gray-400">
          <li>· 文章保存为草稿后，可在列表中点击「发布」</li>
          <li>· 修改站点设置后，前台可能需要清除 Redis 缓存才能立即生效</li>
          <li>· 角色需分配菜单和资源后，对应权限才会生效</li>
        </ul>
      </div>
    </div>
  </div>
</template>
