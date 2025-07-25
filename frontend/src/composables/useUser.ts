import { ref, watch, onMounted } from 'vue'
import api from '@/services/api'
import { useToast } from '@/composables/useToast'
import { useConfirm } from '@/composables/useConfirm'
import type { User } from '@/models/User'
import { parseError } from '@/utils/errorHandler'

export function useUser() {
  const { showToast } = useToast()
  const { showConfirm } = useConfirm()

  const usuarios = ref<User[]>([])
  const loading = ref(false)
  const saveLoading = ref(false)
  const showModal = ref(false)
  const editedUser = ref<User | null>(null)

  const pagination = ref({
    page: 1,
    itemsPerPage: 10,
    sortBy: [{ key: 'id', order: 'asc' as const }],
    totalItems: 0,
  })

  const headers = [
    { title: 'ID', key: 'id' },
    { title: 'Nome', key: 'name' },
    { title: 'Email', key: 'email' },
    { title: 'Permissão', key: 'roles' },
    { title: 'Criação', key: 'creationDate' },
    { title: 'Ações', key: 'actions', sortable: false },
  ]

  function openEditModal(user: User) {
    editedUser.value = { ...user }
    showModal.value = true
  }

  async function handleSaveUsuario(payload: Partial<User> & { password?: string }) {
    saveLoading.value = true
    try {
      if (payload.id) {
        await api.put(`/users/${payload.id}`, payload)
        showToast('Usuário atualizado com sucesso!', 'success')
      } else {
        await api.post('/users', payload)
        showToast('Usuário adicionado com sucesso!', 'success')
      }
      showModal.value = false
      fetchUsuarios()
    } catch (error) {
      showToast(parseError(error), 'error')
    } finally {
      saveLoading.value = false
    }
  }

  async function fetchUsuarios() {
    try {
      loading.value = true

      const { page, itemsPerPage, sortBy } = pagination.value
      const sort = sortBy.length ? `${sortBy[0].key},${sortBy[0].order}` : 'id,asc'

      const response = await api.get('/users', {
        params: {
          page: page - 1,
          size: itemsPerPage,
          sort,
        },
      })

      usuarios.value = response.data.content
      pagination.value.totalItems = response.data.page.totalElements
    } catch (error) {
      showToast(parseError(error), 'error')
    } finally {
      loading.value = false
    }
  }

  async function deleteUsuario(id: number) {
    const confirmDelete = await showConfirm(
      'Deletar usuário',
      'Tem certeza que deseja excluir este usuário?',
      'error',
    )
    if (!confirmDelete) return

    try {
      await api.delete(`/users/${id}`)
      showToast('Usuário excluído com sucesso', 'success')
      fetchUsuarios()
    } catch (error) {
      showToast(parseError(error), 'error')
    }
  }

  watch(
    () => [pagination.value.page, pagination.value.itemsPerPage, pagination.value.sortBy],
    fetchUsuarios,
    { deep: true },
  )

  onMounted(() => {
    fetchUsuarios()
  })

  return {
    usuarios,
    loading,
    pagination,
    headers,
    saveLoading,
    showModal,
    editedUser,
    fetchUsuarios,
    deleteUsuario,
    handleSaveUsuario,
    openEditModal,
  }
}
