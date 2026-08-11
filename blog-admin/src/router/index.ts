import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory('/lightdiary-admin/'),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: { guest: true },
    },
    {
      path: '/',
      component: () => import('@/layouts/AdminLayout.vue'),
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          redirect: '/dashboard',
        },
        {
          path: 'dashboard',
          name: 'dashboard',
          component: () => import('@/views/DashboardView.vue'),
          meta: { title: '仪表盘' },
        },
        {
          path: 'articles',
          name: 'articles',
          component: () => import('@/views/article/ArticleList.vue'),
          meta: { title: '文章管理' },
        },
        {
          path: 'articles/new',
          name: 'article-create',
          component: () => import('@/views/article/ArticleEdit.vue'),
          meta: { title: '新建文章' },
        },
        {
          path: 'articles/:id/edit',
          name: 'article-edit',
          component: () => import('@/views/article/ArticleEdit.vue'),
          meta: { title: '编辑文章' },
        },
        {
          path: 'categories',
          name: 'categories',
          component: () => import('@/views/category/CategoryList.vue'),
          meta: { title: '分类管理' },
        },
        {
          path: 'tags',
          name: 'tags',
          component: () => import('@/views/tag/TagList.vue'),
          meta: { title: '标签管理' },
        },
        {
          path: 'bookmarks',
          name: 'bookmarks',
          component: () => import('@/views/bookmark/BookmarkList.vue'),
          meta: { title: '书签管理' },
        },
        {
          path: 'innovations',
          name: 'innovations',
          component: () => import('@/views/innovation/InnovationList.vue'),
          meta: { title: '实验室管理' },
        },
        {
          path: 'pages',
          name: 'pages',
          component: () => import('@/views/page/PageList.vue'),
          meta: { title: '页面管理' },
        },
        {
          path: 'pages/new',
          name: 'page-create',
          component: () => import('@/views/page/PageEdit.vue'),
          meta: { title: '新建页面' },
        },
        {
          path: 'pages/:id/edit',
          name: 'page-edit',
          component: () => import('@/views/page/PageEdit.vue'),
          meta: { title: '编辑页面' },
        },
        {
          path: 'settings/site',
          name: 'settings-site',
          component: () => import('@/views/settings/SiteSettings.vue'),
          meta: { title: '站点设置' },
        },
        {
          path: 'settings/nav',
          name: 'settings-nav',
          component: () => import('@/views/settings/NavItems.vue'),
          meta: { title: '导航管理' },
        },
        {
          path: 'settings/social',
          name: 'settings-social',
          component: () => import('@/views/settings/SocialLinks.vue'),
          meta: { title: '社交链接' },
        },
        {
          path: 'comments',
          name: 'comments',
          component: () => import('@/views/comment/CommentList.vue'),
          meta: { title: '评论管理' },
        },
        {
          path: 'guestbook',
          name: 'guestbook',
          component: () => import('@/views/guestbook/GuestbookList.vue'),
          meta: { title: '留言板管理' },
        },
        {
          path: 'comments/settings',
          name: 'comments-settings',
          component: () => import('@/views/comment/CommentSettings.vue'),
          meta: { title: '评论配置' },
        },
        {
          path: 'system/admins',
          name: 'system-admins',
          component: () => import('@/views/system/AdminList.vue'),
          meta: { title: '用户管理' },
        },
        {
          path: 'system/roles',
          name: 'system-roles',
          component: () => import('@/views/system/RoleList.vue'),
          meta: { title: '角色管理' },
        },
        {
          path: 'system/menus',
          name: 'system-menus',
          component: () => import('@/views/system/MenuList.vue'),
          meta: { title: '菜单管理' },
        },
        {
          path: 'system/resources',
          name: 'system-resources',
          component: () => import('@/views/system/ResourceList.vue'),
          meta: { title: '资源管理' },
        },
        {
          path: 'account/profile',
          name: 'account-profile',
          component: () => import('@/views/account/ProfileView.vue'),
          meta: { title: '账户设置' },
        },
      ],
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/dashboard',
    },
  ],
})

router.beforeEach(async (to) => {
  const auth = useAuthStore()

  if (!auth.initialized) {
    await auth.fetchUserInfo()
  }

  if (to.meta.requiresAuth && !auth.isLoggedIn) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }

  if (to.meta.guest && auth.isLoggedIn) {
    return { path: '/dashboard' }
  }

  return true
})

export default router
