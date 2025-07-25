<script setup lang="ts">
import { useUser } from '@/composables/useUser'
import { formatDate } from '@/utils/date'
import UsuarioForm from '@/components/UserForm.vue'

const {
  usuarios,
  loading,
  pagination,
  headers,
  saveLoading,
  showModal,
  editedUser,
  deleteUsuario,
  handleSaveUsuario,
  openEditModal,
} = useUser()
</script>

<template>
  <v-card class="pa-4">
    <v-card-title class="text-h5 d-flex justify-space-between">
      Usuários
      <div>
        <UsuarioForm
          v-model:isOpen="showModal"
          :loading="saveLoading"
          :user="editedUser"
          @saved="handleSaveUsuario"
        />
        <v-btn
          color="primary"
          prepend-icon="mdi-plus"
          @click="
            () => {
              showModal = true
              editedUser = null
            }
          "
        >
          Adicionar
        </v-btn>
      </div>
    </v-card-title>

    <v-divider class="mb-4" />

    <v-data-table-server
      v-model:page="pagination.page"
      v-model:items-per-page="pagination.itemsPerPage"
      v-model:sort-by="pagination.sortBy"
      :items="usuarios"
      :items-length="pagination.totalItems"
      :loading="loading"
      loading-text="Carregando usuários..."
      :items-per-page-options="[5, 10, 20, 50, 100]"
      class="elevation-1"
      :headers="headers"
    >
      <template #[`item.creationDate`]="{ item }">
        {{ formatDate(item.creationDate) }}
      </template>

      <template #[`item.roles`]="{ item }">
        <v-chip
          v-for="role in item.roles"
          :key="role"
          class="ma-1"
          color="primary"
          variant="tonal"
          label
          small
        >
          {{ role }}
        </v-chip>
      </template>

      <template #[`item.actions`]="{ item }">
        <v-btn icon size="small" color="blue" variant="text" @click="openEditModal(item)">
          <v-icon>mdi-pencil</v-icon>
        </v-btn>

        <v-btn icon size="small" color="red" variant="text" @click="deleteUsuario(item.id)">
          <v-icon>mdi-delete</v-icon>
        </v-btn>
      </template>
    </v-data-table-server>
  </v-card>
</template>
