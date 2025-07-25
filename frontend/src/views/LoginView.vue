<script setup lang="ts">
import { ref } from 'vue'
import { computed } from 'vue'
import api from '@/services/api'
import { useTheme } from 'vuetify'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useToast } from '@/composables/useToast'
import { parseError } from '@/utils/errorHandler'

const email = ref('')
const password = ref('')
const theme = useTheme()
const loading = ref(false)
const router = useRouter()
const auth = useAuthStore()
const { showToast } = useToast()
const isDark = computed(() => theme.global.name.value === 'dark')

const toggleTheme = () => {
  const newTheme = isDark.value ? 'light' : 'dark'
  theme.global.name.value = newTheme
  localStorage.setItem('user-theme', newTheme)
}

async function handleLogin() {
  try {
    if (loading.value) return

    loading.value = true

    const response = await api.post('/auth/login', {
      email: email.value,
      password: password.value,
    })

    const user = response.data

    auth.setUser(user)
    router.push('/dashboard')
  } catch (error) {
    showToast(parseError(error), 'error')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <v-btn
    icon
    class="position-absolute"
    style="top: 20px; right: 20px"
    @click="toggleTheme"
    variant="tonal"
    :color="isDark ? 'yellow' : 'blue'"
  >
    <v-icon>{{ isDark ? 'mdi-white-balance-sunny' : 'mdi-weather-night' }}</v-icon>
  </v-btn>

  <v-container fluid class="d-flex align-center justify-center fill-height">
    <v-card
      width="420"
      class="pa-8 fade-in"
      elevation="16"
      rounded="xl"
      style="backdrop-filter: blur(10px)"
    >
      <v-card-item class="justify-center">
        <v-avatar
          size="72"
          class="d-flex align-center justify-center"
          style="border: 6px solid #2196f3; background-color: white"
        >
          <v-icon size="40" color="primary">mdi-account</v-icon>
        </v-avatar>
      </v-card-item>

      <v-card-title class="text-center text-h5 mb-2">Olá, seja bem-vindo </v-card-title>

      <v-card-subtitle class="text-center mb-6">
        Entre para continuar ao painel de controle
      </v-card-subtitle>

      <v-form ref="form" @submit.prevent="handleLogin">
        <v-text-field
          label="Email"
          placeholder="exemplo@email.com"
          prepend-inner-icon="mdi-email-outline"
          type="email"
          variant="outlined"
          density="comfortable"
          class="mb-4"
          v-model="email"
        ></v-text-field>

        <v-text-field
          label="Senha"
          placeholder="••••••••"
          prepend-inner-icon="mdi-lock-outline"
          type="password"
          variant="outlined"
          density="comfortable"
          class="mb-6"
          v-model="password"
        ></v-text-field>

        <v-btn
          type="submit"
          color="primary"
          size="large"
          block
          elevation="4"
          class="mb-4"
          style="transition: background-color 0.3s"
          :loading="loading"
          :disabled="loading"
        >
          Entrar
        </v-btn>

        <!-- <div class="d-flex justify-space-between">
          <v-btn variant="text" size="small">Esqueci a senha</v-btn>
          <v-btn variant="text" size="small">Criar conta</v-btn>
        </div> -->
      </v-form>
    </v-card>
  </v-container>
</template>

<style scoped>
.fade-in {
  opacity: 0;
  transform: translateY(20px);
  animation: fadeInUp 1.8s ease forwards;
}

@keyframes fadeInUp {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
