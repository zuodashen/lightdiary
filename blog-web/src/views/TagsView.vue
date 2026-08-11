<script setup lang="ts">
import { computed } from 'vue'
import { RouterLink } from 'vue-router'
import { useSiteStore } from '@/stores/site'
import EmptyState from '@/components/common/EmptyState.vue'
import type { Tag } from '@/types'

const siteStore = useSiteStore()
const tags = computed(() => siteStore.tags)

const maxCount = computed(() =>
  Math.max(...tags.value.map((t) => t.articleCount ?? 0), 1),
)

const palette = [
  ['#0153e5', '#7509b6'],
  ['#7509b6', '#c026d3'],
  ['#0891b2', '#0153e5'],
  ['#7c3aed', '#db2777'],
  ['#2563eb', '#9333ea'],
  ['#0d9488', '#6366f1'],
]

interface CloudTag extends Tag {
  fontSize: number
  rotate: number
  delay: number
  colors: [string, string]
}

const cloudTags = computed<CloudTag[]>(() => {
  const count = tags.value.length
  const sparseBoost = count <= 2 ? 1.35 : count <= 4 ? 1.15 : 1

  return tags.value.map((tag, index) => {
    const articleCount = tag.articleCount ?? 0
    const ratio = articleCount / maxCount.value
    const fontSize = (1.25 + ratio * 2.5 + (index % 3) * 0.06) * sparseBoost
    const rotate = ((tag.id * 17 + index * 7) % 13) - 6
    return {
      ...tag,
      fontSize,
      rotate,
      delay: (tag.id % 8) * 0.35,
      colors: palette[index % palette.length] as [string, string],
    }
  })
})

const totalArticles = computed(() =>
  tags.value.reduce((sum, t) => sum + (t.articleCount ?? 0), 0),
)
</script>

<template>
  <div class="container-blog pb-10 pt-2">
    <EmptyState
      v-if="!tags.length"
      title="暂无标签"
      description="还没有创建任何标签"
      icon="🏷️"
    />

    <section v-else class="tag-stage">
      <div class="tag-stage-glow" aria-hidden="true" />
      <div class="tag-stage-grid" aria-hidden="true" />

      <div v-if="tags.length" class="tag-stage-meta">
        <span>{{ tags.length }} 标签</span>
        <span class="tag-stage-dot" />
        <span>{{ totalArticles }} 篇</span>
      </div>

      <div class="tag-cloud-inner">
        <RouterLink
          v-for="tag in cloudTags"
          :key="tag.id"
          :to="`/tags/${tag.slug}`"
          class="tag-cloud-item"
          :style="{
            '--tag-size': `${tag.fontSize}rem`,
            '--tag-rotate': `${tag.rotate}deg`,
            '--tag-delay': `${tag.delay}s`,
            '--tag-c1': tag.colors[0],
            '--tag-c2': tag.colors[1],
          }"
        >
          <span class="tag-cloud-hash">#</span>
          <span class="tag-cloud-name">{{ tag.name }}</span>
          <span v-if="tag.articleCount !== undefined" class="tag-cloud-count">
            {{ tag.articleCount }}
          </span>
        </RouterLink>
      </div>
    </section>
  </div>
</template>

<style scoped>
.tag-stage {
  position: relative;
  overflow: hidden;
  min-height: min(58vh, 480px);
  border-radius: 1.25rem;
  border: 1px solid color-mix(in srgb, var(--color-primary) 12%, transparent);
  background: color-mix(in srgb, var(--color-surface-elevated) 88%, transparent);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 3rem 1.5rem 2.5rem;
}

.light .tag-stage {
  background: color-mix(in srgb, white 92%, transparent);
  border-color: color-mix(in srgb, var(--color-primary) 8%, transparent);
}

