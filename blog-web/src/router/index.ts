import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  scrollBehavior(_to, _from, savedPosition) {
    if (savedPosition) return savedPosition
    return { top: 0 }
  },
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomeView.vue'),
      meta: { title: '首页' },
    },
    {
      path: '/post/:slug',
      name: 'post',
      component: () => import('@/views/PostView.vue'),
      meta: { title: '文章' },
    },
    {
      path: '/archives',
      name: 'archives',
      component: () => import('@/views/ArchivesView.vue'),
      meta: { title: '归档' },
    },
    {
      path: '/categories',
      name: 'categories',
      component: () => import('@/views/CategoriesView.vue'),
      meta: { title: '分类' },
    },
    {
      path: '/categories/:slug',
      name: 'category-detail',
      component: () => import('@/views/CategoryDetailView.vue'),
      meta: { title: '分类' },
    },
    {
      path: '/tags',
      name: 'tags',
      component: () => import('@/views/TagsView.vue'),
      meta: { title: '标签' },
    },
    {
      path: '/tags/:slug',
      name: 'tag-detail',
      component: () => import('@/views/TagDetailView.vue'),
      meta: { title: '标签' },
    },
    {
      path: '/bookmarks',
      name: 'bookmarks',
      component: () => import('@/views/BookmarksView.vue'),
      meta: { title: '书签' },
    },
    {
      path: '/lab',
      name: 'lab',
      component: () => import('@/views/LabView.vue'),
      meta: { title: '实验室' },
    },
    {
      path: '/guestbook',
      name: 'guestbook',
      component: () => import('@/views/GuestbookView.vue'),
      meta: { title: '留言板' },
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('@/views/AboutView.vue'),
      meta: { title: '关于' },
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('@/views/NotFoundView.vue'),
      meta: { title: '404' },
    },
  ],
})

router.afterEach((to) => {
  const title = to.meta.title as string | undefined
  if (title) {
    document.title = `${title} · lightdiary`
  }
})

export default router
