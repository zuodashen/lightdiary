import { nextTick, onBeforeUnmount, watch, type Ref } from 'vue'
import { useThemeStore } from '@/stores/theme'

let mermaidReady: Promise<typeof import('mermaid').default> | null = null
let renderSeq = 0

async function loadMermaid() {
  if (!mermaidReady) {
    mermaidReady = import('mermaid').then((m) => m.default)
  }
  return mermaidReady
}

function decodeHtml(text: string) {
  const el = document.createElement('textarea')
  el.innerHTML = text
  return el.value
}

/**
 * 将文章 HTML 中的 ```mermaid 代码块渲染为流程图。
 * 后端 commonmark 会输出 <pre><code class="language-mermaid">...</code></pre>
 *
 * @param contentKey 内容变化时触发重新渲染（如 article.contentHtml）
 */
export function useMermaid(
  rootRef: Ref<HTMLElement | null>,
  enabled: Ref<boolean>,
  contentKey?: Ref<unknown>,
) {
  const themeStore = useThemeStore()
  let cancelled = false

  async function renderMermaid() {
    await nextTick()
    const root = rootRef.value
    if (!root || !enabled.value || cancelled) return

    const blocks = root.querySelectorAll('pre > code.language-mermaid')
    if (!blocks.length) return

    const mermaid = await loadMermaid()
    if (cancelled) return

    mermaid.initialize({
      startOnLoad: false,
      securityLevel: 'strict',
      theme: themeStore.isDark ? 'dark' : 'default',
      fontFamily: 'inherit',
    })

    for (const codeEl of Array.from(blocks)) {
      if (cancelled) return
      const pre = codeEl.parentElement
      if (!pre || pre.dataset.mermaidRendered === '1') continue

      const source = decodeHtml(codeEl.textContent || '').trim()
      if (!source) continue

      const id = `mermaid-${Date.now()}-${++renderSeq}`
      try {
        const { svg } = await mermaid.render(id, source)
        if (cancelled) return

        const wrap = document.createElement('div')
        wrap.className = 'mermaid-diagram'
        wrap.innerHTML = svg
        pre.replaceWith(wrap)
      } catch (e) {
        console.warn('Mermaid render failed:', e)
        pre.dataset.mermaidRendered = 'error'
      }
    }
  }

  const deps = contentKey
    ? [rootRef, enabled, contentKey, () => themeStore.mode]
    : [rootRef, enabled, () => themeStore.mode]

  watch(deps, () => {
    void renderMermaid()
  }, { flush: 'post' })

  onBeforeUnmount(() => {
    cancelled = true
  })

  return { renderMermaid }
}
