<script setup lang="ts">
import { ref } from 'vue'
import { updatePassword } from '@/api/admin'
import { useAuthStore } from '@/stores/auth'
import { useToast } from '@/composables/useToast'

const auth = useAuthStore()
const toast = useToast()
const saving = ref(false)

const form = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})

async function handleSubmit() {
  if (!form.value.oldPassword || !form.value.newPassword) {
    toast.error('请填写完整')
    return
  }
  if (form.value.newPassword !== form.value.confirmPassword) {
    toast.error('两次新密码不一致')
    return
  }
  if (!auth.username) {
    toast.error('未获取到当前用户')
    return
  }
  saving.value = true
  try {
    await updatePassword({
      username: auth.username,
      oldPassword: form.value.oldPassword,
      newPassword: form.value.newPassword,
    })
    toast.success('密码修改成功，请重新登录')
    form.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
  } catch (e) {
    toast.error(e instanceof Error ? e.message : '修改失败')
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <div class="max-w-lg space-y-6">
    <div class="admin-card">
      <h3 class="mb-4 font-medium text-white">账户信息</h3>
      <dl class="space-y-2 text-sm">
        <div class="flex gap-4">
          <dt class="w-20 text-gray-500">用户名</dt>
          <dd class="text-gray-200">{{ auth.username || '-' }}</dd>
        </div>
        <div class="flex gap-4">
          <dt class="w-20 text-gray-500">角色</dt>
          <dd class="text-gray-200">{{ auth.roles?.join('、') || '-' }}</dd>
        </div>
      </dl>
    </div>

    <form class="admin-card space-y-4" @submit.prevent="handleSubmit">
      <h3 class="font-medium text-white">修改密码</h3>
      <div>
        <label class="admin-label">当前密码</label>
        <input v-model="form.oldPassword" type="password" class="admin-input" autocomplete="current-password" />
      </div>
      <div>
        <label class="admin-label">新密码</label>
        <input v-model="form.newPassword" type="password" class="admin-input" autocomplete="new-password" />
      </div>
      <div>
        <label class="admin-label">确认新密码</label>
        <input v-model="form.confirmPassword" type="password" class="admin-input" autocomplete="new-password" />
      </div>
      <button type="submit" class="btn-primary" :disabled="saving">
        {{ saving ? '保存中...' : '修改密码' }}
      </button>
    </form>
  </div>
</template>
