<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import type { Task, TaskCategory, TaskPriority, TaskStatus } from '@/models/Task'

const emit = defineEmits(['update:isOpen', 'saved'])

const props = defineProps<{
  isOpen: boolean
  loading: boolean
  task: Task | null
}>()

// --- Estado do Formulário ---
const internalOpen = ref(props.isOpen)
const formRef = ref()
const title = ref('')
const description = ref('')
const status = ref<TaskStatus>('PENDING')
const priority = ref<TaskPriority>('MEDIUM')
const category = ref<TaskCategory>('BEFORE_MOVE')
const deadline = ref('')

// --- Opções para os Selects (TRADUZIDO) ---
const statusOptions = [
  { title: 'Pendente', value: 'PENDING' },
  { title: 'Em Progresso', value: 'IN_PROGRESS' },
  { title: 'Concluída', value: 'COMPLETED' },
]

const priorityOptions = [
  { title: 'Baixa', value: 'LOW' },
  { title: 'Média', value: 'MEDIUM' },
  { title: 'Alta', value: 'HIGH' },
  { title: 'Urgente', value: 'URGENT' },
]

const categoryOptions = [
  { title: 'Antes da Mudança', value: 'BEFORE_MOVE' },
  { title: 'Durante a Mudança', value: 'DURING_MOVE' },
  { title: 'Depois da Mudança', value: 'AFTER_MOVE' },
]

const dialogTitle = computed(() => (props.task ? 'Editar Tarefa' : 'Adicionar Tarefa'))

// Sincroniza o estado de abertura do modal com o componente pai
watch(() => props.isOpen, (val) => {
  internalOpen.value = val
  if (val && !props.task) {
    resetForm()
  }
})
watch(internalOpen, (val) => {
  emit('update:isOpen', val)
})

// Popula o formulário se estiver em modo de edição
watch(() => props.task, (taskData) => {
    if (taskData) {
      title.value = taskData.title
      description.value = taskData.description || ''
      status.value = taskData.status
      priority.value = taskData.priority
      category.value = taskData.category
      deadline.value = taskData.deadline ? taskData.deadline.split('T')[0] : ''
    }
  },
  { immediate: true },
)

function close() {
  internalOpen.value = false
}

async function submit() {
  const { valid } = await formRef.value?.validate()
  if (!valid) return

  const payload = {
    id: props.task?.id,
    title: title.value,
    description: description.value,
    status: status.value,
    priority: priority.value,
    category: category.value,
    deadline: deadline.value || null,
  }
  emit('saved', payload)
}

function resetForm() {
  formRef.value?.reset()
  formRef.value?.resetValidation()
  title.value = ''
  description.value = ''
  status.value = 'PENDING'
  priority.value = 'MEDIUM'
  category.value = 'BEFORE_MOVE'
  deadline.value = ''
}
</script>

<template>
  <v-dialog v-model="internalOpen" max-width="600px" persistent>
    <v-card>
      <v-card-title class="text-h6 font-weight-bold">
        <v-icon class="me-2">mdi-clipboard-edit-outline</v-icon>
        {{ dialogTitle }}
      </v-card-title>
      <v-card-text>
        <v-form ref="formRef" @submit.prevent="submit">
          <v-text-field
            v-model="title"
            label="Título da Tarefa"
            :rules="[(v) => !!v || 'Título é obrigatório']"
            required
            variant="outlined"
            density="comfortable"
          />
          <v-textarea
            v-model="description"
            label="Descrição (Opcional)"
            rows="3"
            variant="outlined"
            density="comfortable"
          />
          <v-row>
            <v-col cols="12" md="6">
              <v-select
                v-model="status"
                :items="statusOptions"
                label="Status"
                variant="outlined"
                density="comfortable"
              />
            </v-col>
            <v-col cols="12" md="6">
              <v-select
                v-model="priority"
                :items="priorityOptions"
                label="Prioridade"
                variant="outlined"
                density="comfortable"
              />
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="12" md="6">
              <v-select
                v-model="category"
                :items="categoryOptions"
                label="Categoria"
                variant="outlined"
                density="comfortable"
              />
            </v-col>
            <v-col cols="12" md="6">
               <v-text-field
                v-model="deadline"
                label="Prazo (Opcional)"
                type="date"
                variant="outlined"
                density="comfortable"
              />
            </v-col>
          </v-row>
        </v-form>
      </v-card-text>
      <v-card-actions class="justify-end pa-4">
        <v-btn variant="tonal" color="grey" @click="close">Cancelar</v-btn>
        <v-btn color="primary" variant="flat" :loading="loading" @click="submit">
          Salvar
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
