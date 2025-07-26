import type { User } from "./User"

export type TaskStatus = 'PENDING' | 'IN_PROGRESS' | 'COMPLETED'
export type TaskPriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT'
export type TaskCategory = 'BEFORE_MOVE' | 'DURING_MOVE' | 'AFTER_MOVE'

export interface Task {
  id: number
  title: string
  description: string
  status: TaskStatus
  priority: TaskPriority
  deadline: string
  category: TaskCategory
  owner: User
  responsibleUser: User | null
}
