<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { fetchSiteSettings, updateSiteSettings } from '@/api/site'
import type { SiteSetting } from '@/types'
import { useToast } from '@/composables/useToast'

const toast = useToast()
const settings = ref<SiteSetting[]>([])
const form = ref<Record<string, string>>({})
const loading = ref(true)
const saving = ref(false)

async function loadData() {
  loading.value = true
  try {
    settings.value = await fetchSiteSettings()
    form.value = Object.fromEntries(
      settings.value.map((s) => [s.settingKey, s.settingValue ?? '']),
    )
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(loadData)

async function handleSave() {
  saving.value = true
  try {
    await updateSiteSettings(form.value)
    toast.success('保存成功，前台可能需要清除 Redis 缓存后生效')
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '保存失败')
  } finally {
    saving.value = false
  }
}

function labelFor(setting: SiteSetting) {
  return setting.description || setting.settingKey
}
</script>

<template>
  <div>
    <div v-if="loading" class="text-sm text-gray-500">加载中...</div>

    <form v-else class="admin-card max-w-3xl space-y-5" @submit.prevent="handleSave">
      <div v-for="setting in settings" :key="setting.id">
        <label class="admin-label">{{ labelFor(setting) }}</label>
        <input
          v-if="(form[setting.settingKey]?.length ?? 0) < 80"
          v-model="form[setting.settingKey]"
          type="text"
          class="admin-input"
        />
        <textarea
          v-else
          v-model="form[setting.settingKey]"
          rows="3"
          class="admin-input resize-y"
        />
        <p class="mt-1 text-xs text-gray-600">{{ setting.settingKey }}</p>
      </div>

      <div class="flex gap-3 pt-2">
        <button type="submit" class="btn-primary" :disabled="saving">
          {{ saving ? '保存中...' : '保存设置' }}
        </button>
      </div>
    </form>
  </div>
</template>
