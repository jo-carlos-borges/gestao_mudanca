<script setup lang="ts">
import { useTasks } from '@/composables/useTasks'
import { formatDate } from '@/utils/date'
import TaskForm from '@/components/TaskForm.vue'
import type { TaskCategory, TaskPriority, TaskStatus } from '@/models/Task'

const {
  tasks,
  loading,
  pagination,
  headers,
  saveLoading,
  showModal,
  editedTask,
  deleteTask,
  handleSaveTask,
  openEditModal,
} = useTasks()

// --- Mapeamento de Cores ---
const statusColors: Record<TaskStatus, string> = {
  PENDING: 'blue',
  IN_PROGRESS: 'orange',
  COMPLETED: 'green',
}

const priorityColors: Record<TaskPriority, string> = {
  LOW: 'grey',
  MEDIUM: 'blue',
  HIGH: 'orange',
  URGENT: 'red',
}

// --- Mapeamento de Labels
const statusLabels: Record<TaskStatus, string> = {
  PENDING: 'Pendente',
  IN_PROGRESS: 'Em Progresso',
  COMPLETED: 'Concluída',
}

const priorityLabels: Record<TaskPriority, string> = {
  LOW: 'Baixa',
  MEDIUM: 'Média',
  HIGH: 'Alta',
  URGENT: 'Urgente',
}

const categoryLabels: Record<TaskCategory, string> = {
  BEFORE_MOVE: 'Antes da Mudança',
  DURING_MOVE: 'Durante a Mudança',
  AFTER_MOVE: 'Depois da Mudança',
}
</script>

<template>
  <v-card class="pa-4">
    <v-card-title class="text-h5 d-flex justify-space-between">
      Minhas Tarefas
      <div>
        <TaskForm
          v-model:isOpen="showModal"
          :loading="saveLoading"
          :task="editedTask"
          @saved="handleSaveTask"
        />
        <v-btn
          color="primary"
          prepend-icon="mdi-plus"
          @click="
            () => {
              editedTask = null
              showModal = true
            }
          "
        >
          Adicionar Tarefa
        </v-btn>
      </div>
    </v-card-title>

    <v-divider class="my-4" />

    <v-data-table-server
      v-model:page="pagination.page"
      v-model:items-per-page="pagination.itemsPerPage"
      v-model:sort-by="pagination.sortBy"
      :items="tasks"
      :items-length="pagination.totalItems"
      :loading="loading"
      loading-text="Carregando tarefas..."
      :headers="headers"
      class="elevation-1"
      density="comfortable"
    >
      <template #[`item.status`]="{ item }">
        <v-chip :color="statusColors[item.status]" variant="tonal" label small>
          {{ statusLabels[item.status] }}
        </v-chip>
      </template>

      <template #[`item.priority`]="{ item }">
        <v-chip :color="priorityColors[item.priority]" variant="tonal" label small>
          {{ priorityLabels[item.priority] }}
        </v-chip>
      </template>

      <template #[`item.category`]="{ item }">
        {{ categoryLabels[item.category] }}
      </template>

      <template #[`item.deadline`]="{ item }">
        {{ item.deadline ? formatDate(item.deadline) : 'N/D' }}
      </template>

      <template #[`item.actions`]="{ item }">
        <v-btn icon size="small" color="blue" variant="text" @click="openEditModal(item)">
          <v-icon>mdi-pencil</v-icon>
        </v-btn>
        <v-btn icon size="small" color="red" variant="text" @click="deleteTask(item.id)">
          <v-icon>mdi-delete</v-icon>
        </v-btn>
      </template>
    </v-data-table-server>
  </v-card>
</template>
