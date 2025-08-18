<script setup lang="ts">
import { ref, computed } from 'vue'
import { useShopping, type Room, type ShoppingItem } from '@/composables/useShopping'
import ShoppingItemForm from '@/components/ShoppingItemForm.vue'

// --- Lógica de Cômodos ---
const { rooms, loadingRooms, createRoom, updateRoom, deleteRoom, items, loadingItems, saveShoppingItem, deleteShoppingItem } = useShopping()
const newRoomName = ref('')
const isCreatingRoom = ref(false)
const editDialog = ref(false)
const editingRoom = ref<Room | null>(null)
const isUpdatingRoom = ref(false)

// --- Lógica de Itens de Compra ---
const showItemForm = ref(false)
const isSavingItem = ref(false)
const editingItem = ref<ShoppingItem | null>(null)

const groupedItems = computed(() => {
  const groups: Record<string, any[]> = {}
  for (const room of rooms.value) {
      groups[room.name] = []
  }
  for (const item of items.value) {
    const roomName = item.room?.name || 'Sem Cômodo'
    if (!groups[roomName]) groups[roomName] = []
    groups[roomName].push(item)
  }
  return groups
})

async function handleAddRoom() {
  isCreatingRoom.value = true
  const success = await createRoom(newRoomName.value)
  if (success) newRoomName.value = ''
  isCreatingRoom.value = false
}

function openEditDialog(room: Room) {
  editingRoom.value = { ...room }
  editDialog.value = true
}

async function handleUpdateRoom() {
  if (!editingRoom.value) return
  isUpdatingRoom.value = true
  const success = await updateRoom(editingRoom.value)
  if (success) editDialog.value = false
  isUpdatingRoom.value = false
}

function openItemCreateDialog() {
    editingItem.value = null
    showItemForm.value = true
}

function openItemEditDialog(item: ShoppingItem) {
    editingItem.value = item
    showItemForm.value = true
}

async function handleSaveItem(payload: any) {
    isSavingItem.value = true
    const success = await saveShoppingItem(payload)
    if (success) {
        showItemForm.value = false
    }
    isSavingItem.value = false
}
</script>

<template>
  <v-dialog v-model="editDialog" max-width="400px" persistent>
     <v-card>
      <v-card-title>Editar Cômodo</v-card-title>
      <v-card-text><v-text-field v-if="editingRoom" v-model="editingRoom.name" label="Nome do cômodo" variant="outlined" @keyup.enter="handleUpdateRoom" /></v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn text @click="editDialog = false">Cancelar</v-btn>
        <v-btn color="primary" :loading="isUpdatingRoom" @click="handleUpdateRoom">Salvar</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>

  <ShoppingItemForm v-model:isOpen="showItemForm" :loading="isSavingItem" :rooms="rooms" :item="editingItem" @saved="handleSaveItem" />

  <v-row>
    <v-col cols="12" md="4">
        <v-card>
            <v-card-title class="text-h6"><v-icon class="me-2">mdi-door-open</v-icon>Gerenciar Cômodos</v-card-title>
            <v-card-text>
                <v-form @submit.prevent="handleAddRoom">
                <v-text-field v-model="newRoomName" label="Nome do novo cômodo" variant="outlined" density="comfortable" append-inner-icon="mdi-plus-box" @click:append-inner="handleAddRoom" :loading="isCreatingRoom" :disabled="isCreatingRoom" placeholder="Ex: Cozinha"/>
                </v-form>
                <v-divider class="my-4" />
                <v-list-subheader>Cômodos Cadastrados</v-list-subheader>
                <v-progress-linear v-if="loadingRooms" indeterminate />
                <v-list v-else-if="rooms.length > 0" lines="one">
                <v-list-item v-for="room in rooms" :key="room.id" :title="room.name">
                    <template #append>
                    <v-btn icon="mdi-pencil" size="small" variant="text" @click="openEditDialog(room)" />
                    <v-btn icon="mdi-delete" size="small" variant="text" @click="deleteRoom(room.id)" />
                    </template>
                </v-list-item>
                </v-list>
                <v-card-text v-else class="text-center text-grey">Nenhum cômodo cadastrado.</v-card-text>
            </v-card-text>
        </v-card>
    </v-col>

    <v-col cols="12" md="8">
      <v-card>
        <v-card-title class="d-flex justify-space-between align-center">
          <span><v-icon class="me-2">mdi-cart-outline</v-icon>Lista de Compras</span>
          <v-btn color="primary" @click="openItemCreateDialog">Adicionar Item</v-btn>
        </v-card-title>
        <v-progress-linear v-if="loadingItems" indeterminate />
        <v-card-text v-if="!loadingItems && items.length === 0" class="text-center text-grey py-8">
            <v-icon size="48" class="mb-2">mdi-cart-off</v-icon>
            <div>Sua lista de compras está vazia.</div>
        </v-card-text>
        <v-expansion-panels v-else variant="accordion">
          <v-expansion-panel v-for="(group, roomName) in groupedItems" :key="roomName">
            <v-expansion-panel-title>
              {{ roomName }}
              <v-chip size="small" class="ms-2" :color="group.length > 0 ? 'primary' : 'default'">{{ group.length }}</v-chip>
            </v-expansion-panel-title>
            <v-expansion-panel-text>
              <v-list v-if="group.length > 0">
                <v-list-item v-for="item in group" :key="item.id">
                    <v-list-item-title>{{ item.quantity }}x {{ item.name }}</v-list-item-title>
                    <v-list-item-subtitle>{{ item.priority }} - {{ item.status }}</v-list-item-subtitle>
                    <template #append>
                        <v-btn icon="mdi-pencil" size="x-small" variant="text" @click="openItemEditDialog(item)" />
                        <v-btn icon="mdi-delete" size="x-small" variant="text" @click="deleteShoppingItem(item.id)" />
                    </template>
                </v-list-item>
              </v-list>
              <v-card-text v-else class="text-grey">Nenhum item para este cômodo ainda.</v-card-text>
            </v-expansion-panel-text>
          </v-expansion-panel>
        </v-expansion-panels>
      </v-card>
    </v-col>
  </v-row>
</template>
