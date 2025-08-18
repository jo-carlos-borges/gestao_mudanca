import { ref, onMounted, computed } from 'vue'
import api from '@/services/api'
import { useToast } from '@/composables/useToast'
import { useConfirm } from '@/composables/useConfirm'
import { parseError } from '@/utils/errorHandler'
import type { Budget, Expense } from '@/models/Budget'
import type { ShoppingItem } from '@/models/Shopping'

export interface BudgetPayload {
  id?: number;
  name: string;
  totalAmount: number | null;
}

export interface ExpensePayload {
  id?: number;
  description: string;
  amount: number | null;
  date: string;
  budgetId: number | null;
  shoppingItemId: number | null;
}

export function useBudgets() {
  const { showToast } = useToast()
  const { showConfirm } = useConfirm()

  const budgets = ref<Budget[]>([])
  const expenses = ref<Expense[]>([])
  const shoppingItems = ref<ShoppingItem[]>([])
  const loading = ref(false)

  const budgetsWithSummary = computed(() => {
    return budgets.value.map(budget => {
      const budgetExpenses = expenses.value.filter((expense: Expense) => expense.budget.id === budget.id)
      const totalSpent = budgetExpenses.reduce((sum: number, expense: Expense) => sum + expense.amount, 0)

      return {
        ...budget,
        expenses: budgetExpenses,
        totalSpent,
        progress: budget.totalAmount > 0 ? (totalSpent / budget.totalAmount) * 100 : 0
      }
    }).sort((a, b) => a.name.localeCompare(b.name));
  })

  async function fetchData() {
    try {
      loading.value = true
      const [budgetsResponse, expensesResponse, itemsResponse] = await Promise.all([
        api.get<Budget[]>('/budgets'),
        api.get<Expense[]>('/expenses'),
        api.get('/shopping-items', { params: { size: 200 } })
      ])
      budgets.value = budgetsResponse.data
      expenses.value = expensesResponse.data
      shoppingItems.value = itemsResponse.data.content
    } catch (error) { showToast(parseError(error), 'error') }
    finally { loading.value = false }
  }

  async function saveBudget(payload: BudgetPayload) {
    try {
      if (payload.id) {
        await api.put(`/budgets/${payload.id}`, payload)
        showToast('Orçamento atualizado com sucesso!', 'success')
      } else {
        await api.post('/budgets', payload)
        showToast('Orçamento criado com sucesso!', 'success')
      }
      await fetchData()
      return true
    } catch (error) {
      showToast(parseError(error), 'error')
      return false
    }
  }

  async function deleteBudget(id: number) {
    const isConfirmed = await showConfirm('Excluir Orçamento', 'Tem certeza? Todas as despesas associadas também serão afetadas.')
    if (!isConfirmed) return
    try {
      await api.delete(`/budgets/${id}`)
      showToast('Orçamento excluído com sucesso!', 'success')
      await fetchData()
    } catch (error) { showToast(parseError(error), 'error') }
  }

  async function saveExpense(payload: ExpensePayload) {
    try {
      if (payload.id) {
        await api.put(`/expenses/${payload.id}`, payload)
        showToast('Despesa atualizada com sucesso!', 'success')
      } else {
        await api.post('/expenses', payload)
        showToast('Despesa registrada com sucesso!', 'success')
      }
      await fetchData()
      return true
    } catch (error) {
      showToast(parseError(error), 'error')
      return false
    }
  }

  async function deleteExpense(id: number) {
    const isConfirmed = await showConfirm('Excluir Despesa', 'Tem certeza que deseja excluir este registro?')
    if (!isConfirmed) return;
    try {
      await api.delete(`/expenses/${id}`)
      showToast('Despesa excluída com sucesso!', 'success')
      await fetchData()
    } catch (error) { showToast(parseError(error), 'error') }
  }

  onMounted(fetchData)

  return {
    loading,
    budgetsWithSummary,
    shoppingItems,
    fetchData,
    saveBudget,
    deleteBudget,
    saveExpense,
    deleteExpense
  }
}
