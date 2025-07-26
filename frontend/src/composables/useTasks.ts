// Local: src/composables/useTasks.ts
import { ref, watch, onMounted } from 'vue'
import api from '@/services/api'
import { useToast } from '@/composables/useToast'
import { useConfirm } from '@/composables/useConfirm'
import type { Task } from '@/models/Task'
import { parseError } from '@/utils/errorHandler'

export function useTasks() {
  const { showToast } = useToast()
  const { showConfirm } = useConfirm()

  const tasks = ref<Task[]>([])
  const loading = ref(false)
  const saveLoading = ref(false)
  const showModal = ref(false)
  const editedTask = ref<Task | null>(null)

  const pagination = ref({
    page: 1,
    itemsPerPage: 10,
    sortBy: [{ key: 'id', order: 'asc' as const }],
    totalItems: 0,
  })

  const headers = [
    { title: 'ID', key: 'id', width: '80px' },
    { title: 'Título', key: 'title' },
    { title: 'Status', key: 'status' },
    { title: 'Prioridade', key: 'priority' },
    { title: 'Categoria', key: 'category' },
    { title: 'Prazo', key: 'deadline' },
    { title: 'Ações', key: 'actions', sortable: false, align: 'end' as const, width: '120px' },
  ]

  async function fetchTasks() {
    try {
      loading.value = true
      const { page, itemsPerPage, sortBy } = pagination.value
      const sort = sortBy.length ? `${sortBy[0].key},${sortBy[0].order}` : 'id,asc'

      const response = await api.get('/tasks', {
        params: {
          page: page - 1,
          size: itemsPerPage,
          sort,
        },
      })

      tasks.value = response.data.content
      pagination.value.totalItems = response.data.totalElements
    } catch (error) {
      showToast(parseError(error), 'error')
    } finally {
      loading.value = false
    }
  }

  async function handleSaveTask(payload: any) {
    saveLoading.value = true
    try {
      if (payload.id) {
        await api.put(`/tasks/${payload.id}`, payload)
        showToast('Tarefa atualizada com sucesso!', 'success')
      } else {
        await api.post('/tasks', payload)
        showToast('Tarefa adicionada com sucesso!', 'success')
      }
      showModal.value = false
      fetchTasks()
    } catch (error) {
      showToast(parseError(error), 'error')
    } finally {
      saveLoading.value = false
    }
  }

  async function deleteTask(id: number) {
    const isConfirmed = await showConfirm(
      'Excluir Tarefa',
      'Tem certeza que deseja excluir esta tarefa? Esta ação não pode ser desfeita.',
      'error'
    )

    if (!isConfirmed) return

    try {
      await api.delete(`/tasks/${id}`)
      showToast('Tarefa excluída com sucesso!', 'success')
      fetchTasks()
    } catch (error) {
      showToast(parseError(error), 'error')
    }
  }

  function openEditModal(task: Task) {
    editedTask.value = { ...task }
    showModal.value = true
  }

  watch(
    () => [pagination.value.page, pagination.value.itemsPerPage, pagination.value.sortBy],
    fetchTasks,
    { deep: true },
  )

  onMounted(() => {
    fetchTasks()
  })

  return {
    tasks,
    loading,
    pagination,
    headers,
    saveLoading,
    showModal,
    editedTask,
    fetchTasks,
    deleteTask,
    handleSaveTask,
    openEditModal,
  }
}
