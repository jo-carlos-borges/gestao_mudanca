import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { User } from '@/models/User'
import router from '@/router'
import api from '@/services/api'

export const useAuthStore = defineStore('auth', () => {
  const user = ref<User | null>(null)

  const isAuthenticated = computed(() => !!user.value)

  const isAdmin = computed(() => {
    return user.value?.roles.includes('ROLE_ADMIN') || false
  })

  function setUser(userData: User) {
    user.value = userData
  }

  async function logout() {
    try {
      await api.post('/auth/logout')
    } catch (error) {
      console.error('Erro ao fazer logout', error)
    } finally {
      user.value = null
      router.push('/')
    }
  }

  return {
    user,
    isAuthenticated,
    isAdmin,
    setUser,
    logout,
  }
})
