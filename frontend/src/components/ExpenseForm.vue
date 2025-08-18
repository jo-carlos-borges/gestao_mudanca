<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import type { Budget, Expense } from '@/models/Budget'
import type { ShoppingItem } from '@/models/Shopping'

const props = defineProps<{
  isOpen: boolean
  loading: boolean
  expense: Expense | null
  budgets: Budget[]
  shoppingItems: ShoppingItem[]
  preselectedBudgetId?: number | null
}>()

const emit = defineEmits(['update:isOpen', 'saved'])

const internalOpen = ref(props.isOpen)
const formRef = ref()

const description = ref('')
const amount = ref<number | null>(null)
const date = ref(new Date().toISOString().slice(0, 10))
const budgetId = ref<number | null>(null)
const shoppingItemId = ref<number | null>(null)

const dialogTitle = computed(() => (props.expense ? 'Editar Despesa' : 'Registrar Despesa'))

watch(() => props.isOpen, (val) => {
  internalOpen.value = val
  if (val) {
    if (!props.expense && props.preselectedBudgetId) {
      budgetId.value = props.preselectedBudgetId
    }
  }
})

watch(() => props.expense, (currentExpense) => {
    if (currentExpense) {
        description.value = currentExpense.description
        amount.value = currentExpense.amount
        date.value = currentExpense.date
        budgetId.value = currentExpense.budget.id
        shoppingItemId.value = currentExpense.shoppingItem?.id || null
    } else {
        resetForm()
    }
}, { immediate: true })


watch(internalOpen, (val) => emit('update:isOpen', val))

async function submit() {
  const { valid } = await formRef.value?.validate()
  if (!valid) return

  const payload = {
    id: props.expense?.id,
    description: description.value,
    amount: amount.value,
    date: date.value,
    budgetId: budgetId.value,
    shoppingItemId: shoppingItemId.value
  }
  emit('saved', payload)
}

function close() {
  internalOpen.value = false
}

function resetForm() {
    formRef.value?.resetValidation()
    description.value = ''
    amount.value = null
    date.value = new Date().toISOString().slice(0, 10)
    budgetId.value = props.preselectedBudgetId || null
    shoppingItemId.value = null
}
</script>

<template>
  <v-dialog v-model="internalOpen" max-width="600px" persistent>
    <v-card>
      <v-card-title class="font-weight-bold">{{ dialogTitle }}</v-card-title>
      <v-card-text>
        <v-form ref="formRef" @submit.prevent="submit">
          <v-text-field v-model="description" label="Descrição da Despesa" :rules="[v => !!v || 'Campo obrigatório']" variant="outlined" />
          <v-row>
            <v-col cols="12" md="6">
              <v-text-field v-model.number="amount" label="Valor (R$)" type="number" prefix="R$" :rules="[v => v > 0 || 'Valor inválido']" variant="outlined" />
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field v-model="date" label="Data da Despesa" type="date" :rules="[v => !!v || 'Campo obrigatório']" variant="outlined" />
            </v-col>
          </v-row>
          <v-select v-model="budgetId" :items="budgets" item-title="name" item-value="id" label="Orçamento" :rules="[v => !!v || 'Campo obrigatório']" variant="outlined" />
          <v-autocomplete v-model="shoppingItemId" :items="shoppingItems" item-title="name" item-value="id" label="Vincular a um Item de Compra (Opcional)" variant="outlined" clearable />
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
