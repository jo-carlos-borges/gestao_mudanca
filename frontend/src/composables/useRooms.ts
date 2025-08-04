import { ref, onMounted } from 'vue'
import api from '@/services/api'
import { useToast } from '@/composables/useToast'
import { useConfirm } from '@/composables/useConfirm'
import { parseError } from '@/utils/errorHandler'

export interface Room {
  id: number
  name: string
}

export function useRooms() {
  const { showToast } = useToast()
  const { showConfirm } = useConfirm()

  const rooms = ref<Room[]>([])
  const loading = ref(false)

  async function fetchRooms() {
    try {
      loading.value = true
      const response = await api.get<Room[]>('/rooms')
      rooms.value = response.data
    } catch (error) {
      showToast(parseError(error), 'error')
    } finally {
      loading.value = false
    }
  }

  async function createRoom(name: string) {
    if (!name || name.trim() === '') {
      showToast('O nome do cômodo não pode estar vazio.', 'warning')
      return
    }

    try {
      await api.post('rooms', { name })
      showToast('Cômodo adicionado com sucesso!', 'success')
      await fetchRooms()
      return true
    } catch (error) {
      showToast(parseError(error), 'error')
      return false
    }
  }

  async function updateRoom(room: Room) {
    try {
      await api.put(`/rooms/${room.id}`, { name: room.name })
      showToast('Cômodo atualizado com sucesso!', 'success')
      await fetchRooms()
      return true
    } catch (error) {
      showToast(parseError(error), 'error')
      return false
    }
  }

  async function deleteRoom(id: number) {
    const isConfirmed = await showConfirm('Excluir Cômodo', 'Tem certeza que deseja excluir este cômodo? Itens de compra associados a ele podem ser afetados no futuro.',)

    if (!isConfirmed) return

    try {
      await api.delete(`/rooms/${id}`)
      showToast('Cômodo excluído com sucesso!', 'success')
      await fetchRooms()
    } catch (error) {
      showToast(parseError(error), 'error')
    }
  }

  onMounted(fetchRooms)

  return {
    rooms,
    loading,
    fetchRooms,
    createRoom,
    updateRoom,
    deleteRoom,
  }
}
