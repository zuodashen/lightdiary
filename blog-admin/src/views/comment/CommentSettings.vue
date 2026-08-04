<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { fetchCommentConfig, updateCommentConfig } from '@/api/comment'
import type { CommentConfig } from '@/types'
import { useToast } from '@/composables/useToast'

const toast = useToast()
const loading = ref(true)
const saving = ref(false)

const form = ref<CommentConfig>({
  system: 'giscus',
  configJson: '{}',
  enabled: 1,
})

onMounted(async () => {
  try {
    const config = await fetchCommentConfig()
    if (config) {
      form.value = {
        id: config.id,
        system: config.system || 'giscus',
        configJson: config.configJson || '{}',
        enabled: config.enabled ?? 1,
      }
    }
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '加载失败')
  } finally {
    loading.value = false
  }
})

async function handleSave() {
  try {
    JSON.parse(form.value.configJson)
  } catch {
    toast.error('configJson 不是有效的 JSON')
    return
  }
  saving.value = true
  try {
    await updateCommentConfig(form.value)
    toast.success('保存成功')
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
      <router-link to="/comments" class="text-sm text-gray-400 hover:text-primary">
        ← 返回评论列表
      </router-link>
    </div>

    <div v-if="loading" class="text-sm text-gray-500">加载中...</div>

    <form v-else class="admin-card max-w-2xl space-y-5" @submit.prevent="handleSave">
      <div>
        <label class="admin-label">评论系统</label>
        <select v-model="form.system" class="admin-input">
          <option value="giscus">Giscus</option>
          <option value="disabled">禁用</option>
        </select>
      </div>

      <label class="flex items-center gap-2 text-sm text-gray-300">
        <input v-model="form.enabled" type="checkbox" :true-value="1" :false-value="0" class="accent-primary" />
        启用评论
      </label>

      <div>
        <label class="admin-label">配置 JSON</label>
        <textarea
          v-model="form.configJson"
          rows="12"
          class="admin-input resize-y font-mono text-sm"
          placeholder='{"repo":"owner/repo","repoId":"...","category":"...","categoryId":"..."}'
        />
        <p class="mt-1 text-xs text-gray-500">Giscus 配置项，需为合法 JSON 格式</p>
      </div>

      <button type="submit" class="btn-primary" :disabled="saving">
        {{ saving ? '保存中...' : '保存配置' }}
      </button>
    </form>
  </div>
</template>
