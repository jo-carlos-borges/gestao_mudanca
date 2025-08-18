<script setup lang="ts">
import { ref } from 'vue'
import { useBudgets, type BudgetPayload, type ExpensePayload } from '@/composables/useBudgets'
import { formatDate } from '@/utils/date'
import BudgetForm from '@/components/BudgetForm.vue'
import ExpenseForm from '@/components/ExpenseForm.vue'
import type { Budget, Expense } from '@/models/Budget'

const { loading, budgetsWithSummary, shoppingItems, saveBudget, deleteBudget, saveExpense, deleteExpense } = useBudgets()

const showBudgetForm = ref(false)
const saveBudgetLoading = ref(false)
const editingBudget = ref<Budget | null>(null)

const showExpenseForm = ref(false)
const saveExpenseLoading = ref(false)
const editingExpense = ref<Expense | null>(null)
const preselectedBudgetId = ref<number | null>(null);

function formatCurrency(value: number) {
  if(typeof value !== 'number') return ''
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value)
}

function openCreateBudgetDialog() {
  editingBudget.value = null
  showBudgetForm.value = true
}

function openEditBudgetDialog(budget: Budget) {
  editingBudget.value = budget
  showBudgetForm.value = true
}

async function handleSaveBudget(payload: BudgetPayload) {
  saveBudgetLoading.value = true
  const success = await saveBudget(payload)
  if (success) showBudgetForm.value = false
  saveBudgetLoading.value = false
}

function openCreateExpenseDialog(budget: Budget) {
  editingExpense.value = null
  preselectedBudgetId.value = budget.id;
  showExpenseForm.value = true
}

function openEditExpenseDialog(expense: Expense) {
  editingExpense.value = expense
  preselectedBudgetId.value = null;
  showExpenseForm.value = true
}

async function handleSaveExpense(payload: ExpensePayload) {
  saveExpenseLoading.value = true
  const success = await saveExpense(payload)
  if (success) showExpenseForm.value = false
  saveExpenseLoading.value = false
}
</script>

<template>
  <div>
    <BudgetForm v-model:isOpen="showBudgetForm" :loading="saveBudgetLoading" :budget="editingBudget" @saved="handleSaveBudget" />
    <ExpenseForm v-model:isOpen="showExpenseForm" :loading="saveExpenseLoading" :expense="editingExpense" :budgets="budgetsWithSummary" :shopping-items="shoppingItems" :preselected-budget-id="preselectedBudgetId" @saved="handleSaveExpense" />

    <div class="d-flex justify-space-between align-center mb-4">
      <h1 class="text-h4">Meus Orçamentos</h1>
      <v-btn color="primary" prepend-icon="mdi-plus" @click="openCreateBudgetDialog">Adicionar Orçamento</v-btn>
    </div>

    <v-progress-linear v-if="loading" indeterminate />

    <v-alert v-else-if="!budgetsWithSummary.length" type="info" variant="tonal" class="mt-4">Nenhum orçamento cadastrado.</v-alert>

    <v-row v-else>
      <v-col v-for="budget in budgetsWithSummary" :key="budget.id" cols="12" md="6">
        <v-card>
          <v-card-title class="d-flex justify-space-between">
            <span>{{ budget.name }}</span>
            <div>
              <v-chip :color="budget.progress > 100 ? 'red' : 'primary'" label>{{ formatCurrency(budget.totalAmount) }}</v-chip>
              <v-menu location="bottom">
                <template #activator="{ props }"><v-btn icon="mdi-dots-vertical" size="small" variant="text" v-bind="props" /></template>
                <v-list>
                  <v-list-item @click="openEditBudgetDialog(budget)"><template #prepend><v-icon>mdi-pencil</v-icon></template><v-list-item-title>Editar</v-list-item-title></v-list-item>
                  <v-list-item @click="deleteBudget(budget.id)"><template #prepend><v-icon>mdi-delete</v-icon></template><v-list-item-title>Excluir</v-list-item-title></v-list-item>
                </v-list>
              </v-menu>
            </div>
          </v-card-title>
          <v-card-text>
            <div>
              <div class="d-flex justify-space-between mb-1">
                <span class="text-subtitle-1">{{ formatCurrency(budget.totalSpent) }}<span class="text-caption text-grey"> gastos</span></span>
                <span class="text-caption text-grey">{{ budget.progress.toFixed(0) }}%</span>
              </div>
              <v-progress-linear :model-value="budget.progress" :color="budget.progress > 100 ? 'red' : (budget.progress > 85 ? 'orange' : 'primary')" height="10" rounded />
            </div>
            <v-divider class="my-4" />
            <div class="d-flex justify-space-between align-center">
                <v-list-subheader>Últimas Despesas</v-list-subheader>
                <v-btn size="small" variant="tonal" @click="openCreateExpenseDialog(budget)">Adicionar Despesa</v-btn>
            </div>
            <div v-if="budget.expenses.length > 0">
              <v-list lines="two" density="compact">
                <v-list-item v-for="expense in budget.expenses.slice(0, 5)" :key="expense.id" :title="expense.description" :subtitle="formatDate(expense.date)">
                  <template #append>
                    <span class="font-weight-bold me-2">{{ formatCurrency(expense.amount) }}</span>
                    <v-btn icon="mdi-pencil" size="x-small" variant="text" @click="openEditExpenseDialog(expense)" />
                    <v-btn icon="mdi-delete" size="x-small" variant="text" @click="deleteExpense(expense.id)" />
                  </template>
                </v-list-item>
              </v-list>
            </div>
             <div v-else class="text-center text-grey py-4">Nenhuma despesa registrada.</div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </div>
</template>
