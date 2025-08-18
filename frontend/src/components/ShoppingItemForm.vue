<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import type { Room } from '@/composables/useShopping'
import type { ShoppingItem } from '@/composables/useShopping'

const emit = defineEmits(['update:isOpen', 'saved'])

const props = defineProps<{
  isOpen: boolean
  loading: boolean
  rooms: Room[]
  item: ShoppingItem | null
}>()

const internalOpen = ref(props.isOpen)
const formRef = ref()

const name = ref('')
const quantity = ref(1)
const status = ref('PENDING')
const priority = ref('IMPORTANT')
const roomId = ref<number | null>(null)

const dialogTitle = computed(() => props.item ? 'Editar Item' : 'Adicionar Item')

const statusOptions = [
  { title: 'Pendente', value: 'PENDING' },
  { title: 'Comprado', value: 'PURCHASED' },
  { title: 'Descartado', value: 'DISCARDED' },
]
const priorityOptions = [
  { title: 'Essencial', value: 'ESSENTIAL' },
  { title: 'Importante', value: 'IMPORTANT' },
  { title: 'Desejável', value: 'DESIRABLE' },
]

watch(() => props.isOpen, (val) => {
  internalOpen.value = val
  if (!val) {
     setTimeout(() => formRef.value?.resetValidation(), 100)
  }
})

watch(() => props.item, (currentItem) => {
    if(currentItem) {
        name.value = currentItem.name
        quantity.value = currentItem.quantity
        status.value = currentItem.status
        priority.value = currentItem.priority
        roomId.value = currentItem.room.id
    } else {
        resetForm()
    }
}, { immediate: true })

watch(internalOpen, (val) => emit('update:isOpen', val))

function close() {
  internalOpen.value = false
}

async function submit() {
  const { valid } = await formRef.value?.validate()
  if (!valid) return

  const payload = {
    id: props.item?.id,
    name: name.value,
    quantity: quantity.value,
    status: status.value,
    priority: priority.value,
    roomId: roomId.value,
  }
  emit('saved', payload)
}

function resetForm() {
  name.value = ''
  quantity.value = 1
  status.value = 'PENDING'
  priority.value = 'IMPORTANT'
  roomId.value = null
}
</script>

<template>
  <v-dialog v-model="internalOpen" max-width="600px" persistent>
    <v-card>
      <v-card-title class="font-weight-bold">{{ dialogTitle }}</v-card-title>
      <v-card-text>
        <v-form ref="formRef" @submit.prevent="submit">
          <v-text-field v-model="name" label="Nome do Item" :rules="[v => !!v || 'O nome é obrigatório']" required variant="outlined" />
          <v-select v-model="roomId" :items="rooms" item-title="name" item-value="id" label="Cômodo" :rules="[v => !!v || 'Selecione um cômodo']" required variant="outlined" />
          <v-row>
            <v-col cols="12" md="4">
              <v-text-field v-model.number="quantity" label="Quantidade" type="number" :min="1" variant="outlined" />
            </v-col>
            <v-col cols="12" md="4">
              <v-select v-model="priority" :items="priorityOptions" label="Prioridade" variant="outlined" />
            </v-col>
             <v-col cols="12" md="4">
              <v-select v-model="status" :items="statusOptions" label="Status" variant="outlined" />
            </v-col>
          </v-row>
        </v-form>
      </v-card-text>
      <v-card-actions class="justify-end pa-4">
        <v-btn variant="tonal" color="grey" @click="close">Cancelar</v-btn>
        <v-btn color="primary" variant="flat" :loading="loading" @click="submit">Salvar</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
