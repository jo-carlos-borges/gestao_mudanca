import type { ShoppingItem } from './Shopping'
import type { User } from './User'

export interface Budget {
  id: number
  name: string
  totalAmount: number
}

export interface Expense {
  id: number
  description: string
  amount: number
  date: string
  budget: Budget
  shoppingItem: ShoppingItem | null
  user: User
}
