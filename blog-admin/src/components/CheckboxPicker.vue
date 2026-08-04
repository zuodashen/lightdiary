<script setup lang="ts">
import { computed, ref, watch } from 'vue'

export interface PickerOption {
  id: number
  label: string
  group?: string
}

const props = defineProps<{
  show: boolean
  title: string
  options: PickerOption[]
  selected: number[]
  loading?: boolean
}>()

const emit = defineEmits<{
  close: []
  confirm: [ids: number[]]
}>()

const checked = ref<number[]>([])

watch(
  () => props.show,
  (visible) => {
    if (visible) checked.value = [...props.selected]
  },
)

const grouped = computed(() => {
  const map = new Map<string, PickerOption[]>()
  for (const opt of props.options) {
    const key = opt.group || '默认'
    if (!map.has(key)) map.set(key, [])
    map.get(key)!.push(opt)
  }
  return map
})

function toggle(id: number) {
  if (checked.value.includes(id)) {
    checked.value = checked.value.filter((v) => v !== id)
  } else {
    checked.value = [...checked.value, id]
  }
}

function selectAll() {
  checked.value = props.options.map((o) => o.id)
}

function clearAll() {
  checked.value = []
}
</script>

<template>
  <Teleport to="body">
    <Transition name="modal">
      <div
        v-if="show"
        class="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4 backdrop-blur-sm"
        @click.self="emit('close')"
      >
        <div
          class="flex max-h-[85vh] w-full max-w-lg flex-col rounded-xl border border-[var(--color-border)] bg-[var(--color-surface-elevated)] shadow-2xl"
        >
          <div class="border-b border-[var(--color-border)] px-5 py-4">
            <h3 class="text-base font-semibold text-white">{{ title }}</h3>
            <div class="mt-2 flex gap-2">
              <button type="button" class="btn-ghost px-2 py-1 text-xs" @click="selectAll">全选</button>
              <button type="button" class="btn-ghost px-2 py-1 text-xs" @click="clearAll">清空</button>
              <span class="ml-auto text-xs text-gray-500">已选 {{ checked.length }} 项</span>
            </div>
          </div>
          <div class="flex-1 overflow-y-auto px-5 py-4">
            <div v-for="[group, items] in grouped" :key="group" class="mb-4">
              <div v-if="grouped.size > 1" class="mb-2 text-xs font-medium text-gray-500">{{ group }}</div>
              <label
                v-for="opt in items"
                :key="opt.id"
                class="mb-1 flex cursor-pointer items-center gap-2 rounded-lg px-2 py-1.5 hover:bg-white/[0.03]"
              >
                <input
                  type="checkbox"
                  class="accent-primary"
                  :checked="checked.includes(opt.id)"
                  @change="toggle(opt.id)"
                />
                <span class="text-sm text-gray-200">{{ opt.label }}</span>
              </label>
            </div>
            <p v-if="options.length === 0" class="text-sm text-gray-500">暂无可选项</p>
          </div>
          <div class="flex justify-end gap-3 border-t border-[var(--color-border)] px-5 py-4">
            <button type="button" class="btn-secondary" :disabled="loading" @click="emit('close')">
              取消
            </button>
            <button
              type="button"
              class="btn-primary"
              :disabled="loading"
              @click="emit('confirm', checked)"
            >
              {{ loading ? '保存中...' : '确定' }}
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.2s ease;
}
.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}
</style>