.tag-stage-glow {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(
      ellipse 55% 45% at 15% 25%,
      color-mix(in srgb, var(--color-secondary) 16%, transparent),
      transparent 68%
    ),
    radial-gradient(
      ellipse 50% 40% at 85% 75%,
      color-mix(in srgb, var(--color-primary) 14%, transparent),
      transparent 68%
    );
  pointer-events: none;
}

.tag-stage-grid {
  position: absolute;
  inset: 0;
  opacity: 0.35;
  background-image:
    linear-gradient(color-mix(in srgb, var(--color-primary) 6%, transparent) 1px, transparent 1px),
    linear-gradient(90deg, color-mix(in srgb, var(--color-primary) 6%, transparent) 1px, transparent 1px);
  background-size: 48px 48px;
  mask-image: radial-gradient(ellipse at center, black 20%, transparent 75%);
  pointer-events: none;
}

.tag-stage-meta {
  position: absolute;
  top: 1rem;
  right: 1.25rem;
  z-index: 2;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.35rem 0.75rem;
  border-radius: 9999px;
  font-size: 0.75rem;
  color: var(--color-muted, #8b8b9a);
  background: color-mix(in srgb, var(--color-primary) 8%, transparent);
  border: 1px solid color-mix(in srgb, var(--color-primary) 12%, transparent);
  backdrop-filter: blur(8px);
}

.tag-stage-dot {
  width: 3px;
  height: 3px;
  border-radius: 50%;
  background: currentColor;
  opacity: 0.5;
}

.tag-cloud-inner {
  position: relative;
  z-index: 1;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: center;
  gap: 0.85rem 1.5rem;
  max-width: 56rem;
  line-height: 1.15;
}

.tag-cloud-item {
  display: inline-flex;
  align-items: baseline;
  gap: 0.08em;
  padding: 0.2em 0.4em;
  font-size: var(--tag-size);
  font-weight: 800;
  letter-spacing: -0.03em;
  transform: rotate(var(--tag-rotate));
  transition:
    transform 0.35s cubic-bezier(0.34, 1.56, 0.64, 1),
    filter 0.25s ease,
    opacity 0.25s ease;
  animation: tag-float 5.5s ease-in-out infinite;
  animation-delay: var(--tag-delay);
  cursor: pointer;
  opacity: 0.92;
  text-shadow: 0 0 40px color-mix(in srgb, var(--tag-c1) 25%, transparent);
}

.tag-cloud-item:hover {
  transform: rotate(0deg) scale(1.1);
  opacity: 1;
  z-index: 3;
  filter: drop-shadow(0 0 22px color-mix(in srgb, var(--tag-c1) 50%, transparent));
}

.tag-cloud-hash {
  font-weight: 900;
  background: linear-gradient(135deg, var(--tag-c1), var(--tag-c2));
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  opacity: 0.9;
}

.tag-cloud-name {
  background: linear-gradient(135deg, var(--tag-c1), var(--tag-c2));
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.tag-cloud-count {
  margin-left: 0.2em;
  font-size: 0.4em;
  font-weight: 700;
  vertical-align: super;
  padding: 0.2em 0.5em;
  border-radius: 9999px;
  background: color-mix(in srgb, var(--tag-c1) 20%, transparent);
  color: var(--tag-c1);
  -webkit-background-clip: unset;
  background-clip: unset;
}

.light .tag-cloud-count {
  background: color-mix(in srgb, var(--tag-c1) 10%, white);
}

.tag-cloud-inner:hover .tag-cloud-item:not(:hover) {
  opacity: 0.38;
  filter: blur(0.4px);
}

@keyframes tag-float {
  0%,
  100% {
    translate: 0 0;
  }
  50% {
    translate: 0 -5px;
  }
}

@media (max-width: 640px) {
  .tag-stage {
    min-height: 320px;
    padding: 2.5rem 1rem 2rem;
  }

  .tag-stage-meta {
    top: 0.75rem;
    right: 0.75rem;
    font-size: 0.6875rem;
  }

  .tag-cloud-inner {
    gap: 0.65rem 1rem;
  }
}
</style>
