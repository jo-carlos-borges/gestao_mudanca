<script setup lang="ts">
import { ref } from 'vue'
import { useTheme } from 'vuetify'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const theme = useTheme()
const drawer = ref(true)

const items = [
  { title: 'Dashboard', icon: 'mdi-monitor-dashboard', to: '/dashboard' },
  { title: 'Tarefas', icon: 'mdi-format-list-checks', to: '/tarefas' },
  ...(auth.isAdmin ? [{ title: 'Usuários', icon: 'mdi-account-multiple', to: '/usuarios' }] : []),
]
</script>

<template>
  <v-app>
    <v-app-bar app color="success" dark flat elevate-on-scroll class="px-4">
      <v-app-bar-nav-icon @click="drawer = !drawer" />
      <v-toolbar-title class="text-h5 font-weight-bold">Base</v-toolbar-title>
      <v-spacer />
      <v-btn
        icon
        @click="theme.global.name.value = theme.global.name.value === 'dark' ? 'light' : 'dark'"
      >
        <v-icon>mdi-theme-light-dark</v-icon>
      </v-btn>

      <v-tooltip text="Sair" location="bottom">
        <template #activator="{ props }">
          <v-btn icon v-bind="props" @click="auth.logout()">
            <v-icon>mdi-alien-outline</v-icon>
          </v-btn>
        </template>
      </v-tooltip>
    </v-app-bar>

    <v-navigation-drawer v-model="drawer" app color="surface" width="260">
      <div class="d-flex align-center px-4 py-3">
        <v-avatar size="48" class="mr-3">
          <v-img src="https://i.pravatar.cc/150?img=3" />
        </v-avatar>
        <div>
          <div class="font-weight-medium">{{ auth.user?.name }}</div>
          <div class="text-grey text-caption">{{ auth.isAdmin ? 'Administrador' : 'Usuário' }}</div>
        </div>
      </div>

      <v-divider class="my-2" />

      <v-list nav>
        <v-list-subheader class="text-grey text-uppercase"> Navegação </v-list-subheader>
        <v-list-item
          v-for="item in items"
          :key="item.title"
          :to="item.to"
          link
          rounded="lg"
          class="menu-item"
        >
          <template v-slot:prepend>
            <v-icon size="28" class="me-3">
              {{ item.icon }}
            </v-icon>
            <v-list-item-title class="font-weight-medium text-body-1">
              {{ item.title }}
            </v-list-item-title>
          </template>
        </v-list-item>
      </v-list>
    </v-navigation-drawer>

    <v-main class="bg-grey-lighten-4 bg-background">
      <v-container fluid class="pa-4">
        <router-view />
      </v-container>
    </v-main>

    <v-footer app height="40" color="surface" class="justify-center text-caption text-grey">
      © {{ new Date().getFullYear() }} — Base
    </v-footer>
  </v-app>
</template>

<style scoped>
.v-list-item--active .v-icon {
  color: rgb(var(--v-theme-success)) !important;
}

.v-list-item--active .v-list-item-title {
  color: rgb(var(--v-theme-success)) !important;
}
</style>
