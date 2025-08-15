import { ref, onMounted } from 'vue'
import api from '@/services/api'
import { useToast } from '@/composables/useToast'
import { useConfirm } from '@/composables/useConfirm'
import { parseError } from '@/utils/errorHandler'

// --- Interfaces ---
export interface Room {
  id: number
  name: string
}

// Adicionei mais campos para usarmos na edição
export interface ShoppingItem {
  id: number
  name: string
  quantity: number
  status: string
  priority: string
  roomId: number
  room?: Room // Opcional, para exibição
}

export function useShopping() {
  const { showToast } = useToast()
  const { showConfirm } = useConfirm()

  // --- Estado para Cômodos (Rooms) ---
  const rooms = ref<Room[]>([])
  const loadingRooms = ref(false)

  // --- Estado para Itens de Compra (ShoppingItems) ---
  const items = ref<any[]>([]) // Mudei para 'any' para aceitar o objeto completo da API
  const loadingItems = ref(false)

  // --- Funções para Cômodos (sem alterações) ---
  async function fetchRooms() {
    try {
      loadingRooms.value = true
      const response = await api.get<Room[]>('/rooms')
      rooms.value = response.data
    } catch (error) { showToast(parseError(error), 'error') }
    finally { loadingRooms.value = false }
  }

  async function createRoom(name: string) {
    if (!name || name.trim() === '') {
      showToast('O nome do cômodo não pode estar vazio.', 'warning')
      return false
    }
    try {
      await api.post('/rooms', { name })
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
    const isConfirmed = await showConfirm('Excluir Cômodo', 'Tem certeza que deseja excluir este cômodo?')
    if (!isConfirmed) return
    try {
      await api.delete(`/rooms/${id}`)
      showToast('Cômodo excluído com sucesso!', 'success')
      await fetchRooms()
    } catch (error) { showToast(parseError(error), 'error') }
  }

  // --- Funções para Itens de Compra ---
  async function fetchItems() {
    try {
        loadingItems.value = true
        const response = await api.get('/shopping-items', { params: { size: 200, sort: 'name,asc' } })
        items.value = response.data.content
    } catch (error) { showToast(parseError(error), 'error') }
    finally { loadingItems.value = false }
  }

  async function saveShoppingItem(payload: any) {
    try {
        if(payload.id) {
            // Edição
            await api.put(`/shopping-items/${payload.id}`, payload);
            showToast('Item atualizado com sucesso!', 'success');
        } else {
            // Criação
            await api.post('/shopping-items', payload);
            showToast('Item adicionado com sucesso!', 'success');
        }
      await fetchItems();
      return true;
    } catch (error) {
      showToast(parseError(error), 'error');
      return false;
    }
  }

  async function deleteShoppingItem(id: number) {
    const isConfirmed = await showConfirm('Excluir Item', 'Tem certeza que deseja excluir este item da sua lista?')
    if (!isConfirmed) return;
    try {
        await api.delete(`/shopping-items/${id}`);
        showToast('Item excluído com sucesso!', 'success');
        await fetchItems();
    } catch (error) {
        showToast(parseError(error), 'error');
    }
  }

  onMounted(() => {
    fetchRooms()
    fetchItems()
  })

  return {
    rooms, loadingRooms, fetchRooms, createRoom, updateRoom, deleteRoom,
    items, loadingItems, fetchItems, saveShoppingItem, deleteShoppingItem
  }
}
