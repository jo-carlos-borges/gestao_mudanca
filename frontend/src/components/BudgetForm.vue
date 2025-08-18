<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import type { Budget } from '@/models/Budget'

const props = defineProps<{
  isOpen: boolean
  loading: boolean
  budget: Budget | null
}>()

const emit = defineEmits(['update:isOpen', 'saved'])

const internalOpen = ref(props.isOpen)
const formRef = ref()

const name = ref('')
const totalAmount = ref<number | null>(null)

const dialogTitle = computed(() => (props.budget ? 'Editar Orçamento' : 'Novo Orçamento'))

watch(() => props.isOpen, (val) => {
  internalOpen.value = val
  if (val && !props.budget) {
    resetForm()
  }
})

watch(() => props.budget, (currentBudget) => {
    if (currentBudget) {
      name.value = currentBudget.name
      totalAmount.value = currentBudget.totalAmount
    }
  }, { immediate: true }
)

watch(internalOpen, (val) => emit('update:isOpen', val))

async function submit() {
  const { valid } = await formRef.value?.validate()
  if (!valid) return

  const payload = {
    id: props.budget?.id,
    name: name.value,
    totalAmount: totalAmount.value
  }
  emit('saved', payload)
}

function close() {
  internalOpen.value = false
}

function resetForm() {
    formRef.value?.resetValidation()
    name.value = ''
    totalAmount.value = null
}
</script>

<template>
  <v-dialog v-model="internalOpen" max-width="500px" persistent>
    <v-card>
      <v-card-title class="font-weight-bold">{{ dialogTitle }}</v-card-title>
      <v-card-text>
        <v-form ref="formRef" @submit.prevent="submit">
          <v-text-field
            v-model="name"
            label="Nome do Orçamento"
            :rules="[v => !!v || 'O nome é obrigatório']"
            variant="outlined"
            placeholder="Ex: Móveis para a Sala"
          />
          <v-text-field
            v-model.number="totalAmount"
            label="Valor Total (R$)"
            type="number"
            prefix="R$"
            :rules="[v => v > 0 || 'O valor deve ser positivo']"
            variant="outlined"
          />
        </v-form>
      </v-card-text>
      <v-card-actions class="pa-4">
        <v-spacer />
        <v-btn variant="tonal" @click="close">Cancelar</v-btn>
        <v-btn color="primary" :loading="loading" @click="submit">Salvar</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
