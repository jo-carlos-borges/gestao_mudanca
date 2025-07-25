import { AxiosError } from 'axios'
import type { ApiErrorResponse } from '@/models/ApiErrorResponse'

export function parseError(error: unknown): string {
  if (isAxiosError(error) && error.response) {
    const data = error.response.data as ApiErrorResponse

    if (Array.isArray(data.errors) && data.errors.length > 0) {
      return data.errors.map((e) => `• ${e}`).join('\n')
    }

    if (data.message) {
      return data.message
    }

    return `Erro ${data.status || error.response.status}: ${data.error || 'Erro desconhecido'}`
  }

  if (error instanceof Error) {
    return error.message
  }

  return String(error)
}

function isAxiosError(payload: unknown): payload is AxiosError {
  return (payload as AxiosError)?.isAxiosError
}
