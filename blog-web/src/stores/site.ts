import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { fetchCommentConfig, fetchNav, fetchSettings, fetchSocial } from '@/api/site'
import { fetchCategories } from '@/api/category'
import { fetchTags } from '@/api/tag'
import type { Category, CommentConfig, NavItem, SocialLink, Tag } from '@/types'

export const useSiteStore = defineStore('site', () => {
  const settings = ref<Record<string, string>>({})
  const navItems = ref<NavItem[]>([])
  const socialLinks = ref<SocialLink[]>([])
  const categories = ref<Category[]>([])
  const tags = ref<Tag[]>([])
  const commentConfig = ref<CommentConfig | null>(null)
  const initialized = ref(false)
  const loading = ref(false)

  const siteTitle = computed(() => settings.value.site_title || 'lightdiary')
  const siteSubtitle = computed(() => settings.value.site_subtitle || '微光博客')
  const siteAuthor = computed(() => settings.value.site_author || 'lightdiary')
  const siteDescription = computed(
    () => settings.value.site_description || '一个分享知识与技术的微光博客',
  )
  const siteStartTime = computed(() => settings.value.site_start_time || '')
  const perPage = computed(() => Number(settings.value.per_page) || 10)
  const sidebarAnnouncement = computed(
    () => settings.value.sidebar_announcement || '',
  )
  const colorPrimary = computed(
    () => settings.value.color_primary || '#7509B6',
  )
  const colorSecondary = computed(
    () => settings.value.color_secondary || '#0153E5',
  )

  async function init() {
    if (initialized.value) return
    loading.value = true
    try {
      const [settingsData, nav, social, cats, tagList, comment] =
        await Promise.all([
          fetchSettings(),
          fetchNav(),
          fetchSocial(),
          fetchCategories(),
          fetchTags(),
          fetchCommentConfig().catch(() => null),
        ])
      settings.value = settingsData
      navItems.value = nav.sort((a, b) => (a.sortOrder ?? 0) - (b.sortOrder ?? 0))
      socialLinks.value = social.sort(
        (a, b) => (a.sortOrder ?? 0) - (b.sortOrder ?? 0),
      )
      categories.value = cats
      tags.value = tagList
      commentConfig.value = comment
      initialized.value = true

      document.documentElement.style.setProperty('--color-primary', colorPrimary.value)
      document.documentElement.style.setProperty(
        '--color-secondary',
        colorSecondary.value,
      )
    } finally {
      loading.value = false
    }
  }

  return {
    settings,
    navItems,
    socialLinks,
    categories,
    tags,
    commentConfig,
    initialized,
    loading,
    siteTitle,
    siteSubtitle,
    siteAuthor,
    siteDescription,
    siteStartTime,
    perPage,
    sidebarAnnouncement,
    colorPrimary,
    colorSecondary,
    init,
  }
})
