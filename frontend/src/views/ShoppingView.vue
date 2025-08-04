<script setup lang="ts">
import { ref } from 'vue'
import { useRooms, type Room } from '@/composables/useRooms'

const { rooms, loading, createRoom, updateRoom, deleteRoom } = useRooms()

const newRoomName = ref('')
const isCreating = ref(false)

const editDialog = ref(false)
const editingRoom = ref<Room | null>(null)
const isUpdating = ref(false)

async function handleAddRoom() {
  isCreating.value = true
  const success = await createRoom(newRoomName.value)
  if (success) {
    newRoomName.value = ''
  }
  isCreating.value = false
}

function openEditDialog(room: Room) {
  editingRoom.value = { ...room }
  editDialog.value = true
}

async function handleUpdateRoom() {
  if (!editingRoom.value) return

  isUpdating.value = true
  const success = await updateRoom(editingRoom.value)
  if (success) {
    editDialog.value = false
  }
  isUpdating.value = false
}
</script>

<template>
  <v-dialog v-model="editDialog" max-width="400px" persistent>
    <v-card>
      <v-card-title>Editar Cômodo</v-card-title>
      <v-card-text>
        <v-text-field
          v-if="editingRoom"
          v-model="editingRoom.name"
          label="Nome do cômodo"
          variant="outlined"
          @keyup.enter="handleUpdateRoom"
        />
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn text @click="editDialog = false">Cancelar</v-btn>
        <v-btn color="primary" :loading="isUpdating" @click="handleUpdateRoom">Salvar</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>

  <v-row>
    <v-col cols="12" md="4">
      <v-card>
        <v-card-title class="text-h6">
          <v-icon class="me-2">mdi-door-open</v-icon>
          Gerenciar Cômodos
        </v-card-title>
        <v-card-text>
          <v-form @submit.prevent="handleAddRoom">
            <v-text-field
              v-model="newRoomName"
              label="Nome do novo cômodo"
              variant="outlined"
              density="comfortable"
              append-inner-icon="mdi-plus-box"
              @click:append-inner="handleAddRoom"
              :loading="isCreating"
              :disabled="isCreating"
              placeholder="Ex: Cozinha"
            />
          </v-form>

          <v-divider class="my-4" />

          <v-list-subheader>Cômodos Cadastrados</v-list-subheader>
          <v-progress-linear v-if="loading" indeterminate />
          <v-list v-else-if="rooms.length > 0" lines="one">
            <v-list-item v-for="room in rooms" :key="room.id" :title="room.name">
              <template #append>
                <v-btn icon="mdi-pencil" size="small" variant="text" @click="openEditDialog(room)" />
                <v-btn icon="mdi-delete" size="small" variant="text" @click="deleteRoom(room.id)" />
              </template>
            </v-list-item>
          </v-list>
          <v-card-text v-else class="text-center text-grey">
            Nenhum cômodo cadastrado.
          </v-card-text>
        </v-card-text>
      </v-card>
    </v-col>

    <v-col cols="12" md="8">
      <v-card class="fill-height">
        <v-card-title class="text-h6">
          <v-icon class="me-2">mdi-cart-outline</v-icon>
          Lista de Compras
        </v-card-title>
        <v-card-text class="text-center text-grey">
          Em breve...
        </v-card-text>
      </v-card>
    </v-col>
  </v-row>
</template>
