import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import api from '@/services/api'
import { useAuthStore } from '@/stores/auth'

import '@mdi/font/css/materialdesignicons.css'
import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'

const savedTheme = localStorage.getItem('user-theme') || 'light'

const vuetify = createVuetify({
  components,
  directives,
  icons: {
    defaultSet: 'mdi',
  },
  theme: {
    defaultTheme: savedTheme,
  },
})

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(vuetify)

const auth = useAuthStore()

await api
  .get('/auth/validate')
  .then((response) => {
    auth.setUser(response.data)
    router.push('/dashboard')
  })
  .catch(() => {
    router.push('/')
  })

app.mount('#app')
