<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { createPage, fetchPages, updatePage } from '@/api/page'
import type { PageParam } from '@/types'
import { useToast } from '@/composables/useToast'

const route = useRoute()
const router = useRouter()
const toast = useToast()

const isEdit = computed(() => !!route.params.id)
const pageId = computed(() => Number(route.params.id))
const loading = ref(false)
const saving = ref(false)

const form = ref<PageParam>({
  title: '',
  slug: '',
  content: '',
  template: 'default',
  status: 'PUBLISHED',
  sortOrder: 0,
})

onMounted(async () => {
  if (!isEdit.value || !pageId.value) return
  loading.value = true
  try {
    const pages = await fetchPages()
    const page = pages.find((p) => p.id === pageId.value)
    if (page) {
      form.value = {
        title: page.title,
        slug: page.slug,
        content: page.content || '',
        template: page.template || 'default',
        status: page.status || 'PUBLISHED',
        sortOrder: page.sortOrder ?? 0,
      }
    }
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '加载失败')
  } finally {
    loading.value = false
  }
})

async function handleSubmit() {
  if (!form.value.title.trim()) {
    toast.error('请填写标题')
    return
  }
  saving.value = true
  try {
    if (isEdit.value && pageId.value) {
      await updatePage(pageId.value, form.value)
      toast.success('更新成功')
    } else {
      await createPage(form.value)
      toast.success('创建成功')
    }
    router.push('/pages')
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '保存失败')
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <div>
    <div class="mb-5">
      <router-link to="/pages" class="text-sm text-gray-400 hover:text-primary">← 返回页面列表</router-link>
    </div>

    <div v-if="loading" class="text-sm text-gray-500">加载中...</div>

    <form v-else class="space-y-5" @submit.prevent="handleSubmit">
      <div class="admin-card space-y-4">
        <div>
          <label class="admin-label">标题 *</label>
          <input v-model="form.title" type="text" class="admin-input" />
        </div>
        <div class="grid gap-4 md:grid-cols-3">
          <div>
            <label class="admin-label">Slug</label>
            <input v-model="form.slug" type="text" class="admin-input" placeholder="about" />
          </div>
          <div>
            <label class="admin-label">模板</label>
            <input v-model="form.template" type="text" class="admin-input" />
          </div>
          <div>
            <label class="admin-label">状态</label>
            <select v-model="form.status" class="admin-input">
              <option value="DRAFT">草稿</option>
              <option value="PUBLISHED">已发布</option>
            </select>
          </div>
        </div>
      </div>

      <div class="admin-card">
        <label class="admin-label">Markdown 内容</label>
        <textarea
          v-model="form.content"
          rows="16"
          class="admin-input resize-y font-mono text-sm leading-relaxed"
          placeholder="# 关于我"
        />
      </div>

      <div class="flex gap-3">
        <button type="submit" class="btn-primary" :disabled="saving">
          {{ saving ? '保存中...' : '保存' }}
        </button>
        <router-link to="/pages" class="btn-ghost">取消</router-link>
      </div>
    </form>
  </div>
</template>
