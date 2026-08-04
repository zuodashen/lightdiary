import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import { useSiteStore } from './stores/site'
import { useThemeStore } from './stores/theme'
import './assets/main.css'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)

async function bootstrap() {
  const siteStore = useSiteStore()
  const themeStore = useThemeStore()

  await siteStore.init()
  themeStore.init(siteStore.settings.default_theme_mode)

  if (siteStore.siteTitle) {
    document.title = siteStore.siteTitle
  }

  app.mount('#app')
}

bootstrap()
